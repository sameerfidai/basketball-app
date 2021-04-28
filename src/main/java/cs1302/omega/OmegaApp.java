package cs1302.omega;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.gson.*;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class OmegaApp extends Application {

    VBox root;
    Scene scene;

    // hbox for search button and search field
    HBox searchBox;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        // setup scene
        root = new VBox();
        scene = new Scene(root);

        // run the app
        run();

        // setup stage
        stage.setTitle("OmegaApp!");
        stage.setScene(scene);
        stage.setMinHeight(600);
        stage.setMinWidth(600);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
    } // start

    /**
     * Creates the ToolBar top most menu.
     */
    public void loadToolbar() {
        HBox hbox = new HBox();
        MenuBar menuBar = new MenuBar();
        menuBar.setMinWidth(600);
        Menu file = new Menu("File");
        Menu help = new Menu("Help");
        menuBar.getMenus().addAll(file, help);
        MenuItem exit = new MenuItem("Exit");
        file.getItems().add(exit);
        MenuItem about = new MenuItem("About");
        help.getItems().add(about);
        exit.setOnAction(event -> Platform.exit());
        hbox.getChildren().add(menuBar);
        HBox.setHgrow(menuBar, Priority.ALWAYS);
        root.getChildren().add(hbox);
    }

    /**
     * Creates the search field.
     */
    public void searchField() {
        searchBox = new HBox(10);
        TextField field = new TextField();
        HBox.setMargin(field, new Insets(5, 1, 5, 10));
        searchBox.getChildren().add(field);
    }

    /**
     * Creates the search button.
     */
    public void loadSearchButton() {
        Button search = new Button("Search");
        HBox.setMargin(search, new Insets(5, 10, 5, 0));
        searchBox.getChildren().add(search);
        root.getChildren().add(searchBox);
    }

    /**
     * Runs the App.
     */
    public void run() {
        loadToolbar();
        searchField();
        loadSearchButton();
    }

} // OmegaApp