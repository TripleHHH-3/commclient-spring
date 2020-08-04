package com.ut.commclient.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@FXMLView(value = "/view/MainView.fxml")
public class MainView extends AbstractFxmlView {
    @Override
    public Parent getView() {
        return super.getView();
//        AnchorPane anchorPane = (AnchorPane) super.getView();
//        Button button = new Button("lalalala");
//        anchorPane.getChildren().add(button);
//        button.setText("aaaaa");
//        return anchorPane;
    }
}
