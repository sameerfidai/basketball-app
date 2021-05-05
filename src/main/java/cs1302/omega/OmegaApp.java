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
import java.net.URL;
import com.google.gson.*;
import javafx.scene.layout.*;

/**
 * The App displays few statistics about an NBA player along with an image.
 */
public class OmegaApp extends Application {

    VBox root;
    Scene scene;
    TextField field;
    HBox searchBox;
    HBox displayImg;
    HBox next;
    VBox stats;
    VBox errorBox;
    String playerName;

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        // setup scene
        root = new VBox();
        scene = new Scene(root);

        // run the app
        try {
            run();
        } catch (Exception e) {
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
        field.setPromptText("e.g: Kyrie Irving");
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
                getStats(playerName, 0);
            } catch (Exception e1) {
                System.err.println(e);
            }
        });
    }

    /**
     * Gets stats for NBA Player.
     * 
     * @throws IOException
     * @param content name of player
     */
    public void getStats(String content, int i) throws IOException {
        if (stats != null) {
            stats.getChildren().clear();
        }
        if (errorBox != null) {
            errorBox.getChildren().clear();
        }
        if (next != null) {
            next.getChildren().clear();
        }
        try {
            String sUrl = "https://www.balldontlie.io/api/v1/players?search=" + content;
            URL url = new URL(sUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement je = JsonParser.parseReader(reader);
            JsonObject jRoot = je.getAsJsonObject();
            JsonArray results = jRoot.getAsJsonArray("data");
            if (results.size() > 1) {
                getNextPlayer();
            }
            JsonObject result = results.get(i).getAsJsonObject();
            printStats(result);
        } catch (Exception e) {
            if (displayImg != null) {
                displayImg.getChildren().clear();
            }
            System.err.println(e);
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
        String height = "";
        try {
            JsonElement jHeightFeet = result.get("height_feet");
            JsonElement jHeightInches = result.get("height_inches");
            String heightFeet = jHeightFeet.getAsString();
            String heightInches = jHeightInches.getAsString();
            height = heightFeet + " " + heightInches + "\"";
        } catch (java.lang.UnsupportedOperationException e) {
            height = "N/A";
        }
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
        Label labelHeight = new Label("Height: " + height);
        stats.getChildren().addAll(labelFirstName, labelLastName, labelPosition, labelTeam, labelHeight);
        stats.setAlignment(Pos.CENTER);
        root.getChildren().add(stats);
        try {
            getImage(firstName, lastName);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Gets the player's image.
     * 
     * @param firstName player's first name
     * @param lastName  player's last name
     * @throws IOException
     */
    public void getImage(String firstName, String lastName) throws IOException {
        if (displayImg != null) {
            displayImg.getChildren().clear();
        }
        try {
            String sUrl = "https://www.thesportsdb.com/api/v1/json/1/searchplayers.php?p=" + firstName + "%20"
                    + lastName;
            URL url = new URL(sUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement je = JsonParser.parseReader(reader);
            JsonObject jRoot = je.getAsJsonObject();
            JsonArray results = jRoot.getAsJsonArray("player");
            JsonObject result = results.get(0).getAsJsonObject();
            JsonElement jImage = result.get("strThumb");
            String link = jImage.getAsString();
            displayImg = new HBox();
            Image img = new Image(link);
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(300);
            imgView.setFitWidth(300);
            HBox.setMargin(imgView, new Insets(30, 10, 10, 10));
            displayImg.setAlignment(Pos.CENTER);
            displayImg.getChildren().add(imgView);
            root.getChildren().add(displayImg);
        } catch (Exception e) {
            errorBox = new VBox();
            Label errorMsg = new Label("NO picture available.");
            VBox.setMargin(errorMsg, new Insets(30, 10, 10, 10));
            errorBox.setAlignment(Pos.CENTER);
            errorBox.getChildren().add(errorMsg);
            root.getChildren().add(errorBox);
        }
    }

    /**
     * Gets next player with there is more than one player with the same name.
     */
    public void getNextPlayer() {
        if (next != null) {
            next.getChildren().clear();
        }
        next = new HBox(20);
        Button button = new Button("Get next player");
        Label label = new Label("There is another player with this name.");
        next.getChildren().addAll(label, button);
        next.setAlignment(Pos.CENTER);
        button.setOnAction(e -> {
            try {
                playerName = field.getText();
                playerName.toLowerCase();
                playerName = playerName.replaceAll("\\s", "+");
                getStats(playerName, 1);
                root.getChildren().removeAll(next);
            } catch (Exception e1) {
                System.err.println(e);
            }
        });
        root.getChildren().add(next);
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