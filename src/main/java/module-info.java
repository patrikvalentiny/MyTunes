module mytunes {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.microsoft.sqlserver.jdbc;
    requires java.sql;
    requires java.naming;
    requires javafx.media;

    opens mytunes to javafx.fxml;
    exports mytunes;
    exports mytunes.gui.controllers;
    exports mytunes.gui.models;
    opens mytunes.gui.controllers to javafx.fxml;
    opens mytunes.be to javafx.base;
    exports mytunes.be;
    exports mytunes.be.tools;
    opens mytunes.be.tools to javafx.base;
}