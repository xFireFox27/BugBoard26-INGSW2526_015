module it.unina.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;
    requires java.net.http;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires static lombok;
    requires org.slf4j;

    // --- EXPORTS & OPENS ---

    exports it.unina.frontend;

    opens it.unina.frontend.controller to javafx.fxml;

    opens it.unina.frontend.model to com.fasterxml.jackson.databind, javafx.base;

    opens it.unina.frontend.view to javafx.fxml;
    opens it.unina.frontend.util to javafx.fxml;

}