package com.livenow.datajpa.repository;

import com.livenow.datajpa.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {


}
