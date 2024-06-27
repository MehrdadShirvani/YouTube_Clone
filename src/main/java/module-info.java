module org.example.youtube {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jakarta.persistence;
    requires com.fasterxml.jackson.databind;

    opens Server to javafx.fxml;
    exports Server;

    opens Client to javafx.fxml;
    exports Client;

    exports Shared.Api.dto to com.fasterxml.jackson.databind;
    exports Shared.Models to com.fasterxml.jackson.databind;
}