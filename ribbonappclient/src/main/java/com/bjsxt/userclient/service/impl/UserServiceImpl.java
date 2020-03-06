package com.bjsxt.userclient.service.impl;

import com.bjsxt.entity.User;
import com.bjsxt.userclient.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import   com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * cacheNames - 可以实现缓存分组，是Hystrix做分组。
 */
@Service
@CacheConfig(cacheNames = "test_cache_names")
public class UserServiceImpl implements UserService {

    /**
     * 是Ribbon技术中的负载均衡客户端对象。其中封装了从Eureka Server上发现的所有的服务地址列表
     * 包括服务的名称，IP，端口等。
     */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 远程方法调用。访问application service，访问的地址是：http://localhost:8080/user/save
     * HystrixCommand注解 - Hystrix容灾处理注解。当前方法做容灾处理。
     *  属性fallbackMethod - 如果远程调用发生问题，调用本地的什么方法获取降级结果（托底）。
     *  属性commandProperties - 定义Hystrix容灾逻辑的具体参数。
     *    CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD - 单位时间内，多少个请求发生错误，开启
     *      熔断（断路由）
     *    CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE - 单位时间内，错误请求百分比，开启
     *      熔断（断路由）
     *    CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS - 断路由开启后，休眠多少毫秒，不访问
     *      远程服务。
     */
    @Override
    @HystrixCommand(fallbackMethod = "saveFallback", commandProperties = {
        @HystrixProperty(
                name=HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD,
                value="10"
        ),
        @HystrixProperty(
                name=HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE,
                value="50"
        ),
        @HystrixProperty(
                name=HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS,
                value="3000"
        )
    })
    public Map<String, Object> save(User user) {
        // 根据服务的名称，获取服务实例。服务名称就是配置文件yml中的spring.application.name
        // 服务实例包括，这个名称的所有服务地址和端口。
        ServiceInstance instance =
                this.loadBalancerClient.choose("ribbon-app-service");
        // 访问地址拼接
        StringBuilder builder = new StringBuilder("");
        builder.append("http://").append(instance.getHost())
                .append(":").append(instance.getPort()).append("/user/save")
                .append("?username=").append(user.getUsername())
                .append("&password=").append(user.getPassword())
                .append("&remark=").append(user.getRemark());
        System.out.println("本地访问地址：" + builder.toString());

        // 创建一个Rest访问客户端模板对象。
        RestTemplate template = new RestTemplate();

        // 约束响应结果类型
        ParameterizedTypeReference<Map<String, Object>> responseType =
                new ParameterizedTypeReference<Map<String, Object>>() {
                };

        // 远程访问application service。
        ResponseEntity<Map<String, Object>> response =
                template.exchange(builder.toString(), HttpMethod.GET,
                null, responseType);

        Map<String, Object> result = response.getBody();
        return result;
    }

    private Map<String, Object> saveFallback(User user){
        // 同步->异步，讲user封装成message消息，发送到RabbitMQ中。
        System.out.println("saveFallback方法执行：" + user);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "服务器忙，请稍后重试！");

        return result;
    }

    /**
     * 查询，只要路径和参数不变，理论上查询结果不变。可以缓存，提升查询效率。
     * @return
     */
    @Cacheable("springcloud_cache")
    public Map<String, Object> get(){
        ServiceInstance instance = this.loadBalancerClient.choose("ribbon-app-service");
        StringBuilder builder = new StringBuilder("");
        builder.append("http://").append(instance.getHost())
                .append(":").append(instance.getPort())
                .append("/get");
        System.out.println(builder.toString());
        RestTemplate template = new RestTemplate();
        ParameterizedTypeReference<Map<String, Object>> type =
                new ParameterizedTypeReference<Map<String, Object>>() {
                };
        ResponseEntity<Map<String, Object>> responseEntity =
                template.exchange(builder.toString(), HttpMethod.GET, null, type);
        return responseEntity.getBody();
    }

    /**
     * 写操作，数据写操作执行后，缓存数据失效，应该清理缓存。
     * @return
     */
    @CacheEvict("springcloud_cache")
    public Map<String, Object> post(){
        ServiceInstance instance = this.loadBalancerClient.choose("ribbon-app-service");
        StringBuilder builder = new StringBuilder("");
        builder.append("http://").append(instance.getHost())
                .append(":").append(instance.getPort())
                .append("/post");
        System.out.println(builder.toString());
        RestTemplate template = new RestTemplate();
        ParameterizedTypeReference<Map<String, Object>> type =
                new ParameterizedTypeReference<Map<String, Object>>() {
                };
        ResponseEntity<Map<String, Object>> responseEntity =
                template.exchange(builder.toString(), HttpMethod.GET, null, type);
        return responseEntity.getBody();
    }
}
