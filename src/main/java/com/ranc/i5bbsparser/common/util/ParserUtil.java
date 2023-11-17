package com.ranc.i5bbsparser.common.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserUtil {

    private static final Logger log = LoggerFactory.getLogger(ParserUtil.class);
    
	public static String hostPartOf(String url) {

		try {
			URI uri = new URL(url).toURI();
			return uri.getHost(); 
		} catch (URISyntaxException e) {
            log.error("::hostPartOf(): caught exception while checking url {}, message = {}", url, e.getMessage());
			throw new IllegalArgumentException("");
		} catch (MalformedURLException e) {
            log.error("::hostPartOf(): caught exception while checking url {}, message = {}", url, e.getMessage());
			throw new IllegalArgumentException("");
		}
		
	}

    public static boolean isValidUrl(String url) {

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
