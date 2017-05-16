package com.taotao.order.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.taotao.order.service.OrderService;

/**
 * @author liuhongbao
 * @date 2017年4月16日 下午8:47:46
 * 
 */
public class MyJob extends QuartzJobBean{

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //获取srping容器
        ApplicationContext applicationContext = (ApplicationContext) context.getJobDetail().getJobDataMap().get("applicationContext");
        
        //获取OrderService执行清理无效订单的方法
        applicationContext.getBean(OrderService.class).cleanInvalidOrder();
    }

}
