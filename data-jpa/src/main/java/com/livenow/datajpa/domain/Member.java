package com.livenow.datajpa.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String userName;

    protected Member(){};

    public Member(String userName){
        this.userName=userName;
    }

}
