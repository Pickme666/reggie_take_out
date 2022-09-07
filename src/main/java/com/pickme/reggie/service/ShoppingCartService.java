package com.pickme.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.ShoppingCart;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {

    boolean saveToCart(ShoppingCart shoppingCart);

    boolean removeCart(ShoppingCart shoppingCart);

    boolean removeAllCart();

    List<ShoppingCart> listCarts();

}
