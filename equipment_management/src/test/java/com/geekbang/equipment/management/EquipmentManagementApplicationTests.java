package com.geekbang.equipment.management;

import com.geekbang.equipment.management.model.DeviceInfo;
import com.geekbang.equipment.management.model.vo.DistributedQueryVO;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Condition;

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
        DistributedQueryVO distributedQueryVO = new DistributedQueryVO();
        Condition condition = new Condition(DeviceInfo.class);
        condition.setOrderByClause("create_time desc");
        distributedQueryVO.setCondition(condition);
        String orderBy = Optional.ofNullable(distributedQueryVO)
                .map(DistributedQueryVO::getCondition)
                .map(condition1 -> condition1.getOrderByClause())
                .orElse("default");
        System.out.println("orderBy = " + orderBy);
        Assert.assertEquals("create_time desc", orderBy);
    }
}
