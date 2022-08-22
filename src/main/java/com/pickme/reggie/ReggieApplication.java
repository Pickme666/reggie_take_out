package com.pickme.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//TODO @Slf4j 注解，开启lombok提供的生成日志的记录器。
@Slf4j
@SpringBootApplication
//启用扫描Servlet组件（过滤器、Servlet和监听器）
@ServletComponentScan
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        //输出日志信息
        log.info("项目启动成功...");
    }

}
