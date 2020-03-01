package com.bjsxt.userservice.controller;

import com.bjsxt.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 提供服务的控制器
 */
@Controller
public class UserController {
    @GetMapping("/post")
    @ResponseBody
    public Map<String, Object> post(){
        System.out.println("post method run");
        Map<String, Object> result = new HashMap<>();
        result.put("message", "post方法执行成功");
        return result;
    }

    @GetMapping("/get")
    @ResponseBody
    public Map<String, Object> get(){
        System.out.println("get method run");
        Map<String, Object> result = new HashMap<>();
        result.put("message", "get方法执行成功");
        return result;
    }

    @RequestMapping("/user/save")
    @ResponseBody
    public Map<String, Object> save(User user){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("新增用户数据： " + user);
        Map<String, Object> result = new HashMap<>();
        result.put("code", "200"); // 返回的状态码
        result.put("message", "新增用户成功"); // 返回的处理结果消息。
        return result;
    }
}
