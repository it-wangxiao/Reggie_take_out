package com.itheima.reggie.web;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Vsunks.v
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description:
 */

@Data
public class R<T> {
    private Integer code;               //编码：1成功，0和其它数字为失败
    private String msg;                 //错误信息
    private T data;                     //数据
    private Map map = new HashMap();    //动态数据

    /**
     * 快捷创建R对象，封装成功的提示信息和数据
     * @param msg
     * @param object
     * @param <T>
     * @return
     */
    public static <T> R<T> success(String msg, T object) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.data = object;
        r.code = 1;
        return r;
    }

    /**
     * 快捷创建Result对象，封装成功的提示信息
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> success(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 1;
        return r;
    }

    /**
     * 快捷创建Result对象，封装失败的提示信息
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
