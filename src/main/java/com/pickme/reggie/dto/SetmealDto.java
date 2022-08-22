package com.pickme.reggie.dto;

import com.pickme.reggie.entity.Setmeal;
import com.pickme.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //套餐菜品列表
    private List<SetmealDish> setmealDishes;

    //套餐分类名称
    private String categoryName;
}
