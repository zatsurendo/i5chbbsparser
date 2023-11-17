package com.ranc.i5bbsparser.domain.services;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.net.InternetDomainName;
import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.model.BbsThread;

@Service
public class BbsHelperImpl implements BbsHelper {

    private static final Logger log = LoggerFactory.getLogger(BbsHelperImpl.class);

    @Autowired
    BbsService bbsService;
    @Autowired
    BbsThreadService bbsThreadService;

    @Override
    public BbsThread addBbs(String spec) throws Exception {
        
        URI uri = null;
        String host = null;
        String path = null;
        // String query = null;
        String domain = null;

        try {
            uri = new URI(spec);
            host = uri.getHost();
            path = uri.getPath();
            // query = uri.getQuery();
            log.info("host: {}, path: {}", host, path);
        } catch (URISyntaxException e) {
            log.error("{} is not uri.", spec);
            throw e;
        }

        try {
            InternetDomainName internetDomainName = InternetDomainName.from(host).topPrivateDomain();
            domain = internetDomainName.toString();
            log.info("domain: {}, host: {}, path: {}", domain, host, path);
        } catch (IllegalArgumentException e) {
            domain = host;
        }

        Bbs bbs = new Bbs(host);
        bbs.setType(domain);
        bbs = bbsService.save(bbs);
        log.info("Bbs {}", bbs.toString());

        BbsThread bbsThread = new BbsThread(spec, true);
        bbsThread.setBbs(bbs);
        bbsThread = bbsThreadService.save(bbsThread);
        log.info("BbsThread {}", bbsThread.toString());
        return bbsThread;
    }
}
