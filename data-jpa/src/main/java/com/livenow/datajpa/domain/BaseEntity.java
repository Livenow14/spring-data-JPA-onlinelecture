package com.livenow.datajpa.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
/**
 * BaseEntity의 modifiedBy를 쓰지 않아도 되는 entity를 위해 BaseTimeEntity를
 * 최상위 클래스로 두고 하위 클래스로 상속받게한다.
 */

@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
