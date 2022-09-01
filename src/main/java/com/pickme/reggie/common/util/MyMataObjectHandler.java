package com.pickme.reggie.common.util;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器，实现公共字段自动填充
 */

@Slf4j
@Component
public class MyMataObjectHandler implements MetaObjectHandler {

    /**
     * 插入时执行
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //设置自动填充的实体类属性和填充的值，LocalDateTime.now() 从默认时区中获取当前的日期时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        // BaseContext.getCurrenId() 获取ThreadLoad中储存的登录用户id
        metaObject.setValue("createUser", LocalContext.getCurrentId());
        metaObject.setValue("updateUser", LocalContext.getCurrentId());
    }

    /**
     * 修改时执行
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", LocalContext.getCurrentId());
    }
}
