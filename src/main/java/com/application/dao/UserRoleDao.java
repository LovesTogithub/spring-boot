package com.application.dao;

import com.application.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by qws on 2017/9/25/025.
 */
@Repository
public interface  UserRoleDao extends JpaRepository<UserRole, Long> {
}
