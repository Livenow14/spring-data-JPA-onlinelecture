package com.livenow.datajpa.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @DisplayName("Member Test")
    @Test
    public void memberEntityTest() {
        //given
        Team team1 = new Team("team1");
        Team teamB = new Team("teamB");
        em.persist(team1);
        em.persist(teamB);

        Member member = new Member("member1", 10, team1);
        Member member2 = new Member("member2", 10, team1);
        Member member3 = new Member("member3", 20, teamB);
        Member member4 = new Member("member4", 30, teamB);

        em.persist(member);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();

        //whn
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for(Member member1 : members){
            System.out.println("member1 = " + member1);
            System.out.println("member.getTeam() = " + member1.getTeam());
        }

        //then
    }

}