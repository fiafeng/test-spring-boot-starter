package com.fiafeng.common.properties.mysql;

import com.fiafeng.common.properties.IEnableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("fiafeng.mysql")
public class FiafengMysqlProperties implements IEnableProperties {

    /**
     * 是否使用默认mysql的相关内容
     */
    public Boolean enable = true;

    /**
     * 没有导入连接池的包时，是否打印执行的sql,参数等信息
     */
    public boolean logEnable = true;

    /**
     * 没有导入连接池的包时，保留连接的最大数量
     */
    public int poolMaxSize = 10;

    /**
     * 没有导入连接池的包时，自定义链接池核心连接数
     */
    public int poolMimSize = 4;

}
