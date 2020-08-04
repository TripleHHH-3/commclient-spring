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
 * @create: 2020-08-04 19:50
 **/
@FXMLController
public class UdpMulticastTabController implements Initializable {
    public TextField bindIpGroupTxt;
    public TextField bindPortTxt;
    public Button bindBeginBtn;
    public Button bindStopBtn;
    public Button sendBtn;
    public TextArea sendMsgTxt;
    public TextField listenIpGroupTxt;
    public TextField listenPortTxt;
    public Button listenBeginBtn;
    public Button listenStopBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
