package com.livenow.datajpa.domain;

import com.livenow.datajpa.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

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

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("member1");
        memberRepository.save(member);  //@PrePersist 가 발생함

        Thread.sleep(100);
        member.setUserName("member2");

        em.flush(); // db에 commit @PreUpdate가 실행행
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(()->new IllegalStateException("사용자가 없음"));

        //then
        System.out.println("findMember.getCreateDate() = " + findMember.getCreateDate());
        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
        System.out.println("findMember.getUserName() = " + findMember.getUserName());
    }


}