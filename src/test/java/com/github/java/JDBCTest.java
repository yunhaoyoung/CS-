package com.github.java;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.github.java.util.CommUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;



import java.sql.*;

import java.util.Properties;

public class JDBCTest {
    private static DruidDataSource dataSource = null;
    static {
        Properties properties = CommUtils.loadProperties("datasource.properties");
        try {
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Query(){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "select *from user ";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                System.out.println("id:"+resultSet.getInt("id")+" username: "+resultSet.getString("username")+" password:"+resultSet.getString("password")+" brief:"+resultSet.getString("brief"));
            }

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            closeState(connection,statement,resultSet);
        }
    }

    @Test
    public void insertQuery(){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String pass = DigestUtils.md5Hex("123456");
            String sql = "insert into user(username, password, brief) value (?,?,?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,"lisi");
            statement.setString(2,pass);
            statement.setString(3, "帅");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(connection,statement);
        }
    }
    @Test
    public void DelecteQuery(){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String sql = "delete from user where username = ?";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,"lisi");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConn(connection,statement);
        }
    }
    private void closeConn(Connection connection, Statement statement){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void login(){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = (Connection) dataSource.getPooledConnection();
            String userName = "zhangsan";
            String password = "123456";
            String sql = "select * from user where username = '"+userName+"'"+"and password = '"+password+"'";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                System.out.println("登录成功");
            } else{
                System.out.println("登录失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeState(connection,statement,resultSet);
        }
    }

    private void closeState(Connection connection, Statement statement,ResultSet resultSet){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
