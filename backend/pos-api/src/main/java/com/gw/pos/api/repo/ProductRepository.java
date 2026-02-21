package com.gw.pos.api.repo;

import com.gw.pos.api.domain.ProductEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
  List<ProductEntity> findByTypeOrderByPriceAsc(String type);
}

