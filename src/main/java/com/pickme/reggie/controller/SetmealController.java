package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.Setmeal;
import com.pickme.reggie.pojo.dto.SetmealDto;
import com.pickme.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@Api(tags = "套餐管理")
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐
     * @param setmealDto
     */
    @PostMapping
    public Res<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return Res.success("");
    }


    /**
     * 删除或批量删除套餐
     * @param ids
     */
    @DeleteMapping
    public Res<String> remove(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return Res.success("");
    }


    /**
     * 修改套餐信息
     * @param setmealDto
     */
    @PutMapping
    public Res<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return Res.success("");
    }

    /**
     * 修改和批量修改套餐状态，启售或停售
     * @param sta
     * @param ids
     */
    @PostMapping("/status/{sta}")
    public Res<String> status(@PathVariable Integer sta, Long[] ids) {
        setmealService.updateStatus(sta,ids);
        return Res.success("");
    }


    /**
     * 根据id查询，回显套餐信息
     * @param id
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
        Page<SetmealDto> setmealDtoPage = setmealService.pageWithCategory(page, pageSize, name);
        return Res.success(setmealDtoPage);
    }

    /**
     * 根据分类id查询套餐列表（移动端）
     * @param setmeal
     */
    @GetMapping("/list")
    public Res<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> setmealList = setmealService.listByCategoryId(setmeal);
        return Res.success(setmealList);
    }
}
