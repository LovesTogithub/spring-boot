package com.application.dao;

import com.application.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by qws on 2017/9/25/025.
 */
@Repository
public interface RoleDao extends JpaRepository<Role, Long> {

}
