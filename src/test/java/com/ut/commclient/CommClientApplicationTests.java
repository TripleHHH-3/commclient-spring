package com.ut.commclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class CommClientApplicationTests {

    @Test
    void contextLoads() throws IOException {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            integers.add(i);
        }
        integers.forEach(integer -> {
            if (integer > 200 && (integer % 5 == 5)) {
                integers.remove(integer);
            }
        });
    }

}
