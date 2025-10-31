package dms.app;

import dms.gui.MovieTableFrameMysql;
import javax.swing.*;

/**
 * Entry point for the MySQL-based Movie Manager DMS application.
 * This class prompts the user for MySQL connection details and
 * launches the main GUI window once the connection parameters are set.
 *
 * Author: Luis Augusto Monserratt Alvarado
 */
public class GuiMainMysql {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // Prompt for MySQL host (e.g., localhost)
        String host = JOptionPane.showInputDialog(
                null,
                "Enter MySQL host (e.g., localhost):",
                "Database Connection",
                JOptionPane.QUESTION_MESSAGE
        );
        if (host == null || host.isBlank()) {
          JOptionPane.showMessageDialog(null, "No host provided. Exiting.");
          System.exit(0);
        }

        // Prompt for MySQL username
        String user = JOptionPane.showInputDialog(
                null,
                "Enter MySQL username:",
                "Database Connection",
                JOptionPane.QUESTION_MESSAGE
        );
        if (user == null || user.isBlank()) {
          JOptionPane.showMessageDialog(null, "No username provided. Exiting.");
          System.exit(0);
        }

        // Prompt for MySQL password
        String pass = JOptionPane.showInputDialog(
                null,
                "Enter MySQL password:",
                "Database Connection",
                JOptionPane.QUESTION_MESSAGE
        );
        if (pass == null) pass = "";

        // ✅ Build JDBC connection string dynamically
        String url = "jdbc:mysql://" + host + ":3306/dms_movies" +
                "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";

        // ✅ Store connection properties for MovieTableFrameMysql to use
        System.setProperty("JDBC_URL", url);
        System.setProperty("DB_USER", user);
        System.setProperty("DB_PASS", pass);

        // ✅ Launch the main application window
        new MovieTableFrameMysql().setVisible(true);

      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                "Error initializing application:\n" + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE
        );
      }
    });
  }
}
