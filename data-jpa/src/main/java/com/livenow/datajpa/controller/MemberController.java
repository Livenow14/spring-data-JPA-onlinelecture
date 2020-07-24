package com.livenow.datajpa.controller;

import com.livenow.datajpa.domain.Member;
import com.livenow.datajpa.dto.MemberDto;
import com.livenow.datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")         //pathvarible기반으로 많이짬 이것이 rest api
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalStateException("사용자가 없습니다."));
        return member.getUserName();
    }

    /**
     * 도메인 클래스 컨버터
     * 이런 기능을 권장하지 않음. 따로 명시해주는것이 좋다
     *
     * 주의: 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이
     * 엔티티는 단순 조회용으로만 사용해야 한다. ( 트랜잭션이 없는 범위에서
     * 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않음
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUserName();
    }

 /*   @PostConstruct
    public void init() {
        memberRepository.save(new Member("member1"));
    }
    */

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=5, sort="userName") Pageable pageable){
        Page<MemberDto> map = memberRepository.findAll(pageable)
                .map(MemberDto::new);
        return map;
    }

    /**
     * Item Test에서 확인하기 위해 주석처리함
     */
    //@PostConstruct
    public void init() {
        for(int i=0; i<100; i++){
            memberRepository.save(new Member("user"+ i, i));
        }
    }
}
