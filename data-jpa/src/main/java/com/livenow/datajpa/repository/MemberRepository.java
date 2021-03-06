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

    /**
     * Specifications(명세)는 실무에서 안쓴다.
     * 그냥 쓰는 방법만 알고 넘어가는게 좋음 .
     */

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
    List<UserNameOnly> findProjectionsByUserName(String username);

    List<UserNameOnlyDto> findProjectionsDtoByUserName(String username);    //Dto 클래스를 통한 사용

    <T>List<T> findProjectionsByUserName(String username, Class<T> type);

    /**
     * 네이티브 쿼리
     * 이는 제약사항이 너무 많고, 반환타입이 몇가지 지원하지 않는다.
     * 그렇기 쨰문에 프로젝션이나 Custom을 사용한다.
     *
     * 페이징 지원
     *
     * 반환 타입
     * Object[]
     * Tuple
     * DTO(스프링 데이터 인터페이스 Projections 지원)
     *
     * 제약
     * Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
     * JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
     * 동적 쿼리 불가
     * JPQL은 위치 기반 파리미터를 1부터 시작하지만 네이티브 SQL은 0부터 시작
     * 네이티브 SQL을 엔티티가 아닌 DTO로 변환은 하려면
     * DTO 대신 JPA TUPLE 조회
     * DTO 대신 MAP 조회
     * @SqlResultSetMapping 복잡
     * Hibernate ResultTransformer를 사용해야함 복잡
     * https://vladmihalcea.com/the-best-way-to-map-a-projection-query-to-a-dto-with-jpaand-hibernate/
     * 네이티브 SQL을 DTO로 조회할 때는 JdbcTemplate or myBatis 권장
     */

    @Query(value = "SELECT m.member_id as m.id, m.userName, t.name as teamName " +
            "FROM member m left join team t",
            countQuery = "SELECT count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
