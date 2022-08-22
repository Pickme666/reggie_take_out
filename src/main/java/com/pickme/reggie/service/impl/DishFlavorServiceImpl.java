package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.entity.DishFlavor;
import com.pickme.reggie.mapper.DishFlavorMapper;
import com.pickme.reggie.service.inter.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
