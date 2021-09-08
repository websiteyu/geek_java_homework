package com.mine.homework05;

import com.mine.homework05.bean.Group;
import com.mine.homework05.bean.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class Homework05ApplicationTests {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        User user = (User)applicationContext.getBean("user");
        System.out.println(user);
        User user2 = (User)applicationContext.getBean("user2");
        System.out.println(user2);

        Group group = (Group) applicationContext.getBean("group");
        System.out.println(group);
    }

}
