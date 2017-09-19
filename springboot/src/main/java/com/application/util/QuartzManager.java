package com.application.util;


import com.application.util.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @version V1.1.6
 * @Description: 定时任务管理类
 */
public class QuartzManager {
    protected static final Logger logger = LoggerFactory.getLogger(QuartzManager.class);
    private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
    private static String JOB_NAME = "_J_NAME";
    private static String JOB_GROUP_NAME = "_JG_NAME";
    private static String TRIGGER_NAME = "_T_NAME";
    private static String TRIGGER_GROUP_NAME = "_TG_NAME";

    /**
     * @param tabName   Job名，必填
     * @param dbName    组名，必填
     * @param taskLocal 执行脚本位置，必填
     * @param taskTime  执行时间,格式为   HH:mm:ss,必填  默认每天执行
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    @SuppressWarnings("unchecked")
    public static Date addJob(String tabName, Integer id, String dbName, String taskLocal, String taskTime, Class<? extends Job> jobClass) {
        //通过schedulerFactory获取一个调度器
        Date date = null;
        try {
            String[] dLen = taskTime.split(":");
            //dLen(0) //时
            //dLen(1) //分
            //dLen(2) //秒
            String cronStr = dLen[2] + " " + dLen[1] + " " + dLen[0] + " * * ? ";
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail jobDetail = new JobDetail(tabName + JOB_NAME, dbName + JOB_GROUP_NAME, jobClass);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("tabName", tabName + JOB_GROUP_NAME);
            jobDataMap.put("dbName", dbName + JOB_GROUP_NAME);
            jobDataMap.put("taskLocal", taskLocal);
            jobDataMap.put("taskTime", taskTime);
            jobDataMap.put("cronStr", cronStr);
            jobDataMap.put("id", id);

            SpringUtils springUtils = new SpringUtils();


            jobDetail.setJobDataMap(jobDataMap);


            // 触发器
            CronTrigger trigger = new CronTrigger(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);
            trigger.setCronExpression(cronStr);

            //把作业和触发器注册到任务调度中
            date = sched.scheduleJob(jobDetail, trigger);
            // 启动  
            if (!sched.isShutdown()) {
                sched.start();
                getJob(tabName, dbName);
                getTrigger(tabName, dbName);
                return date;
            }
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
        return date;
    }


    /**
     * @param tabName   Job名，必填
     * @param dbName    组名，必填
     * @param taskLocal 执行脚本位置，必填
     * @param taskTime  执行时间,格式为   HH:mm:ss,必填  默认每天执行
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    @SuppressWarnings("unchecked")
    public static Date modifyJobTime(String tabName, Integer id, String dbName, String taskLocal, String taskTime, Class<? extends Job> jobClass) {
        Date date = null;
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();

            CronTrigger trigger = (CronTrigger) sched.getTrigger(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);
            if (trigger == null) {
                return null;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(taskTime)) {
                deleteJob(tabName, dbName);
                date = addJob(tabName, id, dbName, taskLocal, taskTime, jobClass);
                logger.debug("修改了定时任务---> " + dbName + JOB_GROUP_NAME + "." + tabName + JOB_NAME);
            }
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
        return date;
    }

    /**
     * 获得一个job
     */
    public static JobDetail getJob(String tabName, String dbName) {
        JobDetail jobDetail = null;
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            jobDetail = sched.getJobDetail(tabName + JOB_NAME, dbName + JOB_GROUP_NAME);
            logger.debug("任务--->jobDetail:{}", jobDetail);
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
        return jobDetail;

    }

    /**
     * 获得一个Trigger
     */
    public static CronTrigger getTrigger(String tabName, String dbName) {
        CronTrigger trigger = null;
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            trigger = (CronTrigger) sched.getTrigger(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);
            logger.debug("触发器--->trigger:{}", trigger);
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
        return trigger;

    }

    /**
     * @param tabName Job名，必填
     * @param dbName  组名，必填
     * @Description: 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    public static boolean deleteJob(String tabName, String dbName) {
        boolean flag = false;
        try {
            JobDetail jobDetail = getJob(tabName, dbName);
            CronTrigger trigger = getTrigger(tabName, dbName);
            logger.debug("移除任务jobDetail:{}", jobDetail);
            logger.debug("移除触发器trigger:{}", trigger);
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.pauseTrigger(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);// 停止触发器
            flag = sched.unscheduleJob(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);// 移除触发器
            if (flag) {
                flag = sched.deleteJob(tabName + JOB_NAME, dbName + JOB_GROUP_NAME);// 删除任务
                return flag;
            }
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
        return flag;
    }

    /**
     * @Description:启动所有定时任务
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    public static void startJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.start();
            logger.debug("开始所有任务");
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();

            if (!sched.isShutdown()) {
                sched.shutdown();
                logger.debug("暂停所有任务");
            }
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
    }

    /**
     * @Description:暂停定时任务(任务和触发器)
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    public static void pauseJob(String tabName, String dbName) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.pauseJob(tabName + JOB_NAME, dbName + JOB_GROUP_NAME);
                sched.pauseTrigger(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);

                JobDetail jobDetail = getJob(tabName, dbName);
                CronTrigger trigger = getTrigger(tabName, dbName);
                logger.debug("暂停任务jobDetail:{}", jobDetail);
                logger.debug("暂停触发器trigger:{}", trigger);
            }
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
    }

    /**
     * @Description:恢复定时任务(任务和触发器)
     * @Title: QuartzManager.java
     * @version V2.2.1
     */
    public static void resumeJob(String tabName, String dbName) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.resumeJob(tabName + JOB_NAME, dbName + JOB_GROUP_NAME);
                sched.resumeTrigger(tabName + TRIGGER_NAME, dbName + TRIGGER_GROUP_NAME);

                JobDetail jobDetail = getJob(tabName, dbName);
                CronTrigger trigger = getTrigger(tabName, dbName);
                logger.debug("恢复任务jobDetail:{}", jobDetail);
                logger.debug("恢复触发器trigger:{}", trigger);
            }
        } catch (Exception e) {
            logger.error("Exception:{}" + e);
        }
    }


}  
