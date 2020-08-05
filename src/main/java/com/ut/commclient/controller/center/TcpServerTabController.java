package com.ut.commclient.controller.center;

import com.ut.commclient.model.TcpClientModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 19:30
 **/
@FXMLController
public class TcpServerTabController implements Initializable {
    public TextField portTxt;
    public Button beginBtn;
    public Button stopBtn;
    public Button sendBtn;
    public TextArea sendMsgTxt;
    public ListView<TcpClientModel> clientListView;
    public TextArea recTxt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
