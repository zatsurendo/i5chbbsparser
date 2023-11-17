package com.ranc.i5bbsparser.domain.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@ToString
@Entity
@Table(name = "bbs")
@EntityListeners(AuditingEntityListener.class)
public class Bbs {
    
    @Id
    @GeneratedValue(generator = "bbs_generator")
    @GenericGenerator(
        name = "bbs_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "bbs_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    private Long id;
    @EqualsAndHashCode.Include
    @Column(nullable = false, length = 255, unique = true)
    private String host;
    @Column(nullable = false, length = 255)
    private String type;
    @OneToMany(mappedBy = "bbs", fetch = FetchType.EAGER)
    private Set<BbsThread> bbsThreads;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    public Bbs(String host) {
        this.host = host;
    }
}
