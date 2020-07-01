package com.livenow.datajpa.repository;


import com.livenow.datajpa.domain.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 실무에서 많이 쓴다. QueryDSL과 함꼐.
 * 네이밍 규칙이 "MemberRepository" + "Impl" 이다 주의 하자.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m ", Member.class)
                .getResultList();
    }
}
