package com.livenow.datajpa.repository;

public class UserNameOnlyDto {

    private final String userName;

    /**
     * 생성자의 파라미터 명을 분석하여 Projections이 실행됨
     */
    public UserNameOnlyDto(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
