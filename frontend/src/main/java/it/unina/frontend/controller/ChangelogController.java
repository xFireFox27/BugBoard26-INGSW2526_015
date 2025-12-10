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

import java.net.http.HttpClient;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChangelogController {

    @FXML private Label lblSubtitle;
    @FXML private TableView<Change> tableChanges;
    @FXML private TableColumn<Change, String> colDate;
    @FXML private TableColumn<Change, String> colAuthor;
    @FXML private TableColumn<Change, String> colAction;
    @FXML private TableColumn<Change, String> colDetails;

    private ChangeService changeService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Metodo chiamato dal controller precedente per passare i dati e inizializzare il servizio
     */
    public void initData(int issueId, HttpClient client, ObjectMapper mapper) {
        // Inizializza il service
        this.changeService = new ChangeService(client, mapper);

        lblSubtitle.setText("Storico delle attività per Issue #" + issueId);

        // Configurazione Colonne Tabella
        setupTableColumns();

        // Caricamento asincrono dei dati
        new Thread(() -> loadChanges(issueId)).start();
    }

    private void setupTableColumns() {
        // 1. Data: Formattiamo l'OffsetDateTime
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedOn() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedOn().format(formatter));
            }
            return new SimpleStringProperty("-");
        });

        // 2. Autore: Navighiamo nell'oggetto User (createdBy)
        colAuthor.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedBy() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedBy().getUsername());
            }
            return new SimpleStringProperty("Sconosciuto");
        });

        // 3. Azione (String semplice)
        colAction.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAction()));

        // 4. Dettagli (String semplice)
        colDetails.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDetails()));
    }

    private void loadChanges(int issueId) {
        try {
            // Chiamata al backend
            List<Change> changes = changeService.getChangesByIssue(issueId);

            // Aggiornamento UI nel thread JavaFX
            Platform.runLater(() -> {
                if (changes != null) {
                    tableChanges.getItems().setAll(changes);
                } else {
                    tableChanges.setPlaceholder(new Label("Nessuna modifica trovata."));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() ->
                    tableChanges.setPlaceholder(new Label("Errore durante il caricamento dati."))
            );
        }
    }
}