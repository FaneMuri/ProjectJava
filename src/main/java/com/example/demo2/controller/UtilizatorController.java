package com.example.demo2.controller;
import com.example.demo2.controller.MessageAlert;
import com.example.demo2.domain.*;
import com.example.demo2.domain.validators.PrietenieValidator;
import com.example.demo2.domain.validators.UtilizatorValidator;
import com.example.demo2.repository.*;
import com.example.demo2.service.PrietenieService;
import com.example.demo2.service.UtilizatorService;
import com.sun.jdi.LongValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import jdk.jshell.execution.Util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
public class UtilizatorController {

    private UtilizatorDbRepository userRepo;
    private PrietenieService friendSrv;

    private UtilizatorService userSrv;
    public TextField textFirstName;
    public TextField textLastName;
    public TextField textId;
    public TextField textIDUpdate;
    public TextField textIDSearch;



    @FXML
    TableView<Utilizator> userTableView;
    @FXML
    TableColumn<Utilizator, Long> idUser;
    @FXML
    TableColumn<Utilizator, String> firstName;
    @FXML
    TableColumn<Utilizator, String> lastName;
    @FXML
    ObservableList<Utilizator> userModel = FXCollections.observableArrayList();


    // Fereastra prietenie
    private final ObservableList<Prietenie> friendRequestsObs = FXCollections.observableArrayList();


    @FXML
    private ListView<Prietenie> listOfFriendRequests;

    @FXML
    private TextField friendRequestEmail1;
    @FXML
    private TextField friendRequestEmail2;


/// FereASTRA MESAJ
private final ObservableList<Message> messagesObs = FXCollections.observableArrayList();
    @FXML
    private ListView<Message> listOfMessages;
    @FXML
    private TextField message;
    @FXML
    private TextField sendFrom;
    @FXML
    private TextField sendTo;
    @FXML
    private TextField showMessagesId1;
    @FXML
    private TextField showMessagesId2;





    public void setUserRepo(UtilizatorDbRepository userRepo , PrietenieService srv , UtilizatorService usrv) {
        this.userRepo = userRepo;
        this.friendSrv = srv;
        this.userSrv = usrv;
        initModel();
    }




