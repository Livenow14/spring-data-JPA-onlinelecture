package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false) //쿼리 확인을 위해
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @DisplayName("save, find 테스트")
    @Test
    public void  testMember(){
        //given
        Member member = new Member("Test Member1");
        Member saveMember = memberJpaRepository.save(member);

        //when
        Member findMember = memberJpaRepository.find(saveMember.getId());

        //then
        assertThat(saveMember.getId()).isEqualTo(findMember.getId());
        assertThat(saveMember.getUserName()).isEqualTo(findMember.getUserName());
        assertThat(findMember).isEqualTo(member);
    }

    @DisplayName("crud 조회")
    @Test
    public void basicCrud() throws Exception{
        //given
        Member member1= new Member("member1");
        Member member2= new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //when
        Member findMember1= memberJpaRepository.findById(member1.getId()).get();
        Member findMember2= memberJpaRepository.findById(member2.getId()).get();

        //then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

}