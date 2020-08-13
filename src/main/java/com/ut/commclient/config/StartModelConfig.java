package com.ut.commclient.config;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.ut.commclient.model.StartModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-12 08:57
 **/
@Configuration
@DependsOn("config")
public class StartModelConfig {
    private static StartModel startModel;

    @Bean
    public StartModel startModel() {
        try {
            //读取配置文件
            YamlReader reader = new YamlReader(new FileReader(Config.getStartPath()));
            StartModel startModel = JSON.parseObject(JSON.toJSONString(reader.read()), StartModel.class);

            listHandle(startModel.getTcpClient());
            listHandle(startModel.getTcpServer());
            listHandle(startModel.getUdpDatagram());
            listHandle(startModel.getUdpMulticast());

            StartModelConfig.startModel = startModel;

            return startModel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static StartModel get() {
        return startModel;
    }

    private void listHandle(List list) {
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<>());
        } else {
            Collections.synchronizedList(list);
        }
    }
}
