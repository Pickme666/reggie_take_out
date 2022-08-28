package com.pickme.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，用于封装后端响应的数据
 */

@Data
public class Res<T> implements Serializable {

    //编码：1成功，0和其它数字为失败
    private Integer code;

    //提示信息
    private String msg;

    //数据
    private T data;

    //动态数据
    private Map map = new HashMap();

    //成功 code = 1
    public static <T> Res<T> success(T object) {
        Res<T> res = new Res<>();
        res.data = object;
        res.code = 1;
        return res;
    }

    //失败 code = 0
    public static <T> Res<T> error(String msg) {
        Res<T> res = new Res<>();
        res.msg = msg;
        res.code = 0;
        return res;
    }

    //操作动态数据
    public Res<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
