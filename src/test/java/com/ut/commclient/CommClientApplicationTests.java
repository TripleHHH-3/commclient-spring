package com.ut.commclient;

import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class CommClientApplicationTests {

    @Test
    void contextLoads() throws IOException {
        FXMLLoader UdpDatagramTabLoader = new FXMLLoader(getClass().getResource("/view/center/UdpDatagramTabView.fxml"));
        Object load = UdpDatagramTabLoader.load();
        Object load1 = UdpDatagramTabLoader.load();
        System.out.println(load.equals(load1));
    }

}
