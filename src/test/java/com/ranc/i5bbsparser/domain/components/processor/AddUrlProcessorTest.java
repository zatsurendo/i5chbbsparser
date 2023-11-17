package com.ranc.i5bbsparser.domain.components.processor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ranc.i5bbsparser.domain.components.OptionProcessor;
import com.ranc.i5bbsparser.domain.components.OptionProcessorImpl;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

@SpringBootTest
public class AddUrlProcessorTest {

    @Autowired
    OptionProcessor optionProcessor;

    @Transactional
    @Test
    public void testInitialParse() {

        BatchProcessor processor = ((OptionProcessorImpl) optionProcessor).getAddUrlProcessor("https://e.5chan.jp/nc0HWIhG2l");
        assertTrue(processor instanceof AddUrlProcessor);
        ((AddUrlProcessor) processor).setInitialize(true);
        processor.execute();
    }
}
