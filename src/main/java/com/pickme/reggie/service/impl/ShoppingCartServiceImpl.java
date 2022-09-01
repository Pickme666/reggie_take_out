package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.mapper.ShoppingCartMapper;
import com.pickme.reggie.pojo.ShoppingCart;
import com.pickme.reggie.service.inter.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

    /**
     * 清空购物车
     * @return
     */
    @Override
    public boolean cleanCart() {
        Long userId = LocalContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null,ShoppingCart::getUserId,userId);
        return this.remove(wrapper);
    }
}
