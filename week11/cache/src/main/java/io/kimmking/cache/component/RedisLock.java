package io.kimmking.cache.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    private long timeout = 30000;

    private final String USER_LOCK = "userLock";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DefaultRedisScript<Boolean> unlockRedisScript;

    @Resource
    private DefaultRedisScript<Boolean> tryLockRedisScript;

    public Boolean tryLock(String key,String value) {
//        String value = String.valueOf(new Random(25).nextInt());
//        需要事务操作
//        事务操作 setIfAbsent 会返回 null?
//        boolean store = stringRedisTemplate.opsForValue().setIfAbsent(key,value);
//        if(store){
//            stringRedisTemplate.expire(key,timeout, TimeUnit.MILLISECONDS);
//            // todo something...
//        }

        List<String> keys = Arrays.asList(key, value);
        return stringRedisTemplate.execute(tryLockRedisScript, keys, String.valueOf(timeout));
    }

    public Boolean tryLockUser(String value) {
        return tryLock(USER_LOCK,value);
    }

    public Boolean unlockUser(String value) {
        return unlock(USER_LOCK,value);
    }

    public boolean unlock(String key,String value){
        return stringRedisTemplate.execute(unlockRedisScript, Arrays.asList(key),value);
    }
}
