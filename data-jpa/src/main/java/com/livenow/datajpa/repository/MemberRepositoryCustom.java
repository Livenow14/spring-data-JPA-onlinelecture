package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
