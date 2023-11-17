package com.ranc.i5bbsparser.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(name = "bbs_thread", uniqueConstraints = @UniqueConstraint(name = "UX_bbs_thread_url", columnNames = {"url"}))
@EntityListeners(AuditingEntityListener.class)
public class BbsThread {
    
    @Id
    @GeneratedValue(generator = "bbs_thread_generator")
    @GenericGenerator(
        name = "bbs_thread_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name", value = "bbs_thread_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    private Long id;
    /**
     * bbs url のパス部
     */
    @EqualsAndHashCode.Include
    @Column(nullable = false, length = 1024)
    private String url;
    /**
     * Bbs パーサーのタイプ（BBS URL のホスト部）
     */
    @Column(nullable = true, length = 512)
    private String typeSpec;
    @Column(nullable = true, length = 512)
    private String title;
    @Column(nullable = true)
    private LocalDateTime lastParsingDateTime;
    @Column(nullable = true)
    private Long lastParsingPostNo;
    @Column(nullable = false)
    private Boolean enabled;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "bbs_id", foreignKey = @ForeignKey(name = "FK_bbs_thread_bbs_id_bbs_id"))
    private Bbs bbs;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    public BbsThread(String url, Boolean enabled) {
        this.url = url;
        this.enabled = enabled;
    }
    public BbsThread(String url, Boolean enabled, Bbs bbs) {
        this.url = url;
        this.enabled = enabled;
        this.bbs = bbs;
    }
}
