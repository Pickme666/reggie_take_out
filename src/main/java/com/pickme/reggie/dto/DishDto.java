package com.pickme.reggie.dto;

import com.pickme.reggie.pojo.Dish;
import com.pickme.reggie.pojo.DishFlavor;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO：Data Transfer Object (数据传输对象)，一般用于展示层与服务层之间的数据传输。
 */

@ApiModel("菜品 DTO")
@Data
public class DishDto extends Dish {

    //口味做法配置列表
    private List<DishFlavor> flavors = new ArrayList<>();

    //菜品分类名
    private String categoryName;

    private Integer copies;
}
