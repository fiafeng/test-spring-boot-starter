package com.fiafeng.common.config.bean;

import com.fiafeng.common.utils.spring.FiafengSpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DefaultDataSource implements DataSource {


    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8}")
    private String url;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:123456}")
    private String password;


    @Autowired
    Environment environment;

    @Override
    public Connection getConnection() {
        return getConnection(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) {

        Connection connection = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (Exception e) {
                throw new RuntimeException("无法加载到驱动（com.mysql.jdbc.Driver，com.mysql.cj.jdbc.Driver），请检查是否导入了mysql驱动");
            }
        }
        try {

            if (environment == null) {
                environment = FiafengSpringUtils.getBean(Environment.class);
            }

            if (url == null) {
                url = environment.getProperty("spring.datasource.url");
            }
            if (username == null) {
                username = environment.getProperty("spring.datasource.username");
            }
            if (password == null) {
                password = environment.getProperty("spring.datasource.password");
            }

            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> face) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> face) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
