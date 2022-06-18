package com.itheima.reggie.utils;

/**
 * 基于ThreadLocal封装工具类，用于在线程内部的多个方法间传递数据，eg：登录的用户ID
 */
public class BaseContextUtil {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}