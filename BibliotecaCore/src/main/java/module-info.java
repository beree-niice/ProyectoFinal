module clases.proyectopruebas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires com.fasterxml.jackson.databind;
    requires com.opencsv;
    requires javafx.base;
    requires javafx.graphics;

    opens clases.proyectopruebas to javafx.fxml;
    opens clases.proyectopruebas.controllers to javafx.fxml;
    opens clases.proyectopruebas.models to javafx.base, javafx.fxml;
    opens clases.proyectopruebas.models.enums to javafx.base, javafx.fxml;

    // Abre los paquetes de vistas si existen
    opens clases.proyectopruebas.view to javafx.fxml;

    // Exporta los paquetes principales
    exports clases.proyectopruebas;
    exports clases.proyectopruebas.controllers;
    exports clases.proyectopruebas.models;
    exports clases.proyectopruebas.models.enums;
    exports clases.proyectopruebas.view;

}