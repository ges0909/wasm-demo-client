module com.valantic.sti.tutorial {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    requires static lombok;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;

    opens com.valantic.sti.tutorial to javafx.fxml;
    exports com.valantic.sti.tutorial;
}
