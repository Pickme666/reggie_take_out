package com.pickme.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pickme.reggie.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