    public void initialize() {
        userTableView.setItems(userModel);

        idUser.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));




        listOfFriendRequests.setItems(friendRequestsObs);

    }

    private void initModel() {

        userModel.setAll(StreamSupport.stream(userRepo.findAll().spliterator(), false).collect(Collectors.toList()));
        listOfFriendRequests.getItems().clear();
        for (Prietenie friendship : friendSrv.getAll()) {

            friendRequestsObs.add(friendship);
        }


    }

    private void clearTextFields() {
        textFirstName.clear();
        textLastName.clear();
        textId.clear();

        textIDUpdate.clear();
        textIDSearch.clear();
    }

    public void onPressAdd(ActionEvent actionEvent) {
        try{
            String first_name = textFirstName.getText();
            String last_name = textLastName.getText();
            String id = textId.getText();
            int nr = 0 ;
            try {
                 nr = Integer.parseInt(id);
            }
            catch (NumberFormatException ex){
                MessageAlert.showErrorMessage(null, "No update provided");

            }

            long nr1 = nr;

            Utilizator newUser = new Utilizator(first_name, last_name);
            newUser.setId(nr1);
            userRepo.save(newUser);

            initModel();
            clearTextFields();
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }


    }

    public void onPressUpdate(ActionEvent actionEvent) {
        try{
            Long id = Long.valueOf(textIDUpdate.getText());
            String first_name = textFirstName.getText();
            String last_name = textLastName.getText();


             Utilizator selectedUser = userRepo.findOne(id).get();
            if(selectedUser != null) {
                boolean hasUpdates = false;

                if(!first_name.isEmpty()) {
                    selectedUser.setFirstName(first_name);
                    hasUpdates = true;
                }
                if(!last_name.isEmpty()) {
                    selectedUser.setLastName(last_name);
                    hasUpdates = true;
                }


                if(hasUpdates) {
                    userRepo.update(selectedUser);
                    initModel();
                    clearTextFields();
                } else {
                    MessageAlert.showErrorMessage(null, "No update provided");
                }

            } else {
                MessageAlert.showErrorMessage(null, "User not found");
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }


    }

    public void onPressDelete(ActionEvent actionEvent) {
        Utilizator selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            userRepo.delete(selectedUser.getId());
            initModel();
        } else {
            MessageAlert.showErrorMessage(null, "No user selected");
        }


    }

    public void onPressSearch(ActionEvent actionEvent) {
        try{
            Long id = Long.valueOf(textIDSearch.getText());

            Utilizator searchUser = userRepo.findOne(id).get();
            if(searchUser != null) {
                userModel.clear();
                userModel.add(searchUser);

                clearTextFields();
            } else {
                   MessageAlert.showErrorMessage(null, "User not found");
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }


    }

    public void onPressReload(ActionEvent actionEvent) {
        initModel();
        clearTextFields();
    }
    /// friendship window



    

    public void createFriendRequest(javafx.scene.input.MouseEvent mouseEvent) {

        Long id1 = Long.valueOf(friendRequestEmail1.getText());
        Long id2 = Long.valueOf(friendRequestEmail2.getText());

        if (!friendSrv.createFriendRequest(id1, id2)) {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setHeaderText("!!!");
            errorAlert.setContentText("Something went wrong");
            errorAlert.showAndWait();
        }

        friendRequestEmail1.clear();
        friendRequestEmail2.clear();
        initModel();
    }

    public void acceptFriendRequest(MouseEvent mouseEvent) {
        if (listOfFriendRequests.getSelectionModel().getSelectedItem() != null) {
            Prietenie friendship = listOfFriendRequests.getSelectionModel().getSelectedItem();
            friendSrv.respondFriendRequest(friendship, CererePrietenie.ACCEPTED);
            initModel();
        }
        initModel();
    }

    public void rejectFriendRequest(MouseEvent mouseEvent) {
        if (listOfFriendRequests.getSelectionModel().getSelectedItem() != null) {
            Prietenie friendship = listOfFriendRequests.getSelectionModel().getSelectedItem();
            friendSrv.respondFriendRequest(friendship, CererePrietenie.REJECTED);
            initModel();
        }
        initModel();
    }

    public void deleteFriendRequest(MouseEvent mouseEvent) {
        if (listOfFriendRequests.getSelectionModel().getSelectedItem() != null) {
            Prietenie friendship = listOfFriendRequests.getSelectionModel().getSelectedItem();
           try {
            friendSrv.delete(friendship.getId());
            initModel();}
           catch (Exception e ){
               initModel();
           }
        }
        initModel();
    }

    public void searchMessages(MouseEvent mouseEvent) {
        Long id1 = Long.parseLong(showMessagesId1.getText());
        Long id2 = Long.parseLong(showMessagesId2.getText());

        showMessagesId1.clear();
        showMessagesId2.clear();

        sendFrom.setText(id1.toString());
        sendTo.setText(id2.toString());

        loadListOfMessages(id1, id2);
    }

    public void sendMessage(MouseEvent mouseEvent) {
        Long idFrom =  Long.parseLong(sendFrom.getText());
        String idTo = sendTo.getText();
        List<String> toUsers = new ArrayList<>(Arrays.asList(idTo.split(" ")));
        List<Long> toUsersLong = new ArrayList<>();
        for (String s : toUsers)
        {
            toUsersLong.add(Long.parseLong(s));
        }
        String msg = message.getText();

        if (!userSrv.addMessage(idFrom, toUsersLong, msg)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("!!!");
            errorAlert.setContentText("Something wrong");
            errorAlert.showAndWait();
        } else {
            loadListOfMessages(idFrom, toUsersLong.get(0));
        }

        message.clear();

    }
    public void loadListOfMessages(Long idFrom, Long idTo) {
        listOfMessages.getItems().clear();
        messagesObs.clear();
        for (Message msg : userSrv.getMessagesBetweenTwoUsers(idFrom, idTo)) {
            messagesObs.add(msg);
        }
        if (messagesObs.isEmpty()) {
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
            errorAlert.setHeaderText(":((");
            errorAlert.setContentText("No messages");
            errorAlert.showAndWait();
        }

        listOfMessages.setItems(messagesObs);
    }


    public void replyMessage(MouseEvent mouseEvent) {
        if (listOfMessages.getSelectionModel().getSelectedItem() != null) {
            Message msg = listOfMessages.getSelectionModel().getSelectedItem();

            Long idFrom = Long.parseLong(sendFrom.getText());
            String emailTo = sendTo.getText();
            List<String> toUsers = new ArrayList<>(Collections.singletonList(emailTo));
            List<Long> toUsersLong = new ArrayList<>();
            for (String s : toUsers)
            {
                toUsersLong.add(Long.parseLong(s));
            }

            String replyText = message.getText();

            if (!userSrv.addMessage(idFrom, toUsersLong, replyText, msg)) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("!!!");
                errorAlert.setContentText("Something wrong");
                errorAlert.showAndWait();
            } else {
                loadListOfMessages(idFrom, toUsersLong.get(0));
            }

            message.clear();
        }

    }
}
