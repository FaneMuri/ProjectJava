package com.example.demo2;

import com.example.demo2.repository.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import com.example.demo2.domain.Prietenie;
import com.example.demo2.domain.Tuple;
import com.example.demo2.domain.Utilizator;
import com.example.demo2.domain.validators.PrietenieValidator;
import com.example.demo2.domain.validators.UtilizatorValidator;
import com.example.demo2.service.PrietenieService;
import com.example.demo2.service.UtilizatorService;
import com.example.demo2.controller.UtilizatorController;

import javafx.scene.Scene;
import javafx.stage.Stage;
public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        UtilizatorDbRepository userRepo = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Fane1234");

        PrietenieDbRepository prietenieFileRepository = new PrietenieDbRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","Fane1234");

        MessageDBRepository messageRepo = new MessageDBRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","Fane1234" , userRepo);

        PrietenieService prietenieService = new PrietenieService(prietenieFileRepository, userRepo);

        UtilizatorService usrv = new UtilizatorService(userRepo,prietenieFileRepository,messageRepo);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        UtilizatorController userController = fxmlLoader.getController();
        userController.setUserRepo(userRepo,prietenieService,usrv);
        primaryStage.setTitle("Users");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        public static void main (String[] args) {
            launch();
        }
}