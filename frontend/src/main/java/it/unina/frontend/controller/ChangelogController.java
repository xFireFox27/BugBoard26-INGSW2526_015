package it.unina.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.model.Change;
import it.unina.frontend.service.ChangeService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import java.net.http.HttpClient;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangelogController {

    @FXML private Label lblSubtitle;
    @FXML private TableView<Change> tableChanges;
    @FXML private TableColumn<Change, String> colDate;
    @FXML private TableColumn<Change, String> colAuthor;
    @FXML private TableColumn<Change, String> colAction;
    @FXML private TableColumn<Change, String> colDetails;

    private ChangeService changeService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Logger logger = LoggerFactory.getLogger(ChangelogController.class);

    public void initialize(int issueId, HttpClient client, ObjectMapper mapper) {
        this.changeService = new ChangeService(client, mapper);

        lblSubtitle.setText("Storico delle attività per Issue #" + issueId);

        setupTableColumns();

        new Thread(() -> loadChanges(issueId)).start();
    }

    private void setupTableColumns() {
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedOn() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedOn().format(formatter));
            }
            return new SimpleStringProperty("-");
        });

        colAuthor.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedBy() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedBy().getUsername());
            }
            return new SimpleStringProperty("Sconosciuto");
        });

        colAction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAction()));
        enableTooltip(colAction);

        colDetails.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetails()));
        enableTooltip(colDetails);
    }

    private void loadChanges(int issueId) {
        try {
            List<Change> changes = changeService.getChangesByIssue(issueId);

            Platform.runLater(() -> {
                if (changes != null) {
                    tableChanges.getItems().setAll(changes);
                } else {
                    tableChanges.setPlaceholder(new Label("Nessuna modifica trovata."));
                }
            });

        } catch (Exception e) {
            Platform.runLater(() ->
                    tableChanges.setPlaceholder(new Label("Errore durante il caricamento dati."))
            );
            logger.error(e.getMessage(), e);
        }
    }

    private void enableTooltip(TableColumn<Change, String> column) {
        column.setCellFactory(col -> new TableCell<Change, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);

                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setPrefWidth(300);
                    tooltip.setWrapText(true);
                    tooltip.setShowDelay(Duration.millis(200));

                    setTooltip(tooltip);
                }
            }
        });
    }
}