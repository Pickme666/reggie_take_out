package com.pickme.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除
     * @param id
     */
    boolean removeCategory(Long id);

    Page<Category> pageCategory(Page<Category> page);

    List<Category> listCategoryOrSetmeal(Category category);
}
