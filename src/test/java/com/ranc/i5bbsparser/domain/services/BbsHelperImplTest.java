package com.ranc.i5bbsparser.domain.services;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
public class BbsHelperImplTest {

    @Autowired
    BbsHelper bbsHelper;

    @Test
    public void addBbsTest() throws Exception {
        bbsHelper.addBbs("https://e.5chan.jp/DDCfWlWZ7v");
    }
}
