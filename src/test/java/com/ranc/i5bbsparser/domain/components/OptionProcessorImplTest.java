package com.ranc.i5bbsparser.domain.components;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionProcessorImplTest {

    private static final Logger log = LoggerFactory.getLogger(OptionProcessor.class);
    
    public static int ANY = 1;
    public static int FINALLY = 99;

    @Test
    public void testProcess() {

        String[] args = {"-a", "https://hoge.com", "-i", "https://fuga.com/", "-e", "-f"};
        process(args);
        String[] args2 = {"-a", "album", "-e"};
        process(args2);
        String[] args3 = {"-a", "-b"};
        process(args3);
    }

    private void process(String[] args) {
        Map<Integer, String> com = new TreeMap<>();
        int i = 0;
        while(i < args.length) {
			String s = args[i];
			log.debug("::process() - now processing args[{}] {}", i, args[i]);
			switch(s) {
				case "-a":
				case "--add":
					String bbsThreadUrl = args[++i];
                    if (!isValidUrl(bbsThreadUrl)) {
                        System.out.println(bbsThreadUrl + " is not URL. process end.");
                        break;
                    }
                    com.put(2, "add url " + bbsThreadUrl);
					break;
				case "-e":
				case "--execute":
					com.put(FINALLY, "execute");
					break;
				case "-i":
				case "--init":
					String oneTimeThreadUrl = args[++i];
                    if (!isValidUrl(oneTimeThreadUrl)) {
                        System.out.println(oneTimeThreadUrl + " is not URL. process end.");
                        break;
                    }
					com.put(1, "init parse " + oneTimeThreadUrl);
					break;
				case "-?":
				case "--help":
					log.info("usage:");
					break;
				default:
                    log.info("unknown option - {}", s);
                    log.info("usage:");
                    break;
			}
			i++;
		}
        System.out.println(com.toString());
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
