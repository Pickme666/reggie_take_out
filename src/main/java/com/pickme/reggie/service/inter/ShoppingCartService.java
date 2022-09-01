package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    boolean cleanCart();
}
