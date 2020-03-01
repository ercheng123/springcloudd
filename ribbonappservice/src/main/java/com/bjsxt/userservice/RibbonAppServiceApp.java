package com.bjsxt.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务提供方启动类
 * 在Spring Cloud低版本中，如果开发的代码是Eureka Client（服务提供者和消费者），
 * 那么启动类上需要增加注解
 * @EnableEurekaClient - 当前应用是一个Eureka客户端
 * @EnableDiscoveryClient - 当前应用需要启动发现机制，就是找到Eureka服务端，并注册发现服务。
 */
@SpringBootApplication
public class RibbonAppServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(RibbonAppServiceApp.class, args);
    }
}
