package com.pickme.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.Dish;
import com.pickme.reggie.pojo.dto.DishDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DishService extends IService<Dish> {

    /**
     * 添加
     * @param dishDto
     */
    @Transactional
    boolean saveWithFlavor(DishDto dishDto);

    /**
     * 删除
     * @param ids
     */
    @Transactional
    boolean removeWithFlavor(List<Long> ids);

    /**
     * 修改
     * @param dishDto
     */
    @Transactional
    boolean updateWithFlavor(DishDto dishDto);


    /**
     * 修改状态
     * @param sta
     * @param ids
     */
    @Transactional
    boolean updateStatus(Integer sta, Long[] ids);


    /**
     * 根据id查询
     * @param id
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     */
    Page<DishDto> pageWithCategory(Integer page, Integer pageSize, String name);

    /**
     * 根据分类id查询菜品（移动端）
     * @param dish
     */
    List<DishDto> listByCategoryId(Dish dish);

}
