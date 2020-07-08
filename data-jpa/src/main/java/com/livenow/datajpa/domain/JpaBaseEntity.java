package com.livenow.datajpa.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass   //진짜 상속관계는 아니고, 속성만 내려서 쓸 수 있게 하는 것, 이게 있어야 Member에 추가됨
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)      //createDate는 바뀔일 없게 updatable 을 false로 지정
    private LocalDateTime createDate;

    private LocalDateTime updatedDate;

    @PrePersist //persis 하기전에 이벤트를 발생시킴
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updatedDate = now;
    }

    @PreUpdate  //update전에 이벤트를 발생
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
