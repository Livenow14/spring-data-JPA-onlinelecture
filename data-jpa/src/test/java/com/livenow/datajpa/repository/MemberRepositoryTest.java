package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import com.livenow.datajpa.domain.Team;
import com.livenow.datajpa.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @DisplayName("Data Jpa 확인")
    @Test
    public void dataJpaTest() throws Exception{
        //given
        Member member = new Member("data JPA Member");
        Member save = memberRepository.save(member);
        Member findMember = memberRepository.findById(save.getId()).get();  //예제니까 예외는 뺀다

        //when

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member);

    }

    @DisplayName("crud 조회")
    @Test
    public void basicCrud() throws Exception{
        //given
        Member member1= new Member("member1");
        Member member2= new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        Member findMember1= memberRepository.findById(member1.getId()).get();
        Member findMember2= memberRepository.findById(member2.getId()).get();

        //then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen(){
        Member member = new Member("AAA", 10);
        Member member1 = new Member("AAA", 20);
        memberRepository.save(member);
        memberRepository.save(member1);
        List<Member> result = memberRepository.findByUserNameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUserName()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void testQuery() {
        Member member = new Member("AAA", 10);
        Member member1 = new Member("AAA", 20);
        memberRepository.save(member);
        memberRepository.save(member1);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member);

    }

    @Test
    public void findUsernameList() {
        Member member = new Member("AAA", 10);
        Member member1 = new Member("AAA", 20);
        memberRepository.save(member);
        memberRepository.save(member1);

        List<String> usernameList = memberRepository.findUsernameList();

        assertThat(usernameList.get(0)).isEqualTo(member.getUserName());
        assertThat(usernameList.get(1)).isEqualTo(member1.getUserName());

    }


    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 10);
        member.setTeam(team);
        memberRepository.save(member);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.get(0).getTeamName()).isEqualTo(member.getTeam().getName());
    }

    /**
     * 파라미터 바인딩
     */
    @Test
    public void findByNames() {
        Member member = new Member("AAA", 10);
        Member member1 = new Member("BBB", 20);
        memberRepository.save(member);
        memberRepository.save(member1);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        assertThat(result.get(0).getUserName()).isEqualTo(member.getUserName());
        assertThat(result.get(1).getUserName()).isEqualTo(member1.getUserName());

    }

    /**
     * 반환타입
     */
    @Test
    public void returnType() {
        Member member = new Member("AAA", 10);
        Member member1 = new Member("BBB", 20);
        memberRepository.save(member);
        memberRepository.save(member1);


        //데이터가 없을때 빈 entity 클래스를 반환한다. 즉 널을 반환하지 않는다.
        List<Member> aaa = memberRepository.findMemberListByUserName("AAA");
        System.out.println("aaa = " + aaa);


        /**
         * 데이터를 없을때, null을 반환한다.
         * JPA는 nullException을 터트린다. 그거와 차이가 있다.
         *
         */
        Member memberByUserName = memberRepository.findMemberByUserName("AAA");
        Member adfadf = memberRepository.findMemberByUserName("adfasdf");


        /**
         * 위와같은 문제를 해결하려, Optional이 나왔다.
         * 그렇기 때문에 단건일때 데이터가 잇을수도 있고, 없을 수 도 있으면 Optional을 사용한다.
         *
         */
        Optional<Member> aaa1 = memberRepository.findOptionalByUserName("dfdf");

        /**
         * 이런경우일 땐 오류가난다.
         */
        Member member2 = new Member("AAA", 20);
       // memberRepository.save(member2);
        Optional<Member> aaa2 = memberRepository.findOptionalByUserName("AAA");


       System.out.println("aaa2 = " + aaa2);


    }

    @DisplayName("JPA paging확인")
    @Test
    public void paging() throws Exception{
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age=9;

        /**
         *  query의 limit를 위한것
         *  실제 값들은 반환타입에 의해 결정됨 (repo 반환타입 변경 필수)
         */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "userName"));

        //when
        // total count까지 날려줌
        Page<Member> pagePaging = memberRepository.findByAge(age, pageRequest);

        /**
         * Entity를 그대로 반환하면 안되기 때문에 map을 사용한다.
         * 페이지를 유지하면서 엔티티를 DTO로 변환
         */
        Page<MemberDto> map = pagePaging.map(member -> new MemberDto(member.getId(), member.getUserName(), null));

        /**
         * 이럴때 Dto로 변환하기 때문에, 바로 반환해도 괜찮다.
         */

        //then
        List<Member> content = pagePaging.getContent();
        long totalElements = pagePaging.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(pagePaging.getTotalElements()).isEqualTo(5);
        assertThat(pagePaging.getNumber()).isEqualTo(0);
        assertThat(pagePaging.getTotalPages()).isEqualTo(2);
        assertThat(pagePaging.isFirst()).isTrue();
        assertThat(pagePaging.hasNext()).isTrue();


        /**
         * Query Methods 기능 활용
         */
        List<Member> byAgeGreaterThan = memberRepository.findByAgeGreaterThan(age, pageRequest);
        for (Member member : byAgeGreaterThan) {
            System.out.println("member = " + member.getUserName());
        }

/*        //when
        //slice 다음페이지 넘기지않고, 더 불러오는것(ex, 어플의 내리기 새로고침)
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        List<Member> content = page.getContent();
        //then
        assertThat(content.size()).isEqualTo(3);
   //     assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
     //   assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();*/


    }
}