package com.bjsxt.userclient.service;

import com.bjsxt.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> save(User user);
    public Map<String, Object> get();
    public Map<String, Object> post();
}
