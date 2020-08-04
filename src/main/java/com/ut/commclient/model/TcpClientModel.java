package com.ut.commclient.model;

import com.ut.commclient.common.BufferedWriterLock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Socket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TcpClientModel {
    private String ip;
    private Integer port;
    private Socket socket;
    private BufferedWriterLock writer;
    private Long lastRecTime;
}
