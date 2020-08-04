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
 * @create: 2020-08-04 19:48
 **/
@FXMLController
public class UdpDatagramTabController implements Initializable {
    public TextField ipTxt;
    public TextField sendPortTxt;
    public Button bindBtn;
    public Button stopBindBtn;
    public Button sendBtn;
    public TextArea sendMsgTxt;
    public TextField recPortTxt;
    public Button listenBtn;
    public Button stopListenBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
