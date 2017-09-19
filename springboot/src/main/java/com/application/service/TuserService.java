package com.application.service;

import com.application.entity.Tuser;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * Created by qws on 2017/9/18/018.
 */
public interface TuserService {

    Tuser findTuserById(Long id);

    Tuser save(Tuser entity);

    Tuser findOne(Long aLong);

    Page<Tuser> findAll(Pageable pageable);

    Tuser findByExample(Example example);

    void deleteTuser(Long id);

    Tuser updateTuser(Tuser tuser);

    List<Tuser> findTuserByUserNameContains(String userName);

    Tuser findByQuery(Long userId);
}
