package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.Category;
import com.pickme.reggie.service.inter.CategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 分类管理
 */
@Api(tags = "分类管理")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param category
     */
    @PostMapping
    public Res<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return Res.success("");
    }


    /**
     * 根据id删除分类
     * @param ids
     */
    @DeleteMapping()
    public Res<String> delete(Long ids) {
        categoryService.removeCategory(ids);
        return Res.success("");
    }

    /**
     * 根据id修改分类信息
     * @param category
     */
    @PutMapping
    public Res<Category> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Res.success(category);
    }


    /**
     * 分页查询分类信息
     * @param page
     * @param pageSize
     */
    @GetMapping("/page")
    public Res<Page<Category>> page(Integer page, Integer pageSize) {
        Page<Category> p = categoryService.pageCategory(new Page<>(page, pageSize));
        return Res.success(p);
    }

    /**
     * 查询菜品或套餐列表，菜品分类或套餐分类下拉菜单
     * @param category
     */
    @GetMapping("/list")
    public Res<List<Category>> selectList(Category category) {
        List<Category> list = categoryService.listCategoryOrSetmeal(category);
        return Res.success(list);
    }
}
