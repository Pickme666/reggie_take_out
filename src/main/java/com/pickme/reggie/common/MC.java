package com.pickme.reggie.common;

/**
 * Message Centralize 响应消息常量集中管理
 */

public class MC {

    /**
     * 账号默认密码
     */
    public static final String DEFAULT_PWD = "123456";


    /**
     * 成功
     */

    public static final String S_INSERT = "添加成功";

    public static final String S_DELETE = "删除成功";

    public static final String S_UPDATE = "修改成功";

    public static final String S_SEND_MSG = "短信发送成功";


    /**
     * 失败
     */

    public static final String E_LOGIN_NO_NAME = "登录失败，用户名不存在";

    public static final String E_LOGIN_PWD = "登录失败，密码不正确";

    public static final String E_LOGIN_DISABLED = "账号已被禁用，无法登录";

    public static final String E_USERNAME_EXIST = "账号已存在";

    public static final String E_UNKNOWN = "未知错误：系统出现问题";

    public static final String E_DELETE_DISH = "当前菜品正在售卖中，不能删除";

    public static final String E_DELETE_SETMEAL = "当前套餐正在售卖中，不能删除";

    public static final String E_PHONE_NULL = "发送验证码失败，手机号码为空";

    public static final String E_CODE = "验证码错误";

    public static final String E_CAR_NULL = "购物车为空";

    public static final String E_ADDRESS = "地址信息异常，请重试";

    public static final String E_IMG = "上传图片错误，请重试";

}
