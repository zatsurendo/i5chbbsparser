package com.ranc.i5bbsparser;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.ranc.i5bbsparser.domain.components.BbsParser;
import com.ranc.i5bbsparser.domain.components.I5chParser;
import com.ranc.i5bbsparser.domain.components.processor.AddUrlProcessor;
import com.ranc.i5bbsparser.domain.components.processor.BatchProcessor;
import com.ranc.i5bbsparser.domain.components.processor.DownloadProcessor;
import com.ranc.i5bbsparser.domain.model.BbsThread;

@Configuration
public class ApplicationConfig {
    
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BbsParser bbsI5chParser() {
        return new I5chParser();
    }

    @Bean
    @Scope( value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BbsParser bbsI5chParserWithThread(BbsThread bbsThread) {
        return new I5chParser(bbsThread);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BatchProcessor addUrlProcessor(String url) {
        return new AddUrlProcessor(url);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BatchProcessor downloadProcessorWithUrl(String url) {
        return new DownloadProcessor(url);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BatchProcessor downloadProcessor() {
        return new DownloadProcessor();
    }

}
