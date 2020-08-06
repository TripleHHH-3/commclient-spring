package com.ut.commclient;

import com.ut.commclient.config.Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommClientApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(Config.REC_PATH);
    }

}
