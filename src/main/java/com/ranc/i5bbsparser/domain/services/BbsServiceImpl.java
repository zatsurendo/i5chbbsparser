package com.ranc.i5bbsparser.domain.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.repository.BbsRepository;

@Service
public class BbsServiceImpl implements BbsService {

    private static final Logger log = LoggerFactory.getLogger(BbsServiceImpl.class);

    @Autowired
    private BbsRepository rep;

    @Override
    public Iterable<Bbs> findAll() {
        return rep.findAll();
    }

    @Override
    public Bbs findById(Long id) {
        Optional<Bbs> optional = rep.findById(id);
        return optional.isPresent() ? optional.get() : null;
    }

    @Transactional
    @Override
    public Bbs save(Bbs bbs) {
        Assert.isNull(bbs.getId(), "bbs.id should be null.: " + bbs.toString());
        return rep.save(bbs);
    }

    @Transactional
    @Override
    public Iterable<Bbs> saveAll(Iterable<Bbs> entities) {
        return rep.saveAll(entities);
    }

    @Transactional
    @Override
    public Bbs update(Bbs bbs) {
        Assert.notNull(bbs.getId(), "bbs.id must not be null: " + bbs.toString());
        Bbs current = rep.findById(bbs.getId()).get();
        if (current == null) 
            throw new EntityNotFoundException("specified bbs is not exist: " + bbs.toString());
        if (Objects.nonNull(bbs.getHost()) && !"".equalsIgnoreCase(bbs.getHost())) {
            current.setHost(bbs.getHost());
        }
        if (Objects.nonNull(bbs.getType()) && !"".equalsIgnoreCase(bbs.getType())) {
            current.setType(bbs.getType());
        }
        return rep.save(current);
    }

    @Transactional
    @Override
    public Iterable<Bbs> updateAll(Iterable<Bbs> entities) {
        List<Bbs> currents = new ArrayList<>();
        for (Bbs bbs : entities) {
            Bbs current = rep.findById(bbs.getId()).get();
            if (current == null) 
                throw new EntityNotFoundException("specified bbs is not exist: " + bbs.toString());
                if (Objects.nonNull(bbs.getHost()) && !"".equalsIgnoreCase(bbs.getHost())) {
                    current.setHost(bbs.getHost());
                }
                if (Objects.nonNull(bbs.getType()) && !"".equalsIgnoreCase(bbs.getType())) {
                    current.setType(bbs.getType());
                }
                currents.add(current);
        }
        return rep.saveAll(currents);
    }

    @Transactional
    @Override
    public void delete(Bbs entity) {
        rep.delete(entity);
    }

    @Override
    public Bbs findByUnique(String unique) {
        Optional<Bbs> opt = rep.findByHost(unique);
        return opt.isPresent() ? opt.get() : null;
    }

    @Override
    public boolean existsByUnique(String unique) {
        return rep.existsByHost(unique);
    }

    @Override
    public Bbs findByBbsUrl(String bbsUrl) {
        Optional<Bbs> opt = rep.findByHost(bbsUrl);
        return opt.isPresent() ? opt.get() : null;
    }

    @Override
    public boolean existsByBbsUrl(String bbsUrl) {
        return rep.existsByHost(bbsUrl);
    }
    
}
