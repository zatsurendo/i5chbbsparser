package com.ranc.i5bbsparser.domain.components.processor;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ranc.i5bbsparser.domain.components.OptionProcessor;
import com.ranc.i5bbsparser.domain.components.OptionProcessorImpl;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

@SpringBootTest
public class DownloadProcessorTest {

    private static final Logger log = LoggerFactory.getLogger(DownloadProcessor.class);
    
    @Autowired
    OptionProcessor optionProcessor;
    @Autowired
    BbsThreadService bbsThreadService;

    @Transactional
    @Test
    public void testExecute() {

        BatchProcessor processor = ((OptionProcessorImpl) optionProcessor).getDonloadProcessor();
        assertTrue(processor instanceof DownloadProcessor);
        processor.execute();
    }
}
