package com.ut.commclient.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 18:50
 **/
@FXMLController
public class TcpClientTabController implements Initializable {
    public TextField ipTxt;
    public TextField portTxt;
    public Button beginBtn;
    public Button stopBtn;
    public Button sendBtn;
    public TextArea sendMsgTxt;
    public TextArea recMsgTxt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
