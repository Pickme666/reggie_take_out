package com.pickme.reggie.dto;

import com.pickme.reggie.pojo.Setmeal;
import com.pickme.reggie.pojo.SetmealDish;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("套餐 DTO")
@Data
public class SetmealDto extends Setmeal {

    //套餐菜品列表
    private List<SetmealDish> setmealDishes;

    //套餐分类名称
    private String categoryName;
}
