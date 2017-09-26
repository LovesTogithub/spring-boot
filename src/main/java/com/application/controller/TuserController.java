package com.application.controller;

import com.application.entity.Tuser;
import com.application.service.TuserService;
import com.application.util.BeanResult;
import com.application.util.ConfigUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by qws on 2017/9/18/018.
 */
@RestController
@RequestMapping("/jpa")
public class TuserController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    TuserService tuserService;

    @ApiOperation(value = "获得用户", notes = "齐文帅")
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/user/getTuserById/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult getUserById(@ApiParam(required = true, name = "id", value = "用户信息", defaultValue = "2")
                                  @PathVariable("id") Long id) {
        BeanResult result;
        try {
            Tuser tuser = tuserService.findTuserById(id);
            logger.info("测试log4j");
            logger.error("测试log4j2");
            result = BeanResult.success(tuser);
        } catch (Exception e) {
            logger.error("错误"+e.getMessage());
            return null;
        }
        return result;
    }
    @ApiOperation(value = "增加用户", notes = "增加用户")
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult add(@RequestBody Tuser tuser) {
        BeanResult result;
        try {
            Tuser t2user = tuserService.save(tuser);
            result = BeanResult.success(t2user);
            logger.info("测试log4j1");
        } catch (Exception e) {

            return null;
        }
        return result;
    }

    @ApiOperation(value = "分页查询全部列表", notes = "分页查询全部列表")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult findALL(@RequestParam(value = "page", defaultValue = "0") Integer page,
                              @RequestParam(value = "size", defaultValue = "15") Integer size) {
        BeanResult result;
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            Pageable pageable = new PageRequest(page, size, sort);
            Page<Tuser> all = tuserService.findAll(pageable);
            result = BeanResult.success(all);
        } catch (Exception e) {
            logger.error("测试log4j1");
            return null;
        }
        return result;
    }

    @RequestMapping(value = "/findOne", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult findOne(@RequestParam(value = "id") Long id) {
        BeanResult result;
        try {
            Tuser one = tuserService.findOne(id);
            result = BeanResult.success(one);
        } catch (Exception e) {
            logger.error("测试log4j1");
            return null;
        }
        return result;
    }

    @RequestMapping(value = "/findExample", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult findExamPle(@RequestParam(value = "id") Long id,
                                  @RequestParam(value = "mobile", required = false) String mobile) {
        BeanResult result;
        try {
            Tuser tuser = new Tuser();
            tuser.setId(id);
            tuser.setMobile(mobile);
            Example<Tuser> of = Example.of(tuser);
            Tuser byExample = tuserService.findByExample(of);
            result = BeanResult.success(byExample);
        } catch (Exception e) {
            logger.error("测试log4j1");
            return null;
        }
        return result;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult update(@RequestBody Tuser tuser) {
        BeanResult result;
        try {
            Tuser t2user = tuserService.updateTuser(tuser);
            result = BeanResult.success(t2user);
        } catch (Exception e) {
            logger.error("测试log4j1");
            return null;
        }
        return result;
    }

    @RequestMapping(value = "/findContain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult findLike(@RequestParam(value = "username", defaultValue = "") String username) {
        BeanResult result;
        try {
            List<Tuser> tuserByUserNameContains = tuserService.findTuserByUserNameContains(username);
            result = BeanResult.success(tuserByUserNameContains);
        } catch (Exception e) {
            logger.error("测试log4j1");
            return null;
        }
        return result;
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BeanResult query(@RequestParam(value = "userId", defaultValue = "") Long userId) {
        BeanResult result;
        try {
            result = BeanResult.success(tuserService.findByQuery(userId));
        } catch (Exception e) {
            logger.error("测试log4j1");
            return null;
        }
        return result;
    }
}
