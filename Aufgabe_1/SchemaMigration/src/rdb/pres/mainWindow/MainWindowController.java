/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb.pres.mainWindow;

import java.io.IOException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javafx.stage.Stage;
import rdb.RDBSchemaMigration;
import rdb.data.DbConnection;
import rdb.data.DbConnectionSingletonFactory;

/**
 * Default Window to be displayed after successful login
 *
 * @author Vanessa Paluch, Atilla Demir
 */
public class MainWindowController implements Initializable {
    
    //Reference to the application instance can be used when switching to a new window
   //private final RDBSchemaMigration application = RDBSchemaMigration.getInstance();
    
    private RDBSchemaMigration application;
    
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
    private Button button_addToListView;
    @FXML
    private Button button_deleteFromListView;
    @FXML
    private Button button_brechneTransfer;
    @FXML
    private Button button_startTransfer;
    @FXML
    private TextArea textArea_beforeTransfer;
    
    List<ChoiceBox<String>> listOfChoiceBoxes = new ArrayList<ChoiceBox<String>>();
    ObservableList<String> observableList = FXCollections.observableArrayList();
    ArrayList<Map<String,String>> list_TableAndTypeNames_from_UserChoice = new ArrayList<>();
    private final String[] typeTableNames = {"company_type","link_type","kind_type","info_type","role_type"};
    private final String[] typeTableNamesRefTables = {"movie_companies","movie_link","title","person_info",
                                                      "movie_info_idx","movie_info","cast_info"};
    private final String[] allTables={"company_type","link_type","kind_type","info_type","role_type",
                                       "movie_companies","movie_link","title","person_info","movie_info_idx",
                                       "movie_info","cast_info","char_name","company_name","name"}; 
    private String[] SQLQueryFromTypeTableRefs;
    
    private DbConnectionSingletonFactory dbConFactory;
    private DbConnection dbConOracle;
    private DbConnection dbConPostgres;
    private DatabaseMetaData databaseMetaDataPG=null;
    private DatabaseMetaData databaseMetaDataORCL=null;
    
    public void setApp(RDBSchemaMigration application) {
        this.application = application;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbConFactory = DbConnectionSingletonFactory.getDbConnectionSingletonFactory();
        dbConOracle = dbConFactory.getDbConnection("oracle");
        dbConPostgres = dbConFactory.getDbConnection("postgres");
        getMetaDataConnection();
        addItems_To_ChoiceBoxes();
    }

    //TODO implement the controller for the migration process

    @FXML
    private void berechneTransfer() {
        
    }
    
    @FXML
    private void startTransfer() {
        
    }
    @FXML
    private void deleteFromListView() {
        while(listView_Auswahl_Transfer.getSelectionModel().selectedItemProperty().getValue()!=null){
            String s = listView_Auswahl_Transfer.getSelectionModel().selectedItemProperty().getValue();
            observableList.remove(s);
            listView_Auswahl_Transfer.getSelectionModel().clearSelection();
        }
        listView_Auswahl_Transfer.getItems().clear();
        listView_Auswahl_Transfer.getItems().addAll(observableList);
    }
    
    @FXML
    private void addToListView() {
        listView_Auswahl_Transfer.getItems().removeAll(observableList);
        
        for(ChoiceBox<String> cbox : listOfChoiceBoxes){
            if(cbox.getValue()!=null&&!cbox.getValue().isEmpty())
                observableList.add(cbox.getValue());
        }
        
        ObservableList list = FXCollections.observableArrayList();
        HashSet<String> set = new HashSet<>();
        for(String s : observableList){
            if(!set.contains(s)){
                list.add(s);
                set.add(s);
            }
        }
        observableList=list;
        listView_Auswahl_Transfer.getItems().addAll(observableList);
    }
    
    /*private void createSQLQuery_with_TypeChoices(){
        ArrayList<Map<String,String>> list_of_SQLQuery_to_typeRefTables = null;
        Map<String,String> map;
        for(String s: typeTableNamesRefTables){
            map = new HashMap<String, String>();
            map.put(s,getSQLQueryfortypeRefTables(s));
        }
    }*/
    
