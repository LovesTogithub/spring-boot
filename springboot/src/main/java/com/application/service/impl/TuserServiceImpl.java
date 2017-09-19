package com.application.service.impl;

import com.application.dao.TuserDao;
import com.application.entity.Tuser;
import com.application.service.TuserService;
import com.application.util.SimpleBeanPropertiesUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qws on 2017/9/18/018.
 */
@Service
public class TuserServiceImpl implements TuserService {

    @Autowired
    TuserDao tuserDao;

    @Override
    public Tuser findTuserById(Long id) {
        return tuserDao.findOne(id);
    }

    @Override
    public Tuser save(Tuser entity) {
        return tuserDao.save(entity);
    }

    @Override
    public Tuser findOne(Long aLong) {
        return tuserDao.findOne(aLong);
    }

    @Override
    public Page<Tuser> findAll(Pageable pageable) {
        return tuserDao.findAll(pageable);
    }

    @Override
    public Tuser findByExample(Example example) {
        return tuserDao.findOne(example);
    }

    @Override
    public void deleteTuser(Long id) {
        tuserDao.delete(id);
    }

    @Override
    public Tuser updateTuser(Tuser tuser) {
        Tuser one = this.findOne(tuser.getId());
        SimpleBeanPropertiesUtil.copyNotNullProperties(tuser, one);
        return tuserDao.saveAndFlush(one);
    }


    @Override
    public List<Tuser> findTuserByUserNameContains(String userName) {
        return tuserDao.findTuserByUserNameContains(userName);
    }

    @Override
    public Tuser findByQuery(Long userId) {
        return tuserDao.findByQuery(userId);
    }
}
