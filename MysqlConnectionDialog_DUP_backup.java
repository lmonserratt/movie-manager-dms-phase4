package dms.util;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Swing dialog to collect MySQL connection parameters at runtime.
 * No credentials are stored; user provides them each run as per rubric.
 */
public class MysqlConnectionDialog extends JDialog {
    private final JTextField hostField = new JTextField("localhost", 16);
    private final JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(3306, 1, 65535, 1));
    private final JTextField dbField = new JTextField("dms_movies", 16);
    private final JTextField userField = new JTextField("root", 16);
    private final JPasswordField passField = new JPasswordField(16);

    private String url; private String user; private String pass;

    public MysqlConnectionDialog(Frame owner) {
        super(owner, "Connect to MySQL", true);
        setLayout(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.add(new JLabel("Host:")); form.add(hostField);
        form.add(new JLabel("Port:")); form.add(portSpinner);
        form.add(new JLabel("Database:")); form.add(dbField);
        form.add(new JLabel("Username:")); form.add(userField);
        form.add(new JLabel("Password:")); form.add(passField);
        add(form, BorderLayout.CENTER);
        JButton ok = new JButton("Connect"); JButton cancel = new JButton("Cancel");
        JPanel buttons = new JPanel(); buttons.add(ok); buttons.add(cancel); add(buttons, BorderLayout.SOUTH);
        ok.addActionListener(e -> onConnect());
        cancel.addActionListener(e -> { url = null; user = null; pass = null; dispose(); });
        pack(); setLocationRelativeTo(owner);
    }

    private void onConnect() {
        String host = hostField.getText().trim();
        int port = (Integer) portSpinner.getValue();
        String db = dbField.getText().trim();
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        if (host.isEmpty() || db.isEmpty() || u.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Host, database and username are required.", "Invalid", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Add recommended params for timezone/Unicode; SSL left to server defaults.
        this.url = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8", host, port, db);
        this.user = u; this.pass = p; dispose();
    }

    public String getUrl() { return url; }
    public String getUser() { return user; }
    public String getPass() { return pass; }
}
