package com.gw.pos.api.repo;

import com.gw.pos.api.domain.LoginInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginInfoRepository extends JpaRepository<LoginInfoEntity, String> {}

