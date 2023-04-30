package com.bullish.checkout.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findAllByProductId(Long id);
}
