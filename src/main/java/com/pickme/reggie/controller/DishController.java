package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.R;
import com.pickme.reggie.dto.DishDto;
import com.pickme.reggie.entity.Dish;
import com.pickme.reggie.service.inter.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 菜品管理
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("");
    }


    /**
     * 删除和批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.removeWithFlavor(ids);
        return R.success("");
    }


    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("");
    }

    /**
     * 修改和批量修改菜品状态，启售或停售
     * @param s
     * @param ids
     * @return
     */
    @PostMapping("/status/{s}")
    public R<String> status(@PathVariable Integer s, Long[] ids) {
        Dish dish = new Dish();
        dish.setStatus(s);
        //遍历id数组，每次遍历都为Dish对象设置不同的id值
        for (Long id : ids) {
            dish.setId(id);
            dishService.updateById(dish);
        }
        return R.success("");
    }


    /**
     * 根据id查询，回显菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getObject(@PathVariable Long id) {
        DishDto dishDto = dishService.getByWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 分页查询菜品信息，除了查询菜品的基本信息外，还要根据菜品分类id来查询分类信息。
     * @param page
     * @param pageSize
     * @param name
     * @return 泛型为 DishDto 的 Page，因为 DishDto 类带有 categoryName 属性来封装分类名称
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page,Integer pageSize,String name) {
        //设置分页查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        //构造分页模型对象，分页查询菜品基本信息
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //调用业务层方法，返回带有菜品基本信息和分类信息的 Page<DishDto> 对象
        Page<DishDto> dishDtoPage = dishService.pageWithCategory(dishPage, wrapper);
        //返回 dishDtoPage
        return R.success(dishDtoPage);
    }

    /**
     * 套餐菜品查询，根据分类id或名称查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        Long categoryId = dish.getCategoryId();
        String name = dish.getName();
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Dish::getStatus,1)
                .eq(categoryId != null,Dish::getCategoryId,categoryId)
                .like(name != null,Dish::getName,name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime)
        ;
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }
}
