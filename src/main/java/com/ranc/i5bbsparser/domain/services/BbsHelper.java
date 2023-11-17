package com.ranc.i5bbsparser.domain.services;

import com.ranc.i5bbsparser.domain.model.BbsThread;

public interface BbsHelper {
    BbsThread addBbs(String url) throws Exception;
}
