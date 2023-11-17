package com.ranc.i5bbsparser.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ranc.i5bbsparser.domain.model.BbsThread;

public interface BbsThreadRepository extends CrudRepository<BbsThread, Long> {
    Optional<BbsThread> findByUrl(String url);
    boolean existsByUrl(String url);
}
