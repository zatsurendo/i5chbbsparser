package com.ranc.i5bbsparser.domain.components.processor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ranc.i5bbsparser.domain.components.BbsParser;
import com.ranc.i5bbsparser.domain.components.BbsParserFactory;
import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

import lombok.Getter;
import lombok.Setter;

public class AddUrlProcessor extends AbstractBatchProcessor {

    private static final Logger log = LoggerFactory.getLogger(AddUrlProcessor.class);
    @Setter
    @Getter
    private boolean initialize = false;

    @Autowired
    public BbsThreadService bbsThreadService;
    @Autowired
    private BbsParserFactory bbsParserFactory;


    public AddUrlProcessor() {}
    public AddUrlProcessor(String url) {
        super(url);
    }

    @Override
    public int execute() {

        int returnValue = 0;
        if (url == null || url.isEmpty()) {
            System.err.println("URL Not specified.");
            return -1;
        }
        if (!isValidUrl(url)) {
            System.err.println(MessageFormatter.format("Specific URL({}) is invalid.", url));
            return -1;
        }

        BbsThread t1 = bbsThreadService.addUrl(this.url);
        if (t1 == null) {
            System.err.println("ERROR: Specified URL is already specified.");
            return -1;
        }

        System.out.println(MessageFormatter.format("Registered 1 thread {}.", t1.toString()));

        if (initialize) {
            returnValue = initialParse();
        }
        return returnValue;
    }

    private int initialParse() {
        BbsThread bbsThread = bbsThreadService.findByUrl(url);
        if (bbsThread == null) {
            log.warn("::initalized() - specUrl already registered. bbsThread = {}", bbsThread);
            return -1;
        }
        log.debug("::initialize() - bbsThread = {}", bbsThread);
        BbsParser parser = bbsParserFactory.getParser(bbsThread);
        int returnValue = parser.parse();
        return returnValue;
    }

	private boolean isValidUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (URISyntaxException e) {
            log.error("::isValidUrl(): caught exception while checking url {}, message = {}", url, e.getMessage());
			return false;
		} catch (MalformedURLException e) {
            log.error("::isValidUrl(): caught exception while checking url {}, message = {}", url, e.getMessage());
			return false;
		}
	}
}
