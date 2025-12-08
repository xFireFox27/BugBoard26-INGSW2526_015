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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
}
