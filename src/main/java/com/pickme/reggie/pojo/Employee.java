package com.pickme.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工表实体类
 */
@ApiModel("员工")
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //账号
    private String username;

    //姓名
    private String name;

    //密码
    private String password;

    //手机号
    private String phone;

    //性别
    private String sex;

    //身份证号
    private String idNumber;

    //状态：0 禁用，1 正常
    private Integer status;


    /**
     * MybatisPlus公共字段自动填充功能。
     * 以下字段在多个表中拥有，为了避免大量重复的代码，通过指定字段的填充策略，在执行的时候统一对这些字段进行处理
     */

    //创建时间
    @TableField(fill = FieldFill.INSERT) //插入时填充
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和修改时填充
    private LocalDateTime updateTime;

    //创建者
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    //更新者
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
