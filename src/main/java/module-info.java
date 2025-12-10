module clases.proyectopruebas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires com.fasterxml.jackson.databind;
    requires com.opencsv;

    opens clases.proyectopruebas to javafx.fxml;
    opens clases.proyectopruebas.controllers to javafx.fxml;
    exports clases.proyectopruebas;

        requires javafx.graphics;
        requires javafx.base;

        // Abrir el paquete de modelos a JavaFX para reflexión
        opens clases.proyectopruebas.models to javafx.base;

        // Si usas FXML, también necesitas abrir los paquetes de vista
        opens clases.proyectopruebas.view.view to javafx.fxml;
        opens clases.proyectopruebas.view to javafx.fxml;

        // Exportar los paquetes necesarios
        exports clases.proyectopruebas.models;
        exports clases.proyectopruebas.view;
        exports clases.proyectopruebas.view.view;
        }