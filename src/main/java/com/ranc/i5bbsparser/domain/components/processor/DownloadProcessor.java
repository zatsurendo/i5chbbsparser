package com.ranc.i5bbsparser.domain.components.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ranc.i5bbsparser.common.util.ParserUtil;
import com.ranc.i5bbsparser.domain.components.BbsParser;
import com.ranc.i5bbsparser.domain.components.BbsParserFactory;
import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

public class DownloadProcessor extends AbstractBatchProcessor {

    private static final Logger log = LoggerFactory.getLogger(DownloadProcessor.class);
    @Autowired
    BbsParserFactory bbsParserFactory;
    @Autowired
    BbsThreadService bbsThreadService;

    public DownloadProcessor() {
        // バッチダウンロード用のインスタンス
        super(null);
    }

    public DownloadProcessor(String url) {
        // その場限りのダウンロード用のインスタンス
        super(url);
    }

    @Override
    public int execute() {
        if (this.url == null) {
            // URL が Null の場合、バッチダウンロード
            log.info("start batch downloading:");
            return batch();
        }
        // URL が Null 出ない場合、ワンタイムダウンロード
        log.info("start downloading from {}", url);
        return runOnce();
    }

    private int batch() {
        int resultCount = 0;
        
        for (BbsThread thread : bbsThreadService.findAll()) {
            log.info("::execute(): start batch process {}", thread.toString());
            BbsParser parser = bbsParserFactory.getParser(thread);
            resultCount = parser.parse();
        }
        System.out.println(MessageFormatter.format("Downloaded {} Files.", resultCount));
        return resultCount;
    }

    private int runOnce() {
        int resultCount = 0;
        String typeSpec = ParserUtil.hostPartOf(url);
        BbsParser parser = bbsParserFactory.getParser(typeSpec);
        resultCount =  parser.parse(url);
        return resultCount;
    }
}
