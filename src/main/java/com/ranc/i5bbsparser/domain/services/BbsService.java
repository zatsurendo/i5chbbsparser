package com.ranc.i5bbsparser.domain.services;

import com.ranc.i5bbsparser.domain.model.Bbs;

public interface BbsService extends BaseRepositoryService<Bbs> {
    Bbs findByBbsUrl(String bbsUrl);
    boolean existsByBbsUrl(String bbsUrl);
}
