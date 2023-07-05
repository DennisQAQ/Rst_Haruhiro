package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.entities.User;
import com.Rst_harohiro.mapper.userMapper;
import com.Rst_harohiro.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class userServiceImpl extends ServiceImpl<userMapper, User> implements UserService {
}
