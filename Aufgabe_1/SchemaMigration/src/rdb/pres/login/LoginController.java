package rdb.pres.login;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import rdb.app.LoginAL;
import rdb.bdo.OracleUser;
import rdb.bdo.PostgresUser;
import rdb.RDBSchemaMigration;
import rdb.pres.mainWindow.*;

/**
 * Login Controller.
 */
public class LoginController extends AnchorPane implements Initializable {

    private RDBSchemaMigration application;
    @FXML
    private Label statusMessage;
    @FXML
    private TextField postgresUsername;
    @FXML
    private PasswordField postgresPassword;
    @FXML
    private Button postgresButton;
    @FXML
    private ImageView postgresImage;
    @FXML
    private TextField oracleUsername;
    @FXML
    private PasswordField oraclePassword;
    @FXML
    private Button oracleButton;
    @FXML
    private ImageView oracleImage;
    @FXML
    private Button startButton;
    @FXML
    private Button quitButton;
    @FXML
    private Font x1;
    private Boolean postgresLoginSuccess = false;
    private Boolean oracleLoginSuccess = false;
    private PostgresUser postgresDbUser;
    private OracleUser oracleDbUser;
    private LoginAL loginService;
    private final Image successImage;
    private final Image failedImage;

    /**
     * Constructor
     */
    public LoginController() {
        this.loginService = new LoginAL();
        this.successImage = new Image("rdb/pres/login/success.png");
        this.failedImage = new Image("rdb/pres/login/failed.png");
    }

    /**
     * Setter for the application
     *
     * @param application
     */
    public void setApp(RDBSchemaMigration application) {
        this.application = application;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusMessage.setText("");
        postgresUsername.setPromptText("postgres user");
        postgresPassword.setPromptText("postgres password");
        oracleUsername.setPromptText("oracle user");
        oraclePassword.setPromptText("oracle password");
        postgresDbUser = PostgresUser.getInstance();
        oracleDbUser = OracleUser.getInstance();
    }

    /**
     * action for the Postgres-Login-Button
     *
     * @param event
     */
    @FXML
    private void processPostgresLogin(ActionEvent event) {
        // sets username and password in singleton-User
        postgresDbUser.setUsername(postgresUsername.getText());
        postgresDbUser.setPassword(postgresPassword.getText());

        if (application == null) {
            // We are running in isolated FXML, e.g. in Scene Builder.
            // Nothing to do
            statusMessage.setText("Hello " + postgresDbUser.getUsername());
        } else {

            if (loginService.postgresUserLogin()) { 
                postgresLoginSuccess = true;
                // show green smilie
                postgresImage.setImage(successImage);
                showMessage();
                if (oracleLoginSuccess) {
                    // both DbConnections successful, enable start button
                    startButton.setDisable(false);
                }
            } else {
                postgresLoginSuccess = false;
                // show red smilie
                postgresImage.setImage(failedImage);
                showMessage();
                // disable startButton
                startButton.setDisable(true);
            }
        }
    }

    /**
     * action for the Oracle-Login-Button
     *
     * @param event
     */
    @FXML
    private void processOracleLogin(ActionEvent event) {
        oracleDbUser.setUsername(oracleUsername.getText());
        oracleDbUser.setPassword(oraclePassword.getText());

        if (application == null) {
            // We are running in isolated FXML, possibly in Scene Builder.
            // NO-OP.
            statusMessage.setText("Hello " + oracleDbUser.getUsername());
        } else {

            if (loginService.oracleUserLogin()) {
                oracleLoginSuccess = true;
                oracleImage.setImage(successImage);
                showMessage();
                if (postgresLoginSuccess) {
                    startButton.setDisable(false);
                }
            } else {
                oracleLoginSuccess = false;
                oracleImage.setImage(failedImage);
                startButton.setDisable(true);
                showMessage();
            }
        }
    }

    /**
     * Method displays a Message depending on database-login-status.
     */
    private void showMessage() {
        if (postgresLoginSuccess) {
            if (oracleLoginSuccess) {
                // both logins are successful
                statusMessage.setText("Login to Postgres and Oracle successful!");
            } else {
                // postgres successful, oracle failed or not tried
                if (!oracleDbUser.getUsername().isEmpty() || !oracleDbUser.getPassword().isEmpty()) {
                    statusMessage.setText("Oracle Login failed, please retry!");
                } else {
                    statusMessage.setText("Postgres-Login successful, please log into Oracle");
                }
            }
        } else {
            if (oracleLoginSuccess) {
                // oracle successful, postgres failed or not tried
                if (!postgresDbUser.getUsername().isEmpty() || !postgresDbUser.getPassword().isEmpty()) {
                    statusMessage.setText("Postgres Login failed, please retry!");
                } else {
                    statusMessage.setText("Oracle-Login successful, please log into Postgres");
                }
            } else {
                // both logins failed or not tried
                if (!oracleDbUser.getUsername().isEmpty() || !oracleDbUser.getPassword().isEmpty()) {
                    // Oracle login failed
                    if (!postgresDbUser.getUsername().isEmpty() || !postgresDbUser.getPassword().isEmpty()) {
                        // both logins tried and failed
                        statusMessage.setText("Oracle and Postgres Login failed, please retry!");
                    } else {
                        // Oracle failed, Postgres not tried
                        statusMessage.setText("Oracle Login failed, please retry!");
                    }
                } else {
                    // Oracle not tried
                    if (!postgresDbUser.getUsername().isEmpty() || !postgresDbUser.getPassword().isEmpty()) {
                        // postgres failed, oracle not tried
                        statusMessage.setText("Postgres Login failed, please retry!");
                    } else {
                        // Oracle not tried, Postgres not tried
                        statusMessage.setText("");
                    }
                }
            }
        }
    }

    /**
     * Logs user out of both dbms and closes application
     *
     * @param event
     */
    @FXML
    private void processQuit(ActionEvent event) {
        loginService.postgresUserLogout();
        loginService.oracleUserLogout();
        application.quit();
    }

    /**
     * switch scene to migration-fxml-scene
     *
     * @param event
     */
    @FXML
    private void processMigration(ActionEvent event) {
        application.openNewWindow("mainWindow/mainWindow.fxml");
    }
}
