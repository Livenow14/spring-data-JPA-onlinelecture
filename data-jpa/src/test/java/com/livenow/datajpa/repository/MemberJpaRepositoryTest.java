package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false) //쿼리 확인을 위해
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @DisplayName("save, find 테스트")
    @Test
    public void testMember(){
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

}