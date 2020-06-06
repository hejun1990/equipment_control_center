package com.geekbang.equipment.management;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EquipmentManagementApplication.class})
class EquipmentManagementApplicationTests {

    @Autowired
    private StringEncryptor encryptor;

    @Test
    public void getPass() {
        String url = encryptor.encrypt("jdbc:mysql://127.0.0.1:3306/equipment_control_center?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false");
        String name = encryptor.encrypt("你的数据库用户名");
        String password = encryptor.encrypt("你的数据库密码");
        System.out.println("url: " + url);
        System.out.println("name: " + name);
        System.out.println("password: " + password);
        Assert.assertTrue(url.length() > 0);
        Assert.assertTrue(name.length() > 0);
        Assert.assertTrue(password.length() > 0);
    }
}
