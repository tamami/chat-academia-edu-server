package lab.aikibo;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


/**
 * Created by tamami on 24/03/17.
 */
public class ServerFrame extends Application {

    public String filePath = "";
    SocketServer socketServer;
    TextField tfDbFile;
    Button btnStartServer;
    TextArea taInfo;

    public ServerFrame() {
        socketServer = new SocketServer(this);
        tfDbFile = new TextField();
        btnStartServer = new Button("Start Server");
        taInfo = new TextArea();
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Server Chat");

        BorderPane border = new BorderPane();
        Scene scene = new Scene(border, 600, 500);

        HBox boxAtas = new HBox();

        tfDbFile.setText(filePath);
        Label lbl = new Label("Database file :");
        Button btnBrowse = new Button("Browse...");
        btnBrowse.setOnAction(new BtnBrowseOnClick(primaryStage));
        btnStartServer.setOnAction(new BtnStartServerOnClick(primaryStage));

        boxAtas.getChildren().addAll(lbl, tfDbFile, btnBrowse, btnStartServer);

        border.setTop(boxAtas);

        border.setCenter(taInfo);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public TextArea getTaInfo() {
        return taInfo;
    }

    public void retryStart(int code) {
        try {
            Thread.sleep(code);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t = new Thread(socketServer);
        t.start();
    }


    // -- inner class

    private class BtnBrowseOnClick implements EventHandler<ActionEvent> {

        private Stage stage;

        public BtnBrowseOnClick(Stage stage) {
            this.stage = stage;
        }

        public void handle(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih File Database User");
            File file = fileChooser.showOpenDialog(stage);
            filePath = file.getAbsolutePath();
            tfDbFile.setText(filePath);
        }
    }

    private class BtnStartServerOnClick implements EventHandler<ActionEvent> {
        private Stage stage;
        public BtnStartServerOnClick(Stage stage) {
            this.stage = stage;
        }

        public void handle(ActionEvent event) {
            Thread t = new Thread(socketServer);
            t.start();
        }
    }
}
