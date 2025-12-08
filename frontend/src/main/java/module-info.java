module it.unina.frontend {
    // ... tutti i tuoi requires ...
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

    // --- EXPORTS & OPENS ---

    exports it.unina.frontend;

    // Controller: aperto a FXML per gestire l'interfaccia
    opens it.unina.frontend.controller to javafx.fxml;

    // MODEL: Modifica QUESTA RIGA.
    // Deve essere aperto sia a Jackson (per il JSON) sia a JavaFX Base (per la Tabella)
    opens it.unina.frontend.model to com.fasterxml.jackson.databind, javafx.base;

    // View (opzionale, ma male non fa)
    opens it.unina.frontend.view to javafx.fxml;
    opens it.unina.frontend.util to javafx.fxml;

}