package com.ranc.i5bbsparser.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ranc.i5bbsparser.domain.model.Bbs;

public interface BbsRepository extends CrudRepository<Bbs, Long> {
    Optional<Bbs> findByHost(String bbsUrl);
    boolean existsByHost(String bbsUrl);
}
