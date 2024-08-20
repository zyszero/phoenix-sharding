package io.github.zyszero.phoenix.sharding.demo;

import io.github.zyszero.phoenix.sharding.config.ShardingAutoConfiguration;
import io.github.zyszero.phoenix.sharding.mybatis.ShardingMapperFactoryBean;
import io.github.zyszero.phoenix.sharding.demo.mapper.UserMapper;
import io.github.zyszero.phoenix.sharding.demo.model.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "io.github.zyszero.phoenix.sharding.demo.mapper",
        factoryBean = ShardingMapperFactoryBean.class)
public class PhoenixShardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixShardingApplication.class, args);
    }


    @Autowired
    UserMapper userMapper;

    @Bean
    ApplicationRunner runner() {
        return args -> {
            for (int id = 1; id <= 10; id++) {
                test(id);
            }
        };
    }

    private void test(int id) {
        System.out.println(" ===> 1, test insert ...");
        int inserted = userMapper.insert(new User(id, "zyszero", 18));
        System.out.println(" ===> inserted = " + inserted);


        System.out.println(" ===> 2, test find ...");
        User user = userMapper.findById(id);
        System.out.println(" ===> result = " + user);


        System.out.println(" ===> 3, test update ...");
        user.setAge(19);
        user.setName("zyszero-new");
        int updated = userMapper.update(user);
        System.out.println(" ===> result = " + updated);


        System.out.println(" ===> 4, test new find ...");
        User user2 = userMapper.findById(id);
        System.out.println(" ===> result = " + user2);

        System.out.println(" ===> 5, test delete ...");
        int deleted = userMapper.delete(id);
        System.out.println(" ===> deleted = " + deleted);
    }
}
