package com.ranc.i5bbsparser.domain.components;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import com.ranc.i5bbsparser.common.util.ParserUtil;
import com.ranc.i5bbsparser.domain.components.processor.AddUrlProcessor;
import com.ranc.i5bbsparser.domain.components.processor.BatchProcessor;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

@Component
public class OptionProcessorImpl implements OptionProcessor{

	private static final Logger log = LoggerFactory.getLogger(OptionProcessorImpl.class);

    @Autowired
    BbsThreadService bbsThreadService;
	@Autowired
	BbsParserFactory parserFactory;
	@Autowired
	BeanFactory beanFactory;

	@Lookup("addUrlProcessor")
	public BatchProcessor getAddUrlProcessor(String url) {
		return null;
	}
	@Lookup("downloadProcessorWithUrl")
	public BatchProcessor getDownloadProcessor(String url) {
		return null;
	}
	@Lookup("downloadProcessor")
	public BatchProcessor getDonloadProcessor() {
		return null;
	}

	private Map<Integer, BatchProcessor> processorMap = new TreeMap<>();
	
	private void printUsage() {
		System.out.println("USAGE: ");
	}

    @Override
    public int process(String... args) {
        log.info("::process() - start process..");
		for (int i = 0; i < args.length; i++) {
			log.info("args[{}]: {}", i, args[i]);
		}
		if (args == null || args.length == 0) {
			printUsage();
		}
		int i = 0;
		while(i < args.length) {
			String s = args[i];
			String urlSpec = "";
			log.debug("::process() - now processing args[{}] {}", i, args[i]);
			switch(s) {
				case "-a":
				case "--add":
					urlSpec = args[++i];
					if (!ParserUtil.isValidUrl(urlSpec)) {
						log.error("invalid url {}", urlSpec);
						printUsage();
						return -1;
					}
					BatchProcessor processor = getAddUrlProcessor(urlSpec);
					processorMap.put(1, processor);
					break;
				case "-e":
				case "--execute":
					processorMap.put(99, getDonloadProcessor());
					break;
				case "-i":
				case "--init":
					
					urlSpec = args[++i];
					if (!ParserUtil.isValidUrl(urlSpec)) {
						log.error("invalid url {}", urlSpec);
						printUsage();
						return -1;
					}
					BatchProcessor initialProcessor = getAddUrlProcessor(urlSpec);
					((AddUrlProcessor) initialProcessor).setInitialize(true);
					processorMap.put(20, initialProcessor);
					break;
				case "--download-only":
					urlSpec = args[++i];
					if (!ParserUtil.isValidUrl(urlSpec)) {
						log.error("invalid url {}", urlSpec);
						printUsage();
						return -1;
					}
					BatchProcessor oneTimeProcessor = getDownloadProcessor(urlSpec);
					processorMap.put(98, oneTimeProcessor);
					break;
				case "-?":
				case "--help":
					printUsage();
					return 0;
				default:
					printUsage();
					return -1;
			}
			i++;
		}

		for (Integer index : processorMap.keySet()) {
			processorMap.get(index).execute();
		}
		return 0;
    }

}
