package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.reggie.utils.BaseContextUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/9 17:45
 * 自动填充类，填充处理器。
 */
@Component
public class TimeAndOpMetaObjectHandler implements MetaObjectHandler {
    /**
     * 当添加一条记录时，如果对一个的实体类的成员变量上有@TableField(fill=xx)
     * 且fill属性的值为FieldFill.INSERT。就会自动调用该方法，为实体类对象的成员赋值
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // setValue(name,value) 为某个成员变量赋值
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 获取当前用户id
        Long id = BaseContextUtil.getCurrentId();
        metaObject.setValue("createUser", id);
        metaObject.setValue("updateUser", id);
    }

    /**
     * 当添加一条记录时，如果对一个的实体类的成员变量上有@TableField(fill=xx)
     * 且fill属性的值为FieldFill.UPDATE。就会自动调用该方法，为实体类对象的成员赋值
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // setValue(name,value) 为某个成员变量赋值
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 获取当前用户id
        Long id = BaseContextUtil.getCurrentId();
        metaObject.setValue("updateUser", id);
    }
}
