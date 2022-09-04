package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.mapper.CategoryMapper;
import com.pickme.reggie.pojo.Category;
import com.pickme.reggie.pojo.Dish;
import com.pickme.reggie.pojo.Setmeal;
import com.pickme.reggie.service.inter.CategoryService;
import com.pickme.reggie.service.inter.DishService;
import com.pickme.reggie.service.inter.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 删除分类时检查是否有关联菜品或套餐
     * @param id
     */
    @Override
    public boolean removeCategory(Long id) {
        //查询是否关联了菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        int count1 = dishService.count(dishWrapper.eq(Dish::getCategoryId, id));
        if (count1 > 0) throw new BusinessException("此分类关联了 "+ count1 +" 种菜品，不能删除");

        //查询是否有关联了套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        int count2 = setmealService.count(setmealWrapper.eq(Setmeal::getCategoryId, id));
        if (count2 > 0) throw new BusinessException("此分类关联了 "+ count2 +" 种套餐，不能删除");

        //正常删除分类
        return super.removeById(id);
    }

    @Override
    public Page<Category> pageCategory(Page<Category> page) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getUpdateTime);
        return this.page(page,queryWrapper);
    }

    @Override
    public List<Category> listCategoryOrSetmeal(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        //按sort字段排除，如果顺序相同，再按修改时间排序
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return this.list(wrapper);
    }

}
