package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.dto.SetmealDto;
import com.pickme.reggie.entity.Category;
import com.pickme.reggie.entity.Setmeal;
import com.pickme.reggie.entity.SetmealDish;
import com.pickme.reggie.mapper.SetmealMapper;
import com.pickme.reggie.service.inter.CategoryService;
import com.pickme.reggie.service.inter.SetmealDishService;
import com.pickme.reggie.service.inter.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐和保存包含的菜品
     * @param setmealDto
     * @return
     */
    @Override
    public boolean saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        //获取保存数据后生成的id
        Long id = setmealDto.getId();
        //获取套餐关联的菜品集合，并为集合中的每一个元素赋值套餐ID (setmealId)
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        //批量保存套餐关联的菜品集合
        return setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除或批量删除套餐信息和套餐菜品信息
     * @param ids
     * @return
     */
    @Override
    public boolean removeWithDish(List<Long> ids) {
        //查询该批次套餐中是否存在售卖中的套餐, 如果存在, 不允许删除
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.in(Setmeal::getId,ids);
        setmealWrapper.eq(Setmeal::getStatus,1);
        if (this.count(setmealWrapper) > 0) {
            throw new BusinessException(MC.E_DELETE_SETMEAL);
        }
        //删除套餐数据
        this.removeByIds(ids);
        //删除套餐关联的菜品数据
        LambdaQueryWrapper<SetmealDish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(SetmealDish::getSetmealId,ids);
        return setmealDishService.remove(dishWrapper);
    }

    /**
     * 修改套餐基本信息和套餐菜品信息
     * @param setmealDto
     * @return
     */
    @Override
    public boolean updateWithDish(SetmealDto setmealDto) {
        long id = setmealDto.getId();
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(wrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        return setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 查询套餐的基本信息和套餐菜品信息，将数据合并为 SetmealDto 对象返回
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(wrapper);

        SetmealDto setmealDto = new SetmealDto();
        setmealDto.setSetmealDishes(list);
        BeanUtils.copyProperties(setmeal,setmealDto);
        return setmealDto;
    }

    /**
     * 分页查询套餐的分类信息，并和套餐的基本信息合并，返回一个新的分页模型对象
     * @param setmealPage
     * @param wrapper
     * @return
     */
    @Override
    public Page<SetmealDto> pageWithCategory(Page<Setmeal> setmealPage, Wrapper<Setmeal> wrapper) {
        Page<SetmealDto> setmealDtoPage = new Page<>();
        List<Setmeal> setmealList = this.page(setmealPage, wrapper).getRecords();
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);
        return setmealDtoPage;
    }

}