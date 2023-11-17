package com.ranc.i5bbsparser.domain.components.processor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class AbstractBatchProcessor implements BatchProcessor {
    
    @Setter
    @Getter
    protected String url = null;
    
    public AbstractBatchProcessor(String url) {
        this.url = url;
    }
}
