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

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member member = new Member("AAA", 10);
        Member member1 = new Member("AAA", 20);
        memberJpaRepository.save(member);
        memberJpaRepository.save(member1);
        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    
    @DisplayName("JPA paging확인")
    @Test
    public void paging() throws Exception{
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age=10;
        int offset =0;
        int limit =3;

        //when
        List<Member> members= memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(10);

        /**
         * 페이지 계산 공식 적용 ...
         * totalPage = totalCount / size ...
         * 마지막 페에지 ...
         * 최초 페이지 ...
         */

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }
}