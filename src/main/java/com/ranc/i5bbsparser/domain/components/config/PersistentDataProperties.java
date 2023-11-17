package com.ranc.i5bbsparser.domain.components.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties("i5chbbsparser.persistent.data")
public class PersistentDataProperties {
    
    /**
     * 初期レコードを投入するかどうか<br>
     * {@code true} を指定することで、初期レコードが投入されているかどうかに関係なく、
     * レコードを挿入しようと試みます。<br>
     * true:投入する, false:投入しない
     */
    @Setter
    @Getter
    private boolean enabled = false;

    @Setter
    @Getter
    private String[] initialUrl = {};
}
