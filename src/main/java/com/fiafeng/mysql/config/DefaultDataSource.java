package com.fiafeng.mysql.config;

import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DefaultDataSource implements DataSource {


    @Value("${spring.datasource.url}")
    private String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";

    @Value("${spring.datasource.username}")
    private String username = "root";

    @Value("${spring.datasource.password}")
    private String password = "123456";


    @Override
    public Connection getConnection() {
        return getConnection(username,password);
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
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
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
