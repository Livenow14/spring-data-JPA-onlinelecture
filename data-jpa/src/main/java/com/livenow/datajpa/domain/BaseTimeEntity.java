package com.livenow.datajpa.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Auditing 오디팅
 * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적
 * 등록일, 수정일, 등록자, 수정자
 * 실무에서 많이 사용하는 부분이라 중요함.
 */

/**
 * BaseEntity의 modifiedBy를 쓰지 않아도 되는 entity를 위해 BaseTimeEntity를
 * 최상위 클래스로 두고 하위 클래스로 상속받게한다.
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime LastModifiedDate;
}
