package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除
     * @param id
     * @return
     */
    boolean remove(Long id);
}
