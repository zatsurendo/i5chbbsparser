package com.ranc.i5bbsparser.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PostImage {
    private String url;
    public PostImage(String url) {
        this.url = url;
    }
}
