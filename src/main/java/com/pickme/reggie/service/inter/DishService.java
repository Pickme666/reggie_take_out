package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.dto.DishDto;
import com.pickme.reggie.pojo.Dish;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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
     * @param s
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
     * @param dishPage
     * @param wrapper
     */
    Page<DishDto> pageWithCategory(Page<Dish> dishPage, Wrapper<Dish> wrapper);

    /**
     * 根据分类id查询菜品（移动端）
     * @param dish
     */
    List<DishDto> listByCategoryId(Dish dish);

}
