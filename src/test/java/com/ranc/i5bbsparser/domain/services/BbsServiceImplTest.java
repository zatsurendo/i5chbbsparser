package com.ranc.i5bbsparser.domain.services;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ranc.i5bbsparser.domain.model.Bbs;

@SpringBootTest
@Transactional
public class BbsServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(BbsServiceImpl.class);
    
    @Autowired
    BbsService srv;

    @Test
    public void testFindAll() {
        testSave();
    }

    @Test
    public void testFindById() {
        Bbs bbs = new Bbs("https://some.bbs.com");
        bbs.setType("bbs.com");
        Bbs bbs2 = srv.save(bbs);
        srv.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
        System.out.println("RESULT: " + srv.findById(bbs2.getId()));
    }

    @Test
    public void testSave() {
        Bbs bbs = new Bbs("https://some.bbs.com");
        bbs.setType("bbs.com");
        srv.save(bbs);
        srv.findAll().forEach(t -> log.info("RESULT: " + t.toString()));
    }

    @Test
    public void testSaveAll() {
        Bbs b1 = new Bbs("https://some.bbs.com");
        b1.setType("bbs.com");
        Bbs b2 = new Bbs("https://any.bbs.jp");
        b2.setType("bbs.jp");
        srv.saveAll(List.of(b1, b2));
        srv.findAll().forEach(t -> log.info("RESULT: " + t.toString()));
    }

    @Test
    public void testUpdate() {
        Bbs b1 = new Bbs("https://some.bbs.com");
        b1.setType("bbs.com");
        Bbs b2 = srv.save(b1);
        srv.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
        b2.setHost("any.bbs.com");
        srv.update(b2);
        srv.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
    }

    @Test
    public void testUpdateAll() {
        Bbs b1 = new Bbs("https://some.bbs.com");
        b1.setType("bbs.com");
        Bbs b2 = new Bbs("https://any.bbs.jp");
        b2.setType("bbs.jp");
        Bbs b3 = srv.save(b1);
        Bbs b4 = srv.save(b2);
        srv.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
        b3.setHost("https://i5ch.com/");
        b4.setHost("https://google.com/");
        srv.updateAll(List.of(b3, b4));
        srv.findAll().forEach(t -> System.out.println("RESULT: " + t.toString()));
    }

    @Test
    public void testFindByUnique() {
        Bbs b1 = new Bbs("https://some.bbs.com");
        b1.setType("bbs.com");
        Bbs b2 = new Bbs("https://any.bbs.jp");
        b2.setType("bbs.jp");
        srv.saveAll(List.of(b1, b2));
        Bbs b3 = srv.findByUnique("https://some.bbs.com");
        System.out.println("RESULT: " + b3.toString());
    }

    @Test
    public void testExists() {
        Bbs b1 = new Bbs("https://some.bbs.com");
        b1.setType("bbs.com");
        Bbs b2 = new Bbs("https://any.bbs.jp");
        b2.setType("bbs.jp");
        srv.saveAll(List.of(b1, b2));
        assertTrue(srv.existsByUnique("https://some.bbs.com"));
        assertFalse(srv.existsByUnique("https://some.bbs.jp"));
    }

}
