package com.ranc.i5bbsparser.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Post {
    private long no;
    private String url;
    private String postId;
    private String name;
    private String trip;
    private LocalDateTime dateTime;
    private String comment;
    private List<PostImage> postImages;
}
