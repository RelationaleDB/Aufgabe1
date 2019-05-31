/*
 *  Copyright 2014 Rainer Stoermer
 */
package rdb;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rdb.app.LoginAL;
import rdb.pres.login.LoginController;
import rdb.pres.mainWindow.MainWindowController;

/**
 *
 * @author Rainer Stoermer
 */
public class RDBSchemaMigration extends Application {

    private static final int LOG_SIZE = 1024000;
    private static final int LOG_ROTATION_COUNT = 1;
    private static final String TITLE = "RDB Schema Migration";
    private static RDBSchemaMigration instance = null;
    private Stage stage;

    public static void main(String[] args) {
        try {
            // set a handler for using a logfile for outputting log-messages
            Handler handler = new FileHandler("imdbsearch.log", LOG_SIZE, LOG_ROTATION_COUNT);
            handler.setFormatter(new SimpleFormatter());
            Logger.getLogger("").addHandler(handler);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        Application.launch(RDBSchemaMigration.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;
        stage.setTitle(TITLE);
        stage.setResizable(false);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pres/login/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            LoginController controller = loader.getController();
            controller.setApp(this);
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE,
                    "Error loading login-fxml-file and controller" + e.getMessage());
        }
    }

    /**
     * This method can be used to switch to a new window
     * @param fxml relative path to the fxml file
     */
    public void openNewWindow(String fxml) {
        stage.close();
        stage = new Stage();
        stage.setTitle(TITLE);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pres/mainWindow/mainWindow.fxml"));
            stage.setScene(new Scene(loader.load()));
            MainWindowController controller = loader.getController();
            controller.setApp(this);
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.SEVERE,
                    "Error loading login-fxml-file and controller" + e.getMessage());
        }
    }

    public void quit() {
        Logger.getLogger(RDBSchemaMigration.class.getName()).log(Level.INFO, "Closing Application...");
        stage.close();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        LoginAL loginService = new LoginAL();
        loginService.postgresUserLogout();
        loginService.oracleUserLogout();
    }

    public static RDBSchemaMigration getInstance() {
        return instance;
    }
}
