package com.github.java.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.github.java.util.CommUtils;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BasedDao {
    private static DruidDataSource dataSource;
    static {
        Properties properties = CommUtils.loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            System.err.println("数据源获取失败");
            e.printStackTrace();
        }
    }

    protected DruidPooledConnection getConnection(){
        try {
            return (DruidPooledConnection) dataSource.getPooledConnection();
        } catch (SQLException e) {
            System.err.println("数据库连接获取失败");
            e.printStackTrace();
        }
        return null;
    }

    protected void closeResource(Connection connection, Statement statement){
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeResource(Connection connection, Statement statement, ResultSet resultSet){
        closeResource(connection,statement);
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
