package com.mine.mall.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.mine.mall.entity.User;
import com.mine.mall.mapper.UserMapper;
import com.mine.mall.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wangmy
 * @since 2021-09-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
  

    @Override
    public long testAddMillion() {
        return testAddMillion2(10);
    }

    /**
     * mybatis plus saveBatch 1000000 数据 花费1376570毫秒 (22.9428333 分)
     * @return
     */
    public long testAddMillion1() {
        List<User> user = listMillionUser(1,1000000);
        long startMillis = System.currentTimeMillis();
        saveBatch(user);
        return System.currentTimeMillis()-startMillis;
    }

    /**
     * threadNum = 4
     * 线程1执行时间===>545370毫秒
     * 线程2执行时间===>546465毫秒
     * 线程0执行时间===>547067毫秒
     * 线程3执行时间===>547462毫秒
     * 9.1243667 分
     *
     * =========================
     *
     * threadNum = 10
     * 线程2执行时间===>244280毫秒
     * 线程5执行时间===>244383毫秒
     * 线程6执行时间===>244702毫秒
     * 线程0执行时间===>245237毫秒
     * 线程1执行时间===>245149毫秒
     * 线程4执行时间===>245312毫秒
     * 线程3执行时间===>245400毫秒
     * 线程8执行时间===>246160毫秒
     * 线程9执行时间===>465571毫秒
     * 线程7执行时间===>465617毫秒
     * 7.7602833 分
     * 是因为 8 核 CPU？？？
     * @return
     */
    public long testAddMillion2(int threadNum) {
//        int threadNum = 4;
        final CountDownLatch cdl = new CountDownLatch(threadNum);
        AtomicLong allMillis = new AtomicLong();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<threadNum;i++){
            List<User> user = listMillionUser(i,1000000/threadNum);
            int finalI = i;
            executorService.submit(()->{
                long startMillis = System.currentTimeMillis();
                saveBatch(user);
                long millis = System.currentTimeMillis()-startMillis;
                System.out.println(StringUtils.join("线程",finalI,"执行时间===>",millis,"毫秒"));
//                allMillis.addAndGet(millis);
                allMillis.set(millis);
                cdl.countDown();
            });
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allMillis.get();
    }

    public List<User> listMillionUser(int index,int num){
        return new ArrayList<User>(){{
            for(int i = num*(index-1); i< num*(index); i++){
                add(new User(StringUtils.join("user",i),"xxxx"));
            }
        }};
    }

}

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wangmy
 * @since 2021-09-16
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    public User(String username,String password){
        this.username = username;
        this.password = password;
        this.createTime = LocalDateTime.now();
    }

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
/**

CREATE TABLE `user` (
  `id` varchar(50) NOT NULL COMMENT 'uuid',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

*/
