package com.ranc.i5bbsparser.domain.components;

import java.net.URI;
import java.net.URISyntaxException;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.ranc.i5bbsparser.domain.components.FileDownloadBbsParser.FileOverwritePolicy;
import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class I5chParserTest {

    private static final Logger log = LoggerFactory.getLogger(I5chParserTest.class);

    @Autowired
    BbsParserFactory bbsParserFactory;
    @Autowired
    BbsThreadService bbsThreadService;

    @Test
    public void testI5chParser() {
        String url = "https://c.5chan.jp/AMl1Hx5rib?p=1";
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
        BbsParser bbsParser = bbsParserFactory.getParser(uri);
        Assert.isTrue(bbsParser instanceof I5chParser, "null");
    }

    @Test
    public void parseTest() {
        String url = "https://e.5chan.jp/Yk1UoOENBe";
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
        BbsParser bbsParser = bbsParserFactory.getParser(uri);
        ((I5chParser) bbsParser).setFileOverwritePolicy(FileOverwritePolicy.OVERWRITE);
        int result = bbsParser.parse(url);
        log.info("::parseTest(): result = {}", result);
    }

    @Test
    @Transactional
    public void testParseBbsThread() {
        String url = "https://c.5chan.jp/AMl1Hx5rib";
        BbsThread bbsThread = bbsThreadService.addUrl(url);
        log.info("::testParseBbsThread - {}", bbsThread.toString());
        BbsParser parser = bbsParserFactory.getParser(bbsThread);
        ((I5chParser) parser).setFileOverwritePolicy(FileOverwritePolicy.OVERWRITE);
        parser.parse();
    }

    @Test
    public void testBaseUrl() throws Exception {
        String url = "https://c.5chan.jp/AMl1Hx5rib?p=7";
        I5chParser parser = new I5chParser();
    }

    @Test
    public void nextPageTest() throws Exception {
        //String url = "https://d.5chan.jp/oCxXyjvOhQ";
        String url = "https://c.5chan.jp/AMl1Hx5rib";
        //String url = "https://d.5chan.jp/U7yiiYge6K?p=2";
        Document doc = Jsoup.connect(url).get();
        Elements pageLinks = doc.select(".page span");
        if (pageLinks == null) {
            return;
        }
        System.out.println(pageLinks.isEmpty() ? "Empty" : pageLinks.size());
        System.out.println(pageLinks.toString());

        Element link = pageLinks.get(1).selectFirst("a");
        String href = link.attr("href");
        System.out.println(href);
        System.out.println(href.replaceFirst("^\\.", ""));
        href = href.replaceFirst("^\\.", "");
        System.out.println(href.replaceFirst("^\\.", ""));
    }


    @Test
    public void convertDatetimeStringTest() {
        String dateTime = "2023/09/04(月) 21:47:00";
        // System.out.println(parser.convertDatetimeString(dateTime));
    }
}
