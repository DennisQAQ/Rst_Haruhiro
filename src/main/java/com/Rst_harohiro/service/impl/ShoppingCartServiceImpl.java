package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.entities.ShoppingCart;
import com.Rst_harohiro.mapper.ShoppingCartMapper;
import com.Rst_harohiro.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
