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
    requires java.sql;
    requires org.hibernate.orm.core;
    requires jdk.httpserver;
    requires java.desktop;
    requires java.net.http;
    requires org.bytedeco.javacv;

    opens Server to javafx.fxml;
    exports Server;

    opens Client to javafx.fxml;
    exports Client;

    opens Shared.Models to com.fasterxml.jackson.databind, org.hibernate.orm.core;

    opens Shared.Api.dto to com.fasterxml.jackson.databind;

}