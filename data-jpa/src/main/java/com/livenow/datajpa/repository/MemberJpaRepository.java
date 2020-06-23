package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }

    public void delete(Member member){
        em.remove(member);
    }
    
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age ){
        return em.createQuery("select m from Member m" +
               " where m.userName =:username and m.age >:age", Member.class )
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    /**
     * setFistResult = 몇번째부터 가져올거야
     * setMaxResult = 몇개나 가져올것이냐?
     */
    public List<Member> findByPage(int age, int offset, int limit){
        return em.createQuery("select m from Member m" +
                " where m.age= :age order by m.userName desc", Member.class)
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * counting은 sorting을 해야할 필요가 없으니, 성능최적화를 위해 제거함
     */
    public long totalCount(int age){
       return em.createQuery("select count (m) from Member m " +
                " where m.age=:age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    /**
     * 벌크성 수정쿼리
     */
    public int bulkAgePlus(int age){
       return em.createQuery("update Member m set m.age = m.age + 1 where m.age >=:age")
                .setParameter("age", age)
                .executeUpdate();
    }

}
