package com.Rst_harohiro.dto;


import com.Rst_harohiro.entities.OrderDetail;
import com.Rst_harohiro.entities.Orders;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderDTO extends Orders implements Serializable {
    private int sumNum;

    List<OrderDetail> OrderDetails;
}
