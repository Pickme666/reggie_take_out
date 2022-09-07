package com.pickme.reggie.common;

import lombok.Data;
import java.io.Serializable;

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


    /**
     * 成功 code = 1
     */
    public static <T> Res<T> success(T object) {
        Res<T> res = new Res<>();
        res.code = 1;
        res.data = object;
        return res;
    }

    public static <T> Res<T> success(T object, String msg) {
        Res<T> res = new Res<>();
        res.code = 1;
        res.data = object;
        res.msg = msg;
        return res;
    }


    /**
     * 失败 code = 0
     */
    public static <T> Res<T> error(String msg) {
        Res<T> res = new Res<>();
        res.code = 0;
        res.msg = msg;
        return res;
    }
}
