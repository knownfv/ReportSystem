module org.example.reportsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires itextpdf;


    opens org.example.reportsystem to javafx.fxml;
    exports org.example.reportsystem;
}