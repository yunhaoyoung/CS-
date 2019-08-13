package com.github.java.client.dao;

import com.github.java.client.entity.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDaoTest {
    AccountDao accountDao = new AccountDao();
    @Test
    public void regist() {
        User user = new User();
        user.setUsername("wangwu");
        user.setPassword("123");
        user.setBrief("shuai");
        boolean flag = accountDao.regist(user);
        assertTrue(flag);
    }

    @Test
    public void login() {
        String username = "wangwu";
        String password = "123";
        User user = accountDao.login(username,password);
        System.out.println(user);
        assertNotNull(user);
    }
}