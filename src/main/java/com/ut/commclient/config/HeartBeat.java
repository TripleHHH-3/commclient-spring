package com.ut.commclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-06 19:26
 **/
@Component
public class HeartBeat {
    /**
     * 用于服务器向客户端发送的心跳包
     */
    private static final String echoClient = "echo:client";

    /**
     * 用于客户端向服务器发送的心跳包
     */
    private static final String echoServer = "echo:server";

    /**
     * 心跳包发送间隔时间
     */
    private static long timeInterval = 5 * 1000;

    /**
     * 没有收到心跳包的超时时间
     */
    private static long timeOut = 15000;

    /**
     * 连接超时重试间隔时间
     */
    private static long timeRetry = 1000;

    @Value("${heartbeat.time-interval}")
    private void setTimeInterval(long timeInterval) {
        HeartBeat.timeInterval = timeInterval;
    }

    @Value("${heartbeat.time-out}")
    private void setTimeOut(long timeOut) {
        HeartBeat.timeOut = timeOut;
    }

    @Value("${heartbeat.time-retry}")
    private void setTimeRetry(long timeRetry) {
        HeartBeat.timeRetry = timeRetry;
    }

    public static String getEchoClient() {
        return echoClient;
    }

    public static String getEchoServer() {
        return echoServer;
    }

    public static long getTimeInterval() {
        return timeInterval;
    }

    public static long getTimeOut() {
        return timeOut;
    }

    public static long getTimeRetry() {
        return timeRetry;
    }
}
