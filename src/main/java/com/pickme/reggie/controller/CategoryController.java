package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.R;
import com.pickme.reggie.entity.Category;
import com.pickme.reggie.service.inter.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类管理
 */

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("");
    }


    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long ids) {
        categoryService.remove(ids);
        return R.success("");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<Category> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success(category);
    }


    /**
     * 分页查询分类信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page,Integer pageSize) {
        Page<Category> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getUpdateTime);
        categoryService.page(p,queryWrapper);
        return R.success(p);
    }

    /**
     * 查询菜品或套餐列表，菜品分类或套餐分类下拉菜单
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> selectList(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        //按sort字段排除，如果顺序相同，再按修改时间排序
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }
}