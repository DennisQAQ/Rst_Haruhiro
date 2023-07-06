package com.Rst_harohiro.service;

import com.Rst_harohiro.entities.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService extends IService<Orders> {
    @Transactional
    void submit(Orders orders);
}
