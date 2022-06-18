package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Vsunks.v
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description:
 */
@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    // 该成员变量序列化时，会序列化为String类型
     @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String name;
    private String password;
    private String phone;
    private String sex;
    //驼峰命名法 ---> 映射的字段名为 id_number
    private String idNumber;
    //0 禁用 1 正常
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
