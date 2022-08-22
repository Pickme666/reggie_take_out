package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.dto.SetmealDto;
import com.pickme.reggie.entity.Setmeal;
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
     * 根据id查询
     * @param id
     */
    SetmealDto getByWithDish(Long id);

    /**
     * 分页查询
     * @param page
     * @param wrapper
     */
    Page<SetmealDto> pageWithCategory(Page<Setmeal> page, Wrapper<Setmeal> wrapper);
}
