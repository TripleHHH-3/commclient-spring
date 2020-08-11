package com.ut.commclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-06 19:26
 **/
@Component
public class Config {
    private static String recPath;
    private static String startPath;
    private static String taskPath;

    @Value("${path.rec}")
    private void setRecPath(String recPath) {
        createFile(recPath);
        Config.recPath = recPath;
    }

    @Value("${path.start}")
    private void setStartPath(String startPath) {
        createFile(startPath);
        Config.startPath = startPath;
    }

    @Value("${path.task}")
    private void setTaskPath(String taskPath) {
        createFile(taskPath);
        Config.taskPath = taskPath;
    }

    public static String getRecPath() {
        return recPath;
    }

    public static String getStartPath() {
        return startPath;
    }

    public static String getTaskPath() {
        return taskPath;
    }

    private void createFile(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
