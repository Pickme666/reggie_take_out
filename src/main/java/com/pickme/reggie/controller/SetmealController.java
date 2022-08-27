package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.dto.SetmealDto;
import com.pickme.reggie.pojo.Setmeal;
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
    public Res<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Res.success("");
    }


    /**
     * 删除或批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public Res<String> remove(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return Res.success("");
    }


    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Res<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return Res.success("");
    }

    /**
     * 修改和批量修改套餐状态，启售或停售
     * @param s
     * @param ids
     */
    @PostMapping("/status/{s}")
    public Res<String> status(@PathVariable Integer s, Long[] ids) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(s);
        for (Long id : ids) {
            setmeal.setId(id);
            setmealService.updateById(setmeal);
        }
        return Res.success("");
    }


    /**
     * 根据id查询，回显套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Res<SetmealDto> getObject(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByWithDish(id);
        return Res.success(setmealDto);
    }

    /**
     * 分页查询套餐基本信息和分类信息
     * @param page
     * @param pageSize
     * @param name
     */
    @GetMapping("/page")
    public Res<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        //分页查询套餐基本和信息分类信息，将数据合并，返回 Page<SetmealDto> 对象
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = setmealService.pageWithCategory(setmealPage, wrapper);
        return Res.success(setmealDtoPage);
    }

    /**
     * 根据分类id查询套餐列表（移动端）
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public Res<List<Setmeal>> list(Setmeal setmeal) {
        Long categoryId = setmeal.getCategoryId();
        Integer status = setmeal.getStatus();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null,Setmeal::getCategoryId,categoryId);
        wrapper.eq(status != null,Setmeal::getStatus,status);
        List<Setmeal> list = setmealService.list(wrapper);
        return Res.success(list);
    }
}
