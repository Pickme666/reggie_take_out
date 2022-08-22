package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.R;
import com.pickme.reggie.dto.SetmealDto;
import com.pickme.reggie.entity.Setmeal;
import com.pickme.reggie.service.inter.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("");
    }


    /**
     * 删除或批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("");
    }


    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("");
    }

    /**
     * 修改和批量修改套餐状态，启售或停售
     * @param s
     * @param ids
     */
    @PostMapping("/status/{s}")
    public R<String> status(@PathVariable Integer s, Long[] ids) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(s);
        for (Long id : ids) {
            setmeal.setId(id);
            setmealService.updateById(setmeal);
        }
        return R.success("");
    }


    /**
     * 根据id查询，回显套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getObject(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 分页查询套餐基本信息和分类信息
     * @param page
     * @param pageSize
     * @param name
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        //分页查询套餐基本和信息分类信息，将数据合并，返回 Page<SetmealDto> 对象
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = setmealService.pageWithCategory(setmealPage, wrapper);
        return R.success(setmealDtoPage);
    }
}
