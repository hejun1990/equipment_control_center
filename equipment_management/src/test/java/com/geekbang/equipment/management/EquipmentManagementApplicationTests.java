package com.geekbang.equipment.management;

import com.geekbang.equipment.management.model.vo.DistributedQueryVO;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {EquipmentManagementApplication.class})
class EquipmentManagementApplicationTests {

    @Autowired
    private StringEncryptor encryptor;

    @Test
    public void encryptTest() {
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

    @Test
    public void optionalTest() {
        String a = "2";
        Integer b = Optional.ofNullable(a).map(s -> Integer.valueOf(s)).orElse(1);
        Assert.assertTrue(b.equals(2));
        DistributedQueryVO distributedQueryVO = new DistributedQueryVO();
        String cause = Optional.ofNullable(distributedQueryVO)
                .map(DistributedQueryVO::getCondition)
                .map(condition -> condition.getOrderByClause())
                .orElse("123");
        Assert.assertEquals("123", cause);
    }
}
