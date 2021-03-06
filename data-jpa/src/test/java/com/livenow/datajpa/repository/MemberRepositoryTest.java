package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import com.livenow.datajpa.domain.Team;
import com.livenow.datajpa.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    @PersistenceContext
    EntityManager em;

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

    /**
     * 벌크성 수정쿼리
     * @Modifying 이 있어야 update쿼리를 실행함
     * DB에 바로 update하기 때문에 영속성 컨텍스트에서 조회가 안됨
     * 다른 update도 모두 체크해야함 .
     */
    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

/*        em.flush();             // 혹시 남아있는것 commit해주는것(사실상 save할때 해줌 )
        em.clear();             // 날려주는것  modify옵션을 해주면 안해도됨  */

        //DB에 바로 update하기 때문에  영속성 컨텍스트에서 반영이 안됨 안됨
        List<Member> result = memberRepository.findMemberListByUserName("member5");

        //그렇기 때문에 영속성 컨텍스트를 조회전 날려줘야함



        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(4);
        assertThat(result.get(0).getAge()).isEqualTo(51);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        Member member3 = new Member("member1", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        em.flush();
        em.clear();         //commit 하고, 영속성 컨텍스트를 다 날려버림

        //when  N + 1 문제
        List<Member> all = memberRepository.findAll();                      //메서드를 override 하고 다시 해봤을때(entitygraph) fetch Join이 됐음을 확인 할 수 있다.
        //List<Member> all = memberRepository.findMemberFetchJoin();      //프록시가 아닌 진짜 객체가 들어오는 것을 확인할 수 있다.
        /**
         * 엔티티 그래프를 사용하여 fetch를 자동적으로 하게 해줌 
         */
        // List<Member> all = memberRepository.findAll();                      //메서드를 override 하고 다시 해봤을때(entitygraph) fetch Join이 됐음을 확인 할 수 있다.
        //List<Member> member1s = memberRepository.findEntityGraphByUserName("member1");      //entityGraph는 기본적으로 left outer join을 사용


        for (Member member : all) {
            System.out.println("member.getUserName() = " + member.getUserName());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());   //team 까지는 프록시 객체가 영속성에 있지만(프록시를 초기화 한다는 거임), team의 name은 없기 때문에 쿼리를 다시 날림. 이때 Lazy가 활성화 됐다고 알 수 있다.


    /*    for (Member member : member1s) {
            System.out.println("member.getUserName() = " + member.getUserName());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }*/
        }
    }

    /**
     * 쿼리 최적화, readOnly
     * 하지만 실제 실무에서 최적화가 많이 안된다. 이점이 많이 없다. 그래서 애매하다. 
     * 실제 성능 최적화가 일어나는지 테스트를 해봐야한다. 
     * cache단에서 튜닝이 더 필요하다. 
     */

    @Test
    public void queryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
       // Member findMember = memberRepository.findById(member1.getId()).get(); // 실무에서는 이렇게 안한다. 원본이랑 가져온거랑 생김. 그래서 readOnly기능 이 필요함.
        //findMember.setUserName("member2");      //dirty checking 하면서 비용이 더 든다.

        Member findMember = memberRepository.findReadOnlyByUserName("member1");
        findMember.setUserName("member2");          //이렇게하면 변경이 안된다. readOnly를 열어놨기 때문에

        em.flush();
    }


    /**
     * 락을 사용하는 법, 자세한건 JPA책을 봐야함.
     * 실시간 트래픽이 많은경우 락을 잘 안건다.
     * select문에서 for update이 나온다.
     */
    @Test
    public void Lock(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        List<Member> member11 = memberRepository.findLockByUserName("member1");

        em.flush();
    }

    @Test
    public void callCustom(){
        //given
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        Member member3 = new Member("member3", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> memberCustom = memberRepository.findMemberCustom();
        List<Member> findAll = memberRepository.findAll();

        assertThat(memberCustom.get(0).getAge()).isEqualTo(findAll.get(0).getAge());
    }

    /**
     * Query By Example
     * 여기서 문제는 join 에서 잘 안됨 (inner join만 됨 )
     * 복잡한 join이 되지 않아 실무에서 잘 안씀
     *
     * Probe: 필드에 데이터가 있는 실제 도메인 객체
     * ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
     * Example: Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용
     *
     * 장점
     * 동적 쿼리를 편리하게 처리
     * 도메인 객체를 그대로 사용
     * 데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
     * 스프링 데이터 JPA JpaRepository 인터페이스에 이미 포함
     *
     * 단점
     * 조인은 가능하지만 내부 조인(INNER JOIN)만 가능함 외부 조인(LEFT JOIN) 안됨
     * 다음과 같은 중첩 제약조건 안됨
     * firstname = ?0 or (firstname = ?1 and lastname = ?2)
     * 매칭 조건이 매우 단순함
     * 문자는 starts/contains/ends/regex
     * 다른 속성은 정확한 매칭( = )만 지원
     *
     * 정리
     * 실무에서 사용하기에는 매칭 조건이 너무 단순하고, LEFT 조인이 안됨
     * 실무에서는 QueryDSL을 사용하자
     * */
    @Test
    public void queryByExample(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");
        Team teamA1 = new Team("teamA");
        member.setTeam(teamA1);

        /**
         * null 일때 넣어줄걸
         */
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);
        List<Member> result = memberRepository.findAll(example);
        assertThat(result.get(0).getUserName()).isEqualTo("m1");
    }

    /**
     * Projectrions( 전체 엔티티중 원하는 데이터 하나만 가져오게 하고싶다~)
     * 구현체가 아닌 인터페이스다.
     *
     * 주의
     * 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
     * 프로젝션 대상이 ROOT가 아니면
     * LEFT OUTER JOIN 처리
     * 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
     *
     * 정리
     * 프로젝션 대상이 root 엔티티면 유용하다.
     * 프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!
     * 실무의 복잡한 쿼리를 해결하기에는 한계가 있다.
     * 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 QueryDSL을 사용하자
     *
     */
    @Test
    public void projections () {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        Member m3 = new Member("m3", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        //when
        List<UserNameOnly> result = memberRepository.findProjectionsByUserName("m1");

        //then
        for (UserNameOnly userNameOnly : result) {
            System.out.println("userNameOnly = " + userNameOnly.getUserName());
        }


        /**
         * Dto를 통한 Projection
         */
        //when
        List<UserNameOnlyDto> m11 = memberRepository.findProjectionsDtoByUserName("m1");

        //then
        for (UserNameOnlyDto userNameOnlyDto : m11) {
            System.out.println("userNameOnlyDto.getUserName() = " + userNameOnlyDto.getUserName());
        }

        /**
         * 동적 Projections, class나 interface등 모두 사용가능하다.
         */
        //when
        List<UserNameOnlyDto> m12 = memberRepository.findProjectionsByUserName("m1", UserNameOnlyDto.class);

        //then
        for (UserNameOnlyDto userNameOnlyDto : m12) {
            System.out.println("userNameOnlyDto.getUserName() = " + userNameOnlyDto.getUserName());
        }

        /**
         * 중첩 projection은 중첩될 시 쓰기가 애매해진다
         */
        //when
        List<NestedClosedProjections> m13 = memberRepository.findProjectionsByUserName("m1", NestedClosedProjections.class);

        //then
        for (NestedClosedProjections nestedClosedProjections : m13) {
            String userName = nestedClosedProjections.getUserName();
            System.out.println("userName = " + userName);
            String name = nestedClosedProjections.getTeam().getName();
            System.out.println("name = " + name);
        }
    }

    /**
     * 네이티브 쿼리
     */
    @Test
    public void nativeQuery() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0,10));
        List<MemberProjection> content = result.getContent();
        
        //then
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUserName() = " + memberProjection.getUserName());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }
    }


}