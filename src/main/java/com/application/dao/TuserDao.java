package com.application.dao;


import com.application.entity.Tuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TuserDao extends JpaRepository<Tuser, Long> {
    //普通jpa在service做服务处理

    //自定义的Jpa-SQl
    List<Tuser> findTuserByUserNameContains(String userName);

    //Query语句查询
    @Query("select a from Tuser a where a.id = ?1")
      Tuser findByQuery(Long userId);
}