package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.mapper.DishMapper;
import com.pickme.reggie.pojo.Category;
import com.pickme.reggie.pojo.Dish;
import com.pickme.reggie.pojo.DishFlavor;
import com.pickme.reggie.pojo.dto.DishDto;
import com.pickme.reggie.service.CategoryService;
import com.pickme.reggie.service.DishFlavorService;
import com.pickme.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "DishCache",key = "#dishDto.categoryId")
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
    @CacheEvict(value = "DishCache",allEntries = true)
    public boolean removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(Dish::getId,ids);
        dishWrapper.eq(Dish::getStatus,1);

        if (this.count(dishWrapper) > 0) throw new BusinessException(MC.E_DELETE_DISH);
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
    @CacheEvict(value = "DishCache",allEntries = true)
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
     * 修改或批量修改菜品状态
     * @param sta
     * @param ids
     * @return
     */
    @Override
    @CacheEvict(value = "DishCache",allEntries = true)
    public boolean updateStatus(Integer sta, Long[] ids) {
        Dish dish = new Dish();
        dish.setStatus(sta);
        //遍历id数组，每次遍历都为Dish对象设置不同的id值
        for (Long id : ids) {
            dish.setId(id);
            this.updateById(dish);
        }
        return true;
    }

    /**
     * 查询菜品基本信息和口味信息，将数据合并为 DishDto 对象返回
     * @param id
     * @return DishDto
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
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
     * @param page
     * @param pageSize
     * @param name
     * @return 泛型为 DishDto 的 Page
     */
    @Override
    public Page<DishDto> pageWithCategory(Integer page, Integer pageSize, String name) {

        //设置分页查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        //构造分页模型对象，分页查询菜品基本信息
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //构造一个泛型为 DishDto 的分页模型对象
        Page<DishDto> dishDtoPage = new Page<>();
        //执行分页条件查询，并返回查询数据列表
        List<Dish> dishList = this.page(dishPage, wrapper).getRecords();
        //对象拷贝，将dishPage的封装的数据拷贝到dishDtoPage拷贝，排除 records 属性
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        //遍历分页查询的菜品基本信息列表数据，根据分类ID查询分类信息，从而获取该菜品的分类名称
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            //构造 DishDto 对象来封装分类信息
            DishDto dishDto = new DishDto();
            //对象拷贝，将菜品基本信息数据拷贝到 DishDto 对象
            BeanUtils.copyProperties(item,dishDto);
            //根据分类id查询分类信息
            Category category = categoryService.getById(item.getCategoryId());
            //如果查询的分类信息不为空，设置此菜品的分类名
            if (category != null) dishDto.setCategoryName(category.getName());
            //返回 DishDto 对象
            return dishDto;
        //返回一个新的集合 List<DishDto> dishDtoList
        }).collect(Collectors.toList());

        //封装 dishDtoList 列表数据到 Page<DishDto> 分页模型对象并返回
        dishDtoPage.setRecords(dishDtoList);
        return dishDtoPage;
    }

    @Override
    @Cacheable(value = "DishCache",key = "#dish.categoryId")
    public List<DishDto> listByCategoryId(Dish dish) {
        Long categoryId = dish.getCategoryId();
        String name = dish.getName();

        //构造条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(Dish::getStatus,1)
                .eq(categoryId != null,Dish::getCategoryId,categoryId)
                .like(name != null,Dish::getName,name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = this.list(wrapper);

        //遍历集合，查询每个菜品的口味数据
        return list.stream().map((item) -> {
            Long dishID = item.getId();
            return this.getByIdWithFlavor(dishID);
        }).collect(Collectors.toList());
    }

}