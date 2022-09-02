package com.pickme.reggie.pojo.dto;

import com.pickme.reggie.pojo.OrderDetail;
import com.pickme.reggie.pojo.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
