package com.bjsxt.userclient.controller;

import com.bjsxt.entity.User;
import com.bjsxt.userclient.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 服务消费端
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/post")
    @ResponseBody
    public Object post(){
        return this.userService.post();
    }

    @GetMapping("/get")
    @ResponseBody
    public Object get(){
        return this.userService.get();
    }

    @RequestMapping("/user/save")
    @ResponseBody
    public Map<String, Object> save(User user){
        // 调用本地服务代码，本地服务代码远程调用application service服务提供方。
        Map<String, Object> result = this.userService.save(user);
        System.out.println("远程调用返回的结果：" + result);

        return result;
    }

}
