package cn.lmcw.jpicasso;

import cn.lmcw.jpicasso.entity.MyObjectBox;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.File;

@SpringBootApplication
public class JpicassoApplication {

    public static BoxStore store = null;

    public static void main(String[] args) {

        FileUtils.copyFile();

        SpringApplication springApplication = new SpringApplication(JpicassoApplication.class);
        springApplication.addListeners((ApplicationListener<ContextClosedEvent>) event -> {
            if (store != null)
                store.close();
        });
        springApplication.run(args);
    }

    public static <T> Box<T> boxFor(Class<T> entityClass) {
        if (store == null) {
            store = MyObjectBox.builder().name("db").build();
        }
        return store.boxFor(entityClass);
    }

}
