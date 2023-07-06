package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.entities.OrderDetail;
import com.Rst_harohiro.mapper.OrderDetailMapper;
import com.Rst_harohiro.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service

public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
