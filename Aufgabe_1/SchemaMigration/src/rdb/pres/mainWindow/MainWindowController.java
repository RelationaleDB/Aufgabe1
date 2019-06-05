/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.pres.mainWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
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
import javafx.util.Pair;
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
    private TextField textViewSchemaEingeben;
    @FXML
    private TextField textViewAuswahlTreffen;
    @FXML
    private TextField textView_Berechnung_Transfer;
    
    @FXML
    private ListView<String> listView_Auswahl_Transfer;
    @FXML
    
    private ChoiceBox<String> choiceBox_company_type;
    @FXML
    private ChoiceBox<String> choiceBox_link_type;
    @FXML
    private ChoiceBox<String> choiceBox_kind_type;
    @FXML
    private ChoiceBox<String> choiceBox_role_type;
    @FXML
    private ChoiceBox<String> choiceBox_info_type_movie_info_idx;
    @FXML
    private ChoiceBox<String> choiceBox_info_type_movie_info;
    @FXML
    private ChoiceBox<String> choiceBox_info_type_person_info;
    @FXML
    private ChoiceBox<Integer> choiceBox_ProductionYear_1;
    @FXML
    private ChoiceBox<Integer> choiceBox_ProductionYear_2;
    
    @FXML
    private Button schemaNachOracleDBKopieren;
    @FXML
    private Button postgresTestTabellenerstellen;
    @FXML
    private Button button_addToListView;
    @FXML
    private Button button_deleteFromListView;
    @FXML
    private Button button_brechneTransfer;
    @FXML
    private Button button_startTransfer;
    
    private ArrayList<Button> buttonList;
    
    ArrayList<Pair<String,String>> table_and_types_Pairs = new ArrayList();
    
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
    private void schemaTransfer_nachORCL() {
        if(textViewSchemaEingeben.getText().equals(""))
            textViewSchemaEingeben.setStyle("-fx-text-inner-color: green;");
        else{
            if(app.call_SchemaTransfer(textViewSchemaEingeben.getText())){
                textViewSchemaEingeben.setText("Das Schema wurde erfolgreich kopiert.");
                textViewSchemaEingeben.setDisable(true);
                textViewSchemaEingeben.setStyle("-fx-text-inner-color: black;");
                button_startTransfer.setDisable(false);
            }
        }
    }
    
    @FXML 
    private void schemaTransfer_inPostgres(){
        app.call_SchemaTransfer_inPostgres();
    }
    
    @FXML
    private void calculateTransfer() {
        ArrayList<String> typeNamesList = new ArrayList<String>();
        for(String s:observableList_typeChoices)
            typeNamesList.add(s);
        ArrayList<StringBuilder> calculateTransfer_infoText = app.call_CalculateTransfer(table_and_types_Pairs, productionyear);
        //for (StringBuilder sb : calculateTransfer_infoText)
        //    System.out.println(sb.toString());
        listView_Auswahl_Transfer.getItems().clear();
        for(StringBuilder sb : calculateTransfer_infoText)
            listView_Auswahl_Transfer.getItems().add(sb.toString());
    }
    
    @FXML
    private void startTransfer() {
            app.call_startTransfer();
    }
    @FXML
    private void deleteFromListView() {
      
        listView_Auswahl_Transfer.getItems().clear();
        observableList_typeChoices.clear();
        table_and_types_Pairs.clear();
    }
    
    
    @FXML
    private void addToListView() {
        listView_Auswahl_Transfer.getItems().removeAll(observableList_typeChoices);
        for (int i = 0; i < listOfChoiceBoxes.size(); i++) {
            if(listOfChoiceBoxes.get(i).getValue()!=null&&!listOfChoiceBoxes.get(i).getValue().isEmpty()){
                observableList_typeChoices.add(listOfChoiceBoxes.get(i).getValue());
                Pair<String,String> pair=null;
                switch(i){
                case 0: 
                        table_and_types_Pairs.add(new Pair<>("company_type", listOfChoiceBoxes.get(i).getValue()));
                        break;
                case 1:
                        table_and_types_Pairs.add(new Pair<>("link_type", listOfChoiceBoxes.get(i).getValue()));
                        break;
                case 2:
                        table_and_types_Pairs.add(new Pair<>("kind_type", listOfChoiceBoxes.get(i).getValue()));
                        break;
                case 3: 
                        table_and_types_Pairs.add(new Pair<>("info_type_movie_info_idx", listOfChoiceBoxes.get(i).getValue()));
                        break;
                case 4: 
                        table_and_types_Pairs.add(new Pair<>("info_type_movie_info", listOfChoiceBoxes.get(i).getValue()));
                        break;
                case 5:
                        table_and_types_Pairs.add(new Pair<>("info_type_person_info", listOfChoiceBoxes.get(i).getValue()));
                        break;
                case 6: 
                        table_and_types_Pairs.add(new Pair<>("role_type", listOfChoiceBoxes.get(i).getValue()));
                        break;
                }
            }
        }
        
        for(Pair pair : table_and_types_Pairs){
            System.out.println("Key: "+pair.getKey()+"\tValue: "+pair.getValue()); 
        }
            System.out.println("------------------------------------------------------------");
            
        
        if(choiceBox_ProductionYear_1.getValue()!=null&&choiceBox_ProductionYear_2.getValue()!=null){
            productionyear[0]=choiceBox_ProductionYear_1.getValue();
            productionyear[1]=choiceBox_ProductionYear_2.getValue();

            if(productionyear[0]>productionyear[1]){
                Integer temp = productionyear[0];
                productionyear[0]=productionyear[1];
                productionyear[1]=temp;
            }
        }
   
        listView_Auswahl_Transfer.getItems().addAll(observableList_typeChoices);
        button_brechneTransfer.setDisable(false);
        
        for(ChoiceBox<String> cb : listOfChoiceBoxes)
            cb.getSelectionModel().selectFirst();
    }

    private void addItems_To_ChoiceBoxes(){
        List<Integer> list = new ArrayList<>();
        
        choiceBox_company_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("company_type")); 
        listOfChoiceBoxes.add(choiceBox_company_type);
        
        choiceBox_link_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("link_type"));
        listOfChoiceBoxes.add(choiceBox_link_type);
        
        choiceBox_kind_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("kind_type"));
        listOfChoiceBoxes.add(choiceBox_kind_type);
        
        choiceBox_info_type_movie_info_idx.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("info_type")); 
        listOfChoiceBoxes.add(choiceBox_info_type_movie_info_idx);
        
        choiceBox_info_type_movie_info.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("info_type")); 
        listOfChoiceBoxes.add(choiceBox_info_type_movie_info);
        
        choiceBox_info_type_person_info.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("info_type")); 
        listOfChoiceBoxes.add(choiceBox_info_type_person_info);
        
        choiceBox_role_type.getItems().addAll(app.call_ItemsList_for_ChoiceBoxes("role_type"));
        listOfChoiceBoxes.add(choiceBox_role_type);
         
        for (int i = 2019; i > 1880; i--) {
            list.add(i);
        }
        choiceBox_ProductionYear_1.getItems().addAll(list);
        choiceBox_ProductionYear_2.getItems().addAll(list);
    }
    
}