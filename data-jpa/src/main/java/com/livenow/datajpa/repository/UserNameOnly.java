package com.livenow.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {

    /**
     * open projection
     *  Value를 빼면 close로 원하는 값만 딱 select함.
     */
    @Value("#{target.userName+' '+ target.age}")
    String getUserName();
}
