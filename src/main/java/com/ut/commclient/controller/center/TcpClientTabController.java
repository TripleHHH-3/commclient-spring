package com.ut.commclient.controller.center;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
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
    @FXML
    private TextField ipTxt;
    @FXML
    private TextField portTxt;
    @FXML
    private Button beginBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button sendBtn;
    @FXML
    private TextArea sendMsgTxt;
    @FXML
    private TextArea recMsgTxt;
    @FXML
    private Tab tcpClientTab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void beginConnect(ActionEvent actionEvent) {
        System.out.println("aaaaa");
    }
}
