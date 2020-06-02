package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * username으로하면 안되는거 확인.
     * Entity class의이름과 동일하게 정의해야함
     */
     List<Member> findByUserNameAndAgeGreaterThan(String userName, int age);
}
