package io.kimmking.cache.controller;

import io.kimmking.cache.component.RedisLock;
import io.kimmking.cache.entity.User;
import io.kimmking.cache.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@EnableAutoConfiguration
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisLock redisLock;
    
    @RequestMapping("/user/find")
    User find(Integer id) {

        String value = String.valueOf(new Random(10000).nextInt());

        redisLock.tryLockUser(value);

        User user = userService.find(id);

        redisLock.unlockUser(value);

        return user;

        //return new User(1,"KK", 28);
    }

    @RequestMapping("/user/list")
    List<User> list() {
        return userService.list();
//        return Arrays.asList(new User(1,"KK", 28),
//                             new User(2,"CC", 18));
    }
}