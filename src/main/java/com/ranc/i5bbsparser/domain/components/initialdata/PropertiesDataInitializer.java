package com.ranc.i5bbsparser.domain.components.initialdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ranc.i5bbsparser.domain.components.config.PersistentDataProperties;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

@Component("propertiesDataInitializer")
public class PropertiesDataInitializer implements DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(PropertiesDataInitializer.class);

    @Autowired
    BbsThreadService bbsThreadService;
    @Autowired
    PersistentDataProperties properties;
    @Override
    public int dataInit() {
        int count = 0;
        for (String s : properties.getInitialUrl()) {
            log.info("::dataInit() - add thread url {}.", s);
            bbsThreadService.addUrl(s);
            count++;
        }
        return count;
    }
}
