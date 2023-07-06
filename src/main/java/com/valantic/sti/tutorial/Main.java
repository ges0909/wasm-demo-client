package com.valantic.sti.tutorial;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Main extends Application {

    final Properties properties = new Properties();

    public static void main(final String... args) {
        launch(args);
    }

    @Override
    @SneakyThrows
    public void init() {
        try (final InputStream stream = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(stream);
        }
    }

    @Override
    public void start(final Stage window) {
        final TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // has to be a property name

        final TableColumn<Product, String> priceColumn = new TableColumn<>("Price");
        nameColumn.setMinWidth(50);
        priceColumn.setMaxWidth(200);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        final TableColumn<Product, String> quantityColumn = new TableColumn<>("Quantity");
        nameColumn.setMinWidth(50);
        quantityColumn.setMaxWidth(200);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        final TableView<Product> table = new TableView<>();
        table.getColumns().add(nameColumn);
        table.getColumns().add(priceColumn);
        table.getColumns().add(quantityColumn);

        final Button button = new Button("Get from Fermyon Cloud");
        button.setOnAction(e -> Platform.runLater(() -> {
            window.getScene().setCursor(Cursor.WAIT);
            table.setItems(FXCollections.observableList(getProductsFromFermyonCloud()));
            window.getScene().setCursor(Cursor.DEFAULT);
        }));

        final VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(table, button);

        final Scene scene = new Scene(layout, 350, 300);

        window.setTitle("Valantic WebAssembly Demo");
        window.setScene(scene);
        window.show();
    }

    @SneakyThrows
    private List<Product> getProductsFromFermyonCloud() {
        final HttpRequest request = HttpRequest.newBuilder() //
                .uri(new URI((String) properties.get("fermyon.app.url"))) //
                .headers("accept", "application/json") //
                .GET() //
                .build();
        final HttpClient client = HttpClient.newHttpClient();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), new TypeReference<List<Product>>() {
        });
    }
}
