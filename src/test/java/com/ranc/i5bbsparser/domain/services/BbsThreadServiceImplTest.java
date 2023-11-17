package com.ranc.i5bbsparser.domain.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.model.BbsThread;

@SpringBootTest
public class BbsThreadServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(BbsThreadServiceImplTest.class);

    @Autowired
    BbsService bbsService;
    @Autowired
    BbsThreadService bbsThreadService;

    @Test
    public void findAll() {
        bbsThreadService.findAll().forEach(System.out::println);
    }

    @Test
    public void testSave() {
        registBbs();
        Bbs b1 = bbsService.findByUnique("https://any.bbs.jp");
        BbsThread t1 = new BbsThread("osaka", true);
        t1.setBbs(b1);
        BbsThread t2 = bbsThreadService.save(t1);
        bbsThreadService.findAll().forEach(t -> System.out.println("FINDALL: " + t.toString()));
    }

    @Test
    public void testSaveAll() {
        registBbs();
        Bbs b1 = bbsService.findByUnique("https://some.bbs.com");
        Bbs b2 = bbsService.findByUnique("https://any.bbs.jp");
        BbsThread t1 = new BbsThread("osaka", true);
        t1.setBbs(b1);
        BbsThread t2 = new BbsThread("kobe", true);
        t2.setBbs(b1);
        BbsThread t3 = new BbsThread("japan", true);
        t3.setBbs(b2);
        bbsThreadService.saveAll(List.of(t1, t2, t3));
        bbsThreadService.findAll().forEach(t -> System.out.println("FINDALL: " + t.toString()));
    }

    @Test
    public void testUpdate() {
        registBbs();
        Bbs b1 = bbsService.findByUnique("https://some.bbs.com");
        Bbs b2 = bbsService.findByUnique("https://any.bbs.jp");
        BbsThread t1 = new BbsThread("osaka", true);
        t1.setBbs(b1);
        BbsThread t2 = new BbsThread("kobe", true);
        t2.setBbs(b1);
        BbsThread t3 = new BbsThread("japan", true);
        t3.setBbs(b2);
        bbsThreadService.saveAll(List.of(t1, t2, t3));
        bbsThreadService.findAll().forEach(t -> System.out.println("INITIAL: " + t.toString()));
        BbsThread t4 = bbsThreadService.findByUnique("osaka");
        t4.setUrl("kyoto");
        bbsThreadService.update(t4);
        bbsThreadService.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
    }

    @Test
    public void testUpdateAll() {
        registBbs();
        Bbs b1 = bbsService.findByUnique("https://some.bbs.com");
        Bbs b2 = bbsService.findByUnique("https://any.bbs.jp");
        BbsThread t1 = new BbsThread("osaka", true);
        t1.setBbs(b1);
        BbsThread t2 = new BbsThread("kobe", true);
        t2.setBbs(b1);
        BbsThread t3 = new BbsThread("japan", true);
        t3.setBbs(b2);
        List<BbsThread> tt = new ArrayList<>();
        bbsThreadService.saveAll(List.of(t1, t2, t3));
        bbsThreadService.findAll().forEach(t -> System.out.println("INITIAL: " + t.toString()));
        BbsThread t4 = bbsThreadService.findByUnique("osaka");
        BbsThread t5 = bbsThreadService.findByUnique("japan");
        t4.setUrl("sapporo");
        t5.setUrl("america");
        bbsThreadService.updateAll(List.of(t4, t5));
        bbsThreadService.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
    }

    @Test
    public void testFindByUrl() {
        Assert.isTrue(bbsThreadService.existsByUrl("https://c.5chan.jp/rItjxRKF5d"), "not found.");
        Assert.isTrue(!bbsThreadService.existsByUrl("https://d.5chan.jp/x4Jn1Gza7z"), "exists");
    }

    @Test
    public void testAddUrl() {
        String[] urls = {"https://c.5chan.jp/rItjxRKF5d", "https://e.5chan.jp/DDCfWlWZ7v", "https://c.5chan.jp/AMl1Hx5rib"};
        Arrays.stream(urls).forEach(bbsThreadService::addUrl);
        bbsThreadService.findAll().forEach(System.out::println);
        bbsService.findAll().forEach(System.out::println);
    }

    @Test
    public void testUpdateOrSave() {
        bbsThreadService.findAll().forEach(t -> log.info("::testUpdateOrSave(): {}", t.toString()));
        BbsThread thread = bbsThreadService.findByUrl("https://c.5chan.jp/rItjxRKF5d");
        thread.setLastParsingPostNo(21L);
        bbsThreadService.saveOrUpdate(thread);
        thread.setUrl("https://d.5chan.jp/x4Jn1Gza7z");
        thread.setId(null);
        bbsThreadService.saveOrUpdate(thread);
        bbsThreadService.findAll().forEach(t -> log.info("::testUpdateOrSave(): {}", t.toString()));
    }

    private void registBbs() {
        Bbs b1 = new Bbs("https://some.bbs.com");
        b1.setType("bbs.com");
        Bbs b2 = new Bbs("https://any.bbs.jp");
        b2.setType("bbs.jp");
        bbsService.saveAll(List.of(b1, b2));
        bbsService.findAll().forEach(t -> System.out.println("SAVEALL: " + t.toString()));
    }
}
