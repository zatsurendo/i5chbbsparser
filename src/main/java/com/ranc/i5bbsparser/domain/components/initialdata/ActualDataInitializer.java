package com.ranc.i5bbsparser.domain.components.initialdata;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.services.BbsService;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

@Component("actualDataInitializer")
public class ActualDataInitializer implements DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(ActualDataInitializer.class);

    @Autowired
    BbsService bbsService;
    @Autowired
    BbsThreadService bbsThreadService;

    @Transactional
    @Override
    public int dataInit() {

        Bbs b1 = new Bbs("c.5chan.jp");
        b1.setType("c.5chan.jp");
        Bbs b2 = new Bbs("e.5chan.jp");
        b2.setType("e.5chan.jp");
        b1 = bbsService.save(b1);
        b2 = bbsService.save(b2);
        log.info("::dataInit() - bbs saved {}", b1.toString());
        log.info("::dataInit() - bbs saved {}", b2.toString());

        BbsThread t1 = new BbsThread("https://c.5chan.jp/rItjxRKF5d", true);
        t1.setLastParsingPostNo(109L);
        t1.setTypeSpec("c.5chan.jp");
        t1.setBbs(b1);
        t1.setTitle("大阪雌豚熟女");
        t1 = bbsThreadService.save(t1);
        log.info("::dataInit() - bbsThread saved {}", t1.toString());

        BbsThread t2 = new BbsThread("https://e.5chan.jp/DDCfWlWZ7v", true);
        t2.setLastParsingPostNo(123L);
        t2.setTypeSpec("c.5chan.jp");
        t2.setBbs(b2);
        t2.setTitle("熟女のおしり、体２");
        t2 = bbsThreadService.save(t2);
        log.info("::dataInit() - bbsThread saved {}", t2.toString());
        
        BbsThread t3 = new BbsThread("https://c.5chan.jp/JFeh6OttuB", true);
        t3.setLastParsingPostNo(36L);
        t3.setTypeSpec("c.5chan.jp");
        t3.setBbs(b1);
        t3.setTitle("おしっこ熟女");
        t3 = bbsThreadService.save(t3);
        log.info("::dataInit() - bbsThread saved {}", t3.toString());
        
        BbsThread t4 = new BbsThread("https://e.5chan.jp/e9mElMNh6X", true);
        t4.setLastParsingPostNo(142L);
        t4.setTypeSpec("c.5chan.jp");
        t4.setBbs(b2);
        t4.setTitle("熟女のフォーマルスーツ");
        t4 = bbsThreadService.save(t4);
        log.info("::dataInit() - bbsThread saved {}", t4.toString());
        
        BbsThread t5 = new BbsThread("https://e.5chan.jp/Yk1UoOENBe", true);
        t5.setLastParsingPostNo(20L);
        t5.setTypeSpec("c.5chan.jp");
        t5.setBbs(b2);
        t5.setTitle("ときどき貸出");
        t5 = bbsThreadService.save(t5);
        log.info("::dataInit() - bbsThread saved {}", t5.toString());
        
        BbsThread t6 = new BbsThread("https://e.5chan.jp/nc0HWIhG2l", true);
        t6.setLastParsingPostNo(53L);
        t6.setTypeSpec("c.5chan.jp");
        t6.setBbs(b2);
        t6.setTitle("パイパン熟女（自由参加)");
        t6 = bbsThreadService.save(t6);
        log.info("::dataInit() - bbsThread saved {}", t6.toString());
        
        BbsThread t7 = new BbsThread("https://c.5chan.jp/0W6WAfs0hs", true);
        t7.setLastParsingPostNo(522L);
        t7.setTypeSpec("c.5chan.jp");
        t7.setBbs(b1);
        t7.setTitle("🚺店員さん・事務員さん・同僚 📸撮り");
        t7 = bbsThreadService.save(t7);
        log.info("::dataInit() - bbsThread saved {}", t7.toString());
        
        BbsThread t8 = new BbsThread("https://e.5chan.jp/hgvSkPWmPA", true);
        t8.setLastParsingPostNo(54L);
        t8.setTypeSpec("c.5chan.jp");
        t8.setBbs(b2);
        t8.setTitle("Tバック熟女(参加自由)");
        t8 = bbsThreadService.save(t8);
        log.info("::dataInit() - bbsThread saved {}", t8.toString());
        
        BbsThread t9 = new BbsThread("https://c.5chan.jp/8DTnnRUlfJ", true);
        t9.setLastParsingPostNo(334L);
        t9.setTypeSpec("c.5chan.jp");
        t9.setBbs(b1);
        t9.setTitle("デブス晒し");
        t9 = bbsThreadService.save(t9);
        log.info("::dataInit() - bbsThread saved {}", t9.toString());
        
        BbsThread ta = new BbsThread("https://c.5chan.jp/VoFpNlZu1m", true);
        ta.setLastParsingPostNo(992L);
        ta.setTypeSpec("c.5chan.jp");
        ta.setBbs(b1);
        ta.setTitle("超熟女");
        ta = bbsThreadService.save(ta);
        log.info("::dataInit() - bbsThread saved {}", ta.toString());
        return 0;
    }
}
