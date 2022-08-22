package com.pickme.reggie.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，用于封装后端响应的数据
 */

@Data
public class R<T> {

    //编码：1成功，0和其它数字为失败
    private Integer code;

    //提示信息
    private String msg;

    //数据
    private T data;

    //动态数据
    private Map map = new HashMap();

    //成功 code = 1
    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.data = object;
        r.code = 1;
        return r;
    }

    //失败 code = 0
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    //操作动态数据
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
