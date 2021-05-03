package cs1302.omega;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class OmegaApp extends Application {

    VBox root;
    Scene scene;
    TextField field;
    HBox searchBox;
    VBox stats;
    VBox errorBox;
    String playerName = null;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        // setup scene
        root = new VBox();
        scene = new Scene(root);

        // run the app
        try {
            run();
        } catch (IOException e) {
            System.err.println(e);
        }

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
        field = new TextField();
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
        search.setOnAction(e -> {
            try {
                playerName = field.getText();
                playerName.toLowerCase();
                playerName = playerName.replaceAll("\\s", "+");
                getStats(playerName);
            } catch (IOException e1) {
                System.err.println(e1);
            }
        });
    }

    /**
     * Gets stats for NBA Player.
     * 
     * @throws IOException
     * @param content name of player
     */
    public void getStats(String content) throws IOException {
        if (stats != null) {
            stats.getChildren().clear();
        }
        if (errorBox != null) {
            errorBox.getChildren().clear();
        }
        try {
            String sUrl = "https://www.balldontlie.io/api/v1/players?search=" + content;
            URL url = new URL(sUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement je = JsonParser.parseReader(reader);
            JsonObject jRoot = je.getAsJsonObject();
            JsonArray results = jRoot.getAsJsonArray("data");
            JsonObject result = results.get(0).getAsJsonObject();
            printStats(result);
        } catch (Exception e) {
            errorBox = new VBox();
            Label errorMsg = new Label("ERROR: Please try again.");
            errorBox.getChildren().add(errorMsg);
            root.getChildren().add(errorBox);
        }
    }

    /**
     * Prints stats for Player.
     * 
     * @param result JSON object
     */
    public void printStats(JsonObject result) {
        stats = new VBox(10);
        JsonElement jFirstName = result.get("first_name");
        JsonElement jLastName = result.get("last_name");
        JsonElement jPosition = result.get("position");
        JsonObject jTeam = result.getAsJsonObject("team");
        JsonElement jeTeam = jTeam.get("full_name");
        String firstName = jFirstName.getAsString();
        String lastName = jLastName.getAsString();
        String position = jPosition.getAsString();
        if (position.equalsIgnoreCase("")) {
            position = "N/A";
        }
        String team = jeTeam.getAsString();
        Label labelFirstName = new Label("First Name: " + firstName);
        Label labelLastName = new Label("Last Name: " + lastName);
        Label labelPosition = new Label("Position: " + position);
        Label labelTeam = new Label("Team: " + team);
        stats.getChildren().addAll(labelFirstName, labelLastName, labelPosition, labelTeam);
        stats.setAlignment(Pos.CENTER);
        root.getChildren().add(stats);
    }

    /**
     * Runs the App.
     * 
     * @throws IOException
     */
    public void run() throws IOException {
        loadToolbar();
        searchField();
        loadSearchButton();
    }

} // OmegaApp