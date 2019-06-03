/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.pres.mainWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rdb.RDBSchemaMigration;
import rdb.app.App;

/**
 * Default Window to be displayed after successful login
 *
 * @author Vanessa Paluch, Atilla Demir
 */
public class MainWindowController implements Initializable {
    
    //Reference to the application instance can be used when switching to a new window
   //private final RDBSchemaMigration application = RDBSchemaMigration.getInstance();
    
    private RDBSchemaMigration application;
    private App app;
    
    @FXML
    private TextField schemaEingeben;
    @FXML
    private ListView<String> listView_Auswahl_Transfer;
    @FXML
    private ChoiceBox<String> choiceBox_company_type;
    @FXML
    private ChoiceBox<String> choiceBox_link_type;
    @FXML
    private ChoiceBox<String> choiceBox_kind_type;
    @FXML
    private ChoiceBox<String> choiceBox_info_type;
    @FXML
    private ChoiceBox<String> choiceBox_role_type;
    @FXML
    private ChoiceBox<Integer> choiceBox_ProductionYear_1;
    @FXML
    private ChoiceBox<Integer> choiceBox_ProductionYear_2;
    @FXML
    private Button schemaKopieren;
    @FXML
    private Button button_addToListView;
    @FXML
    private Button button_deleteFromListView;
    @FXML
    private Button button_brechneTransfer;
    @FXML
    private Button button_startTransfer;
    @FXML
    private TextArea textArea_beforeTransfer;
    
    private ArrayList<Button> buttonList;
    
    List<ChoiceBox<String>> listOfChoiceBoxes = new ArrayList<ChoiceBox<String>>();
    ObservableList<String> observableList_typeChoices = FXCollections.observableArrayList();
    Integer [] productionyear = {0,0};
    
    public void setApp(RDBSchemaMigration application) {
        this.application = application;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        app = new App();
        addItems_To_ChoiceBoxes();
        button_startTransfer.setDisable(true);
        button_brechneTransfer.setDisable(true);
    }

    //TODO implement the controller for the migration process

    @FXML
    private void schemaTransfer() {
        if(schemaEingeben.getText().equals(""))
            schemaEingeben.setStyle("-fx-text-inner-color: green;");
        else{
            if(app.call_SchemaTransfer(schemaEingeben.getText())){
                schemaEingeben.setText("Das Schema wurde erfolgreich kopiert.");
                schemaKopieren.setDisable(true);
                schemaEingeben.setStyle("-fx-text-inner-color: black;");
                button_startTransfer.setDisable(false);
            }
        }
    }
    
    @FXML
    private void calculateTransfer() {
        ArrayList<String> typeNamesList = new ArrayList<String>();
        for(String s:observableList_typeChoices)
            typeNamesList.add(s);
        ArrayList<StringBuilder> calculateTransfer_infoText = app.call_CalculateTransfer(typeNamesList, productionyear);
        
    }
    
    @FXML
    private void startTransfer() {
            app.call_startTransfer();
    }
    @FXML
    private void deleteFromListView() {
        String message = "";
        while(listView_Auswahl_Transfer.getSelectionModel().selectedItemProperty().getValue()!=null){
            String s = listView_Auswahl_Transfer.getSelectionModel().selectedItemProperty().getValue();
            message=s;
            observableList_typeChoices.remove(s);
            listView_Auswahl_Transfer.getSelectionModel().clearSelection();
        }
        listView_Auswahl_Transfer.getItems().clear();
        listView_Auswahl_Transfer.getItems().addAll(observableList_typeChoices);
        if(!message.isEmpty())
            schemaEingeben.setText("Type wurde gelöscht: "+message);
    }
    
    @FXML
    private void addToListView() {
        listView_Auswahl_Transfer.getItems().removeAll(observableList_typeChoices);
        
        for(ChoiceBox<String> cbox : listOfChoiceBoxes){
            if(cbox.getValue()!=null&&!cbox.getValue().isEmpty())
                observableList_typeChoices.add(cbox.getValue());
        }
        
        if(choiceBox_ProductionYear_1.getValue()!=null&&choiceBox_ProductionYear_2.getValue()!=null){
            productionyear[0]=choiceBox_ProductionYear_1.getValue();
            productionyear[1]=choiceBox_ProductionYear_2.getValue();

            if(productionyear[0]>productionyear[1]){
                Integer temp = productionyear[0];
                productionyear[0]=productionyear[1];
                productionyear[1]=temp;
            }
        }
        
        ObservableList list = FXCollections.observableArrayList();
        HashSet<String> set = new HashSet<>();
        for(String s : observableList_typeChoices){
            if(!set.contains(s)){
                list.add(s);
                set.add(s);
            }
        }
        observableList_typeChoices=list;
        listView_Auswahl_Transfer.getItems().addAll(observableList_typeChoices);
        schemaEingeben.setText("Diese Types... sind jetzt in der Liste");
        button_brechneTransfer.setDisable(false);
    }

    private void addItems_To_ChoiceBoxes(){
        List<Integer> list = new ArrayList<>();
        
        choiceBox_company_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("company_type")); 
        listOfChoiceBoxes.add(choiceBox_company_type);
        
        choiceBox_link_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("link_type"));
        listOfChoiceBoxes.add(choiceBox_link_type);
        
        choiceBox_kind_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("kind_type"));
        listOfChoiceBoxes.add(choiceBox_kind_type);
        
        choiceBox_info_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("info_type")); 
        listOfChoiceBoxes.add(choiceBox_info_type);
        
        choiceBox_role_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("role_type"));
        listOfChoiceBoxes.add(choiceBox_role_type);
         
        for (int i = 2019; i > 1880; i--) {
            list.add(i);
        }
        choiceBox_ProductionYear_1.getItems().addAll(list);
        choiceBox_ProductionYear_2.getItems().addAll(list);
    }
    
}