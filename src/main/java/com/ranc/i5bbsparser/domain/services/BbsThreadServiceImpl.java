package com.ranc.i5bbsparser.domain.services;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.repository.BbsThreadRepository;

@Service
public class BbsThreadServiceImpl implements BbsThreadService {

    private static final Logger log = LoggerFactory.getLogger(BbsThreadServiceImpl.class);

    @Autowired
    public BbsService bbsService;
    @Autowired
    private BbsThreadRepository rep;

    @Override
    public Iterable<BbsThread> findAll() {
        return rep.findAll();
    }

    @Override
    public BbsThread findById(Long id) {
        Optional<BbsThread> opt = rep.findById(id);
        return opt.isPresent() ? opt.get() : null;
    }

    @Transactional
    @Override
    public BbsThread save(BbsThread entity) {
        Assert.isNull(entity.getId(), "bbsThread.id should be null:" + entity.toString());
        return rep.save(entity);
    }

    @Transactional
    @Override
    public Iterable<BbsThread> saveAll(Iterable<BbsThread> entities) {
        List<BbsThread> currents = new ArrayList<>();
        for (BbsThread e : entities) {
            Assert.isNull(e.getId(), "bbsThread.id shoud be null" + e.toString());
            currents.add(e);
        }
        return rep.saveAll(currents);
    }

    @Override
    public BbsThread update(BbsThread entity) {
        Assert.notNull(entity.getId(), "bbsThread.id must not be null: " + entity.toString());
        BbsThread current = rep.findById(entity.getId()).get();
        if (current == null) 
            throw new EntityNotFoundException("specified bbs is not exist: " + entity.toString());
        return rep.save(copyPropsToCurrent(entity, current));
    }

    @Override
    public Iterable<BbsThread> updateAll(Iterable<BbsThread> entities) {
        List<BbsThread> threads = new ArrayList<>();
        for (BbsThread entity : entities) {
            Assert.notNull(entity.getId(), "bbsThread.id must not be null: " + entity.toString());
            BbsThread current = rep.findById(entity.getId()).get();
            if (current == null)
                throw new EntityNotFoundException("specified bbs is not exist: " + entity.toString());
            threads.add(copyPropsToCurrent(entity, current));
        }
        return rep.saveAll(threads);
    }

    /**
     * copy properties from entity to current record.
     * @param entity
     * @param current
     * @return
     */
    private BbsThread copyPropsToCurrent(BbsThread entity, BbsThread current) {
        
        if (Objects.nonNull(entity.getBbs())) {
            current.setBbs(entity.getBbs());
        }
        if (Objects.nonNull(entity.getUrl()) && !"".equalsIgnoreCase(entity.getUrl())) {
            current.setUrl(entity.getUrl());
        }
        if (Objects.nonNull(entity.getLastParsingDateTime())) {
            current.setLastParsingDateTime(entity.getLastParsingDateTime());
        }
        if (Objects.nonNull(entity.getLastParsingPostNo())) {
            current.setLastParsingPostNo(entity.getLastParsingPostNo());
        }
        if (Objects.nonNull(entity.getEnabled())) {
            current.setEnabled(entity.getEnabled());
        }
        if (Objects.nonNull(entity.getTitle())) {
            current.setTitle(entity.getTitle());
        }
        log.debug("::copyPropsToCurrent() - value from {}", entity);
        log.debug("::copyPropsToCurrent() - value to   {}", entity);
        return current;
    }

    @Override
    public void delete(BbsThread entity) {
        rep.delete(entity);
    }

    @Override
    public BbsThread findByUnique(String unique) {
        return findByUrl(unique);
    }

    @Override
    public boolean existsByUnique(String unique) {
        return existsByUrl(unique);
    }

    @Override
    public BbsThread findByUrl(String url) {
        return rep.findByUrl(url).get();
    }

    @Override
    public boolean existsByUrl(String url) {
        return rep.existsByUrl(url);
    }

    @Transactional
    @Override
    public BbsThread addUrl(String url) {

        try {
            URI uri = new URL(url).toURI();
            String urlHost = uri.getHost();
            String urlPath = uri.getPath();
            if (urlHost == null || urlHost.isEmpty()) {
                throw new IllegalArgumentException();
            }
            if (urlPath == null || urlPath.isEmpty()) {
                throw new IllegalArgumentException();
            }
            if (urlPath.startsWith("/")) {
                urlPath = urlPath.replaceFirst("^/", "");
            }

            Bbs bbs = new Bbs();
            bbs.setHost(urlHost);
            bbs.setType(urlHost);
            if (!bbsService.existsByBbsUrl(urlHost)) {
                bbsService.save(bbs);
            }

            Bbs bbs2 = bbsService.findByBbsUrl(urlHost);
            BbsThread thread = new BbsThread();
            thread.setBbs(bbs2);
            thread.setEnabled(true);
            thread.setTypeSpec(urlHost);
            thread.setUrl(url);
            
            if (!rep.findByUrl(thread.getUrl()).isEmpty()) {
                log.warn("::addUrl(): Specified URL is already registered. url = {}", url);
                return null;
            }

            BbsThread result = rep.save(thread);
            log.info("::addUrl(): Added successfully. bbsThread = {}", result);
            return result;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public BbsThread saveOrUpdate(BbsThread bbsThread) {
        if (existsByUrl(bbsThread.getUrl())) {
            return update(bbsThread);
        }
        return save(bbsThread);
    }
}
