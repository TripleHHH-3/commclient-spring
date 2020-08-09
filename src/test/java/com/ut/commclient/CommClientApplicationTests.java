package com.ut.commclient;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.roskenet.jfxsupport.test.GuiTest;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SpringBootTest
public abstract class CommClientApplicationTests<T extends AbstractFxmlView> extends GuiTest {
    protected Class clazz;

    public CommClientApplicationTests() {
        Class c = this.getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.clazz = (Class<T>) p[0];
        }
    }

    @PostConstruct
    public final void init() throws Exception {
        init(this.clazz);
    }


    @AfterAll
    public final void resetValues() {
        // You are responsible for cleaning up your Beans!
        Platform.runLater(() -> {
            /*
                maybe like this
                TextField helloLabel = (TextField) find("#nameField");
                helloLabel.setText("");
            */
            reset();
        });
    }

    public abstract void reset();
}
