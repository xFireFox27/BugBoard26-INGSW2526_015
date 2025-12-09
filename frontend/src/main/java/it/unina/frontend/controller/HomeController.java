package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.model.Issue;
import it.unina.frontend.model.User;
import it.unina.frontend.service.IssueService;
import it.unina.frontend.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeController {

    private static final String ALL_FILTER = "Tutti";

    @FXML private Label welcomeLabel;
    @FXML private Button btnAddIssue;
    @FXML private Button btnManageUsers;

    // --- FILTRI ---
    @FXML private ComboBox<String> filterStatus;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterPriority;
    @FXML private ComboBox<String> sortCombo;

    // --- TABELLA ---
    @FXML private TableView<Issue> issuesTable;
    @FXML private TableColumn<Issue, Integer> colId;
    @FXML private TableColumn<Issue, String> colTitle;
    @FXML private TableColumn<Issue, String> colState;
    @FXML private TableColumn<Issue, String> colPriority;
    @FXML private TableColumn<Issue, String> colType;
    @FXML private TableColumn<Issue, String> colAuthor;
    @FXML private TableColumn<Issue, String> colDate;

    private final IssueService issueService = new IssueService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colState.setCellValueFactory(new PropertyValueFactory<>("status"));

        colAuthor.setCellValueFactory(cellData -> {
            User author = cellData.getValue().getAuthor();
            return new SimpleStringProperty(author != null ? author.getUsername() : "Sconosciuto");
        });

        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedOn() != null) {
                return new SimpleStringProperty(cellData.getValue().getCreatedOn().format(dateFormatter));
            }
            return new SimpleStringProperty("");
        });

        filterStatus.getItems().addAll(ALL_FILTER, "To Do", "In Progress", "Done", "Archived");
        filterStatus.getSelectionModel().select(ALL_FILTER);

        filterType.getItems().addAll(ALL_FILTER, "Bug", "Feature", "Documentation", "Question");
        filterType.getSelectionModel().select(ALL_FILTER);

        filterPriority.getItems().addAll(ALL_FILTER, "Low", "Medium", "High");
        filterPriority.getSelectionModel().select(ALL_FILTER);

        sortCombo.getItems().addAll("id", "title", "creation time", "priority");
        sortCombo.getSelectionModel().select("id");

        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Benvenuto, " + user.getUsername());

            if (!"Admin".equals(user.getRole())) {
                btnManageUsers.setVisible(false);
                btnManageUsers.setManaged(false);
            }
            if ("External".equals(user.getRole())) {
                btnAddIssue.setVisible(false);
                btnAddIssue.setManaged(false);
            }
        }

        issuesTable.setRowFactory(tv -> {
            TableRow<Issue> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Controlla che la riga non sia vuota e che sia un doppio click (o click singolo se preferisci)
                if (!row.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY
                        && event.getClickCount() == 2) {

                        Issue clickedIssue = row.getItem();
                        openIssueView(clickedIssue);
                    }
                });
                return row;
            });

        refreshTable();
    }

    @FXML
    public void refreshTable() {
        try {
            String status = filterStatus.getValue();
            String type = filterType.getValue();
            String priority = filterPriority.getValue();
            String sortBy = sortCombo.getValue();

            List<Issue> issues = issueService.getFilteredIssues(status, type, priority, sortBy);

            ObservableList<Issue> data = FXCollections.observableArrayList(issues);
            issuesTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile caricare le segnalazioni: " + e.getMessage());
        }
    }

    @FXML
    public void resetFilters() {
        filterStatus.getSelectionModel().select(ALL_FILTER);
        filterType.getSelectionModel().select(ALL_FILTER);
        filterPriority.getSelectionModel().select(ALL_FILTER);
        sortCombo.getSelectionModel().select("id");
        refreshTable();
    }

    @FXML
    public void onAddIssueClick() {
        try {
            MainApp.setRoot("add_issue");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLogoutClick() throws IOException {
        SessionManager.getInstance().setToken(null);
        SessionManager.getInstance().setCurrentUser(null);
        MainApp.setRoot("login");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openIssueView(Issue issue) {
        try {
            // 1. Carichiamo il loader manualmente (invece di usare MainApp.setRoot)
            // per poter prendere il controller PRIMA di mostrare la scena.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unina/frontend/view/IssueView.fxml")); // Controlla il path!
            Parent root = loader.load();

            // 2. Recuperiamo il controller della pagina di dettaglio
            IssueViewController controller = loader.getController();

            // 3. Passiamo i dati della issue cliccata al nuovo controller
            controller.setIssueData(issue);

            // 4. Cambiamo la scena attuale con la nuova view
            // Recuperiamo lo Stage (finestra) attuale da un elemento della UI (es. issuesTable)
            Stage stage = (Stage) issuesTable.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire i dettagli della segnalazione: " + e.getMessage());
        }
    }
}
