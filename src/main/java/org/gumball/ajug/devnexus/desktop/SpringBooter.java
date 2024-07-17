package org.gumball.ajug.devnexus.desktop;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBooter {
    public static void main(String[] args) {
        Application.launch(DevNexusApplication.class, args);
    }
}
