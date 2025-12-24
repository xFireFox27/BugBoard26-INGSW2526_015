package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.model.Issue;
import it.unina.frontend.model.User;
import it.unina.frontend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class HomeController {

    @FXML private BorderPane mainLayout;
    @FXML private Button btnAddIssue;
    @FXML private Button btnManageUsers;

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            if (!"Admin".equals(user.getRole())) {
                btnManageUsers.setVisible(false);
                btnManageUsers.setManaged(false);
            }
            if ("External".equals(user.getRole())) {
                btnAddIssue.setVisible(false);
                btnAddIssue.setManaged(false);
            }
        }
        showDashboard();
    }


    @FXML
    public void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unina/frontend/view/dashboard.fxml"));
            Parent view = loader.load();

            DashboardController controller = loader.getController();
            controller.setMainController(this);

            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showIssueDetail(Issue issue) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unina/frontend/view/IssueView.fxml"));
            Parent view = loader.load();

            IssueViewController controller = loader.getController();
            controller.setIssueData(issue);

            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAddIssue() {
        loadSimpleView("add_issue");
    }

    @FXML
    public void showUserManagement() {
        loadSimpleView("UserManagementView");
    }

    private void loadSimpleView(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unina/frontend/view/" + fxmlFileName + ".fxml"));
            Parent view = loader.load();
            mainLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onLogoutClick() throws IOException {
        SessionManager.getInstance().setToken(null);
        MainApp.setRoot("login");
    }
}