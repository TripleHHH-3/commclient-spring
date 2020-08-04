package com.ut.commclient;

import com.ut.commclient.view.MainView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.SplashScreen;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@SpringBootApplication
public class CommClientApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(CommClientApplication.class, MainView.class, new SplashScreen() {
            public boolean visible() {
                return false;
            }
        }, args);
        
    }

}
