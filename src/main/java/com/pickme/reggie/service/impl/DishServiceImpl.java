package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.dto.DishDto;
import com.pickme.reggie.entity.Category;
import com.pickme.reggie.entity.Dish;
import com.pickme.reggie.entity.DishFlavor;
import com.pickme.reggie.mapper.DishMapper;
import com.pickme.reggie.service.inter.CategoryService;
import com.pickme.reggie.service.inter.DishFlavorService;
import com.pickme.reggie.service.inter.DishService;
import com.sun.org.apache.bcel.internal.generic.LADD;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品以及保存口味信息
     * @param dishDto
     * @return
     */
    @Override
    public boolean saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息
        this.save(dishDto);
        //获取保存的菜品ID
        Long dishId = dishDto.getId();
        //获取菜品口味列表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //采用Stream流来遍历列表，为菜品口味对象属性dishId赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList()); //返回一个新的List
        //flavors.forEach((item) -> flavor.setDishId(dishId));
        //批量保存菜品口味列表
        return dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除或批量删除菜品和口味数据
     * @param ids
     * @return
     */
    @Override
    public boolean removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(Dish::getId,ids);
        dishWrapper.eq(Dish::getStatus,1);
        if (this.count(dishWrapper) > 0) {
            throw new BusinessException(MC.E_DELETE_DISH);
        }

        this.removeByIds(ids);

        LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
        flavorWrapper.in(DishFlavor::getDishId,ids);
        return dishFlavorService.remove(flavorWrapper);
    }

    /**
     * 修改菜品基本信息和口味信息
     * @param dishDto
     * @return
     */
    @Override
    public boolean updateWithFlavor(DishDto dishDto) {
        long id = dishDto.getId();
        //修改菜品基本数据
        this.updateById(dishDto);
        //设置条件，根据菜品id删除原有的口味数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(wrapper);
        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        return dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品基本信息和口味信息，将数据合并为 DishDto 对象返回
     * @param id
     * @return DishDto
     */
    @Override
    public DishDto getByWithFlavor(Long id) {
        //根据ID查询菜品的基本信息
        Dish dish = this.getById(id);
        //根据菜品的ID查询菜品口味列表数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);
        //合并数据并返回
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 分页查询菜品的分类信息，并和菜品的基本信息合并，返回一个新的分页模型对象
     * @param dishPage 菜品基本信息分页对象
     * @param wrapper 分页条件
     * @return 泛型为 DishDto 的 Page
     */
    @Override
    public Page<DishDto> pageWithCategory(Page<Dish> dishPage, Wrapper<Dish> wrapper) {
        //构造一个泛型为 DishDto 的分页模型对象
        Page<DishDto> dishDtoPage = new Page<>();
        //执行分页条件查询，并返回查询数据列表
        List<Dish> dishList = this.page(dishPage, wrapper).getRecords();
        //对象拷贝，将dishPage的封装的数据拷贝到dishDtoPage拷贝，排除 records 属性
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        //遍历分页查询的菜品基本信息列表数据，根据分类ID查询分类信息，从而获取该菜品的分类名称
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            long categoryId = item.getCategoryId();     //获取分类id，根据分类id查询分类信息
            Category category = categoryService.getById(categoryId);
            DishDto dishDto = new DishDto();            //构造 DishDto 对象来封装分类信息
            BeanUtils.copyProperties(item,dishDto);     //对象拷贝，将菜品基本信息数据拷贝到 DishDto 对象
            if (category != null) {                     //如果查询的分类信息不为空，设置此菜品的分类名
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;                              //返回 DishDto 对象
        }).collect(Collectors.toList());                 //返回一个新的集合 List<DishDto> dishDtoList
        dishDtoPage.setRecords(dishDtoList);             //封装 dishDtoList 列表数据
        return dishDtoPage;                              //返回 Page<DishDto> 分页模型对象
    }

}