   /* private String getSQLQueryfortypeRefTables(String  refTableName){
        String SQL = "SELECT * from "+refTableName+" ";
        ArrayList<String> list = null;
        for(Map<String,String> map : list_TableAndTypeNames_from_UserChoice){
            for(Map.Entry<String,String> entry : map.entrySet()){
                if(entry.getKey().equals(refTableName))
                    list.add(entry.getValue());
            }
        }
        if(!list.isEmpty())
            SQL = SQL.concat("WHERE "+getTableNameByValue_ForTypeTables(list.get(0))+" IN(SELECT id FROM ");
            for(String s : list)
                SQL = SQL.concat("WHERE person_role_id IN(SELECT id FROM role_type WHERE role = '"
                    +cast_info_Role_type1+"' OR role = '"+cast_info_Role_type1+"')");
    }*/
    
    private void get_List_Of_TypeValuesTableNames(){
        list_TableAndTypeNames_from_UserChoice = new ArrayList<>();
        Map<String,String> map = null;
        for(String s : observableList){
            map = new HashMap<String,String>();
            map.put(getTableNameByValue_ForTypeTables(s), s);
            for(Map.Entry<String,String> entry : map.entrySet())
                System.out.println("Key: "+entry.getKey()+"\tValue"+entry.getValue());
            list_TableAndTypeNames_from_UserChoice.add(map);
        }
    }
    
    private int getRowCountFromResulSet(){
        return 0;
    }
    
    private String getTableNameByValue_ForTypeTables(String value){
        String tableName = null;
        for (int i = 0; i < typeTableNames.length; i++) {
           try(ResultSet rs= dbConPostgres.getConnection().createStatement().executeQuery("SELECT * from "+typeTableNames[i]+" ");)
            { 
                 while(rs.next()){
                    if(rs.getString(2).equals(value))
                           return typeTableNames[i];
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        return tableName;
    }
    
    
    
    private void addItems_To_ChoiceBoxes(){
        String[] typeTableNames = {"company_type","link_type","kind_type","info_type","role_type"};
        ArrayList<String> list = null;  
        ArrayList<Integer> list2 = new ArrayList<>(); 
        for (int i = 0; i < typeTableNames.length; i++) {
            try(ResultSet rs= dbConPostgres.getConnection().createStatement().executeQuery("SELECT * from "+typeTableNames[i]+" ");)
            {
                list=new ArrayList<>();
                while(rs.next()){
                    list.add(rs.getString(2));
                }
                list.add(0, "");
                switch(i){
                    case 0: choiceBox_company_type.getItems().addAll(list); 
                            listOfChoiceBoxes.add(choiceBox_company_type);
                            break;
                    case 1: choiceBox_link_type.getItems().addAll(list);
                            listOfChoiceBoxes.add(choiceBox_link_type);
                            break;
                    case 2: choiceBox_kind_type.getItems().addAll(list);
                            listOfChoiceBoxes.add(choiceBox_kind_type);
                            break;
                    case 3: choiceBox_info_type.getItems().addAll(list); 
                            listOfChoiceBoxes.add(choiceBox_info_type);
                            break;
                    case 4: choiceBox_role_type.getItems().addAll(list);
                            listOfChoiceBoxes.add(choiceBox_role_type);
                            break;
                }

            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        for (int i = 2019; i > 1880; i--) {
            list2.add(i);
        }
        choiceBox_ProductionYear_1.getItems().addAll(list2);
        choiceBox_ProductionYear_2.getItems().addAll(list2);
        
            
    }
    
    public void getMetaDataConnection(){
        try {
            databaseMetaDataORCL = dbConOracle.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Oracle Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
        try {
            databaseMetaDataPG = dbConPostgres.getConnection().getMetaData();
        } catch (SQLException e) {
            System.err.println("Die Verbindung zur Postgres Datenbank (DatabaseSchema) war nicht erfolgreich.");
            e.printStackTrace();
        }
    }
    
    
}