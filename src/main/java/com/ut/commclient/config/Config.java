package com.ut.commclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${rec-path}")
    private void setRecPath(String recPath) {
        Config.recPath = recPath;
    }

    @Value("${start-path}")
    private void setStartPath(String startPath) {
        Config.startPath = startPath;
    }

    @Value("${task-path}")
    private void setTaskPath(String taskPath) {
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
}
