package com.feirinha.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.feirinha.api.model.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Item> findByName(String name);
}
