package com.ranc.i5bbsparser.domain.services;

import com.ranc.i5bbsparser.domain.model.BbsThread;

public interface BbsThreadService extends BaseRepositoryService<BbsThread> {
    BbsThread findByUrl(String url);
    BbsThread addUrl(String url);
    BbsThread saveOrUpdate(BbsThread bbsThread);
    boolean existsByUrl(String url);
}
