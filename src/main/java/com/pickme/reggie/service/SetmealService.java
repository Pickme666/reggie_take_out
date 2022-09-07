package com.pickme.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.Setmeal;
import com.pickme.reggie.pojo.dto.SetmealDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 添加
     * @param setmealDto
     */
    @Transactional
    boolean saveWithDish(SetmealDto setmealDto);

    /**
     * 删除
     * @param ids
     */
    @Transactional
    boolean removeWithDish(List<Long> ids);

    /**
     * 修改
     * @param setmealDto
     */
    @Transactional
    boolean updateWithDish(SetmealDto setmealDto);

    /**
     * 修改状态
     * @param sta
     * @param ids
     * @return
     */
    boolean updateStatus(Integer sta, Long[] ids);

    /**
     * 根据id查询
     * @param id
     */
    SetmealDto getByWithDish(Long id);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     */
    Page<SetmealDto> pageWithCategory(Integer page, Integer pageSize, String name);

    /**
     * 根据分类id查询套餐列表
     * @param setmeal
     * @return
     */
    List<Setmeal> listByCategoryId(Setmeal setmeal);
}
