package dms.app;

import javax.swing.*;

/**
 * GuiMainMysql
 * ------------
 * Application entry point for the MySQL-backed GUI.
 */
public class GuiMainMysql {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless","false");
        SwingUtilities.invokeLater(() -> new MovieTableFrameMysql().setVisible(true));
    }
}
