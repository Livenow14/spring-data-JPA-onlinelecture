package com.livenow.datajpa.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id", "username", "age"})     //가급걱이면 연관관계 필드는 안하는게 좋다.
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String userName){
        this.userName=userName;
    }

    public Member(String userName, int age, Team team) {
        this.userName = userName;
        this.age = age;
        if(team !=null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

}
