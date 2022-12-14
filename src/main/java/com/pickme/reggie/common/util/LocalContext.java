package com.pickme.reggie.common.util;

/**
 * 基于 ThreadLocal 封装的工具类，用于保存和获取当前登录用户id，即Session中的数据
 */

public class LocalContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return 登录用户id
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
