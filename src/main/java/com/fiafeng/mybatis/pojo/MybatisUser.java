package com.fiafeng.mybatis.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fiafeng.common.annotation.PojoAnnotation;
import com.fiafeng.common.pojo.Interface.IBaseUser;
import lombok.Data;
import lombok.experimental.Accessors;

@PojoAnnotation
@TableName()
@Data
@Accessors(chain = true)
public class MybatisUser implements IBaseUser {


    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

}
