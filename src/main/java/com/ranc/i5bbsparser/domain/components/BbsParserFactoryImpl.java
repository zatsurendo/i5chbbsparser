package com.ranc.i5bbsparser.domain.components;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.model.BbsThread;

@Component
public class BbsParserFactoryImpl implements BbsParserFactory {

    private static final Logger log = LoggerFactory.getLogger(BbsParserFactoryImpl.class);

    @Lookup("bbsI5chParser")
    BbsParser bbsi5BbsParser() {
        return null;
    }
    @Lookup("bbsI5chParserWithThread")
    BbsParser bbsi5BbsParser(BbsThread bbsThread) {
        return null;
    }

    @Override
    public BbsParser getParser(String typeSpec) {
        if (typeSpec.endsWith("5chan.jp")) {
            return bbsi5BbsParser();
        }
        log.error("::getParser::Bbs type {} is not supported", typeSpec);
        throw new IllegalArgumentException(MessageFormatter.format("::getParser::Bbs type {} is not supported", typeSpec).getMessage());
    }

    @Override
    public BbsParser getParser(BbsThread bbsThread) {
        String typeSpec = bbsThread.getBbs().getType();
        if (typeSpec.endsWith("5chan.jp")) {
            return bbsi5BbsParser(bbsThread);
        }
        log.error("::getParser::Bbs type {} is not supported", typeSpec);
        throw new IllegalArgumentException(MessageFormatter.format("::getParser::Bbs type {} is not supported", typeSpec).getMessage());
    }

    @Override
    public BbsParser getParser(Bbs bbs) {
        String typeSpec = bbs.getType();
        return getParser(typeSpec);
    }

    @Override
    public BbsParser getParser(URI uri) {
        String uriHost = uri.getHost();
        return getParser(uriHost);
    }
}
