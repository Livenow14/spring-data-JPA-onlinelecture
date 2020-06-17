package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import com.livenow.datajpa.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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

}
