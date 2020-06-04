package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import com.livenow.datajpa.dto.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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


}
