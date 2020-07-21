package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import com.livenow.datajpa.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 핵심 비즈니스 로직이 있는 repo랑 화면을 구성하는 repo는 분리하자.
 * 사용자 정의 리포지가 꼭 필요한것이 아니기 떄문이고, 핵심비즈니스 로직이 잘 안보이기 때문이다.
 * 아키텍쳐 적으로 고민해야한다. 애플리케이션이 커지면서 커맨드와 쿼리 분리, 핵심 비즈니스 로직, 화면 구성, 라이프 사이클따라서 뭐가 변경되는지 분리할 줄 알아야한다.
 */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    /**
     * username으로하면 안되는거 확인.
     * Entity class의이름과 동일하게 정의해야함
     */
    List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);

    /**
     * 이는 정적 쿼리만 가능하다.
     * 동적 쿼리를 위해서는 QueryDsl을 사용해야한다.
     */
    @Query("select m from Member m where m.userName = :userName and m.age =:age")
    List<Member> findUser(@Param("userName") String userName,
                          @Param("age") int age);

    @Query("select m.userName from Member m")
    List<String> findUsernameList();

    @Query("select new com.livenow.datajpa.dto.MemberDto(m.id, m.userName, t.name) from Member m join m.team t ")
    List<MemberDto> findMemberDto();

    /**
     * 파라미터 바인딩
     */
    @Query("select m from Member m where m.userName in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /**
     * 반환타입, 위와같은 것을 지원해준다.
     * 참고: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의
     * Query.getSingleResult() 메서드를 호출한다. 이 메서드를 호출했을 때 조회 결과가 없으면
     * javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히 불편하
     * 다. 스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을 반환한다
     */
    List<Member> findMemberListByUserName(String username); //컬렉션
    Member findMemberByUserName(String username); //단건
    Optional<Member> findOptionalByUserName(String username); //단건 Optional

    /**
     * 페이징, 페이징 지원
     */
    //Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 페이징, count쿼리 사요안함
     *Query Methods 기능 활용
     */
    List<Member> findByAgeGreaterThan(int age, Pageable pageable);

    /**
     * slice, 어플의 밑으로 더보기 이런 형식을 사용할 때
     */
    //Slice<Member> findByAge(int age, Pageable pageable);

    /**
     * count Query 분리, 조인이 들어가면 모든 값들을 조회해서 count해야하기 때문에 분리해야한다.
     * sorting이 너무 복잡하면 Query안에 넣는걸 추천 .
     */
    @Query(value = "select m from Member m left join m.team t",
    countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * 벌크성 수정쿼리
     * @Modifying 이 있어야 update쿼리를 실행함
     */
  //  @Modifying
    @Modifying(clearAutomatically = true)       //쿼리가 나간다음에 자동적으로 영속성 컨텍스트 clear해줌.
    @Query("update Member m set m.age = m.age +1 where m.age >=:age")
    int bulkAgePlus(@Param("age")int age);


    @Query("select m from Member m join fetch m.team t")
    List<Member> findMemberFetchJoin();

    /**
     * 엔티티 그래프를 사용하면 @Query(jpql)를 사용하지 않아도 된다.
     * fetch조인을 저절로 해준다고 생각.
     * 쿼리가 복잡해진다면 jpql를 사용하여 fetch join을 사용한다.
     * 간단한 fetch join은 밑과 같이 사용한다.
     */
    @Override
    @EntityGraph(attributePaths = "team")
    List<Member> findAll();

    @EntityGraph(attributePaths = "team")
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = "team")
    List<Member> findEntityGraphByUserName(@Param("userName") String userName);

    /**
     * 쿼리 힌트. readOnly를 표현함.
     */
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUserName(String userName);

    /**
     * 락을 사용하는 법, 자세한건 JPA책을 봐야함
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUserName(String userName);



    /**
     * Auditing 오디팅
     * 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적
     * 등록일, 수정일, 등록자, 수정자
     * 실무에서 많이 사용하는 부분이라 중요함.
     */

    /**
     * 스프링 데이터 JPA 구현체 분석
     * JPA Repository에서 왼쪽 버튼을 누르면 볼 수 있다. SimpleJpaRepository가 JPA의 구현체
     * 기본적으로 Transactional(readOnly = treu)를 걸고있고, 업데이트나 저장의 경우 따로 Transactional 건다.
     * 그래서 스프링 데이터 JPA를 사용할때 트렌잭션이 없어도 데이터 등록, 변경이 가능했다.
     * Transactional(readOnly = treu)은 flush를 즉, 커밋을 안하게 해줌. 변경감지 이런것도 하지않기에 약간의 성능향상
     *
     * save가 매우 중요함.
     * 	public <S extends T> S save(S entity) {
     *
     * 		if (entityInformation.isNew(entity)) {
     * 			em.persist(entity);
     * 			return entity;
     *                } else {
     * 			return em.merge(entity);
     *        }*
     * }
     *
     * 새로운 엔티티면 저장("persist")
     * 새로운 엔티티가 아니면 병합("merge")
     * 병합은 DB에 있는 데이터를 가져와서 바꿔치기를 함.
     * DB에 꺼내서, 파라미터로 넘어온 애로 바꿔치기한다는 것
     * 단점은 DB select쿼리가 한번 간다는 것임 .
     *
     * 이는 성능의 저하를 일으키기 때문에 변경은 dirtyChecking(변경감지)를 사용해야한다.
     * merge는 준영속상태를 영속상태로 바꿔야할 때 쓰는것이기 때문에. 데이터 변경에서는 사용하면 안된다.
     * entityInformation.isNew(entity)는 어떻게 진행되는지 확인.
     */
}
