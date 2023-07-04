package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.mapper.DishMapper;
import com.Rst_harohiro.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
