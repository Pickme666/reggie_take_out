package com.pickme.reggie.dto;

import com.pickme.reggie.entity.Dish;
import com.pickme.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO：Data Transfer Object (数据传输对象)，一般用于展示层与服务层之间的数据传输。
 */

@Data
public class DishDto extends Dish {

    //口味做法配置列表
    private List<DishFlavor> flavors = new ArrayList<>();

    //菜品分类名
    private String categoryName;

    private Integer copies;
}
