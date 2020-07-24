package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

/*    @Test
    public void save(){
        Item item = new Item();
        *//**
         * @GenerateValue는 SimpleJpaRepository에서
         * save할때 em.persist가 완료되는 시점에 기본키를 생성함함         *
         * transaction 없어도됨. 기본적으로 걸려있음
         *//*

        itemRepository.save(item);
    }*/

    @Test
    public void Changedsave(){
        Item item = new Item("A");
        /**
         * "A"가 있으니까 SimpleJpaRepository에서
         * persist가 동작을 하지않고 머지가 동작하기됨  이는 위험험
         * 기본적으로 merge를 쓰지 않는다고 생각
         * */

        itemRepository.save(item);
    }

}