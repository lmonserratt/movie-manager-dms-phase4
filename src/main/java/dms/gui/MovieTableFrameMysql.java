package dms.gui;

import dms.dao.MovieDao;
import dms.dao.MysqlMovieDao;
import dms.model.Movie;
import dms.service.MovieService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Main window (MySQL): auto-connect on startup, then full CRUD, search, and average duration.
 * If auto-connect fails, a manual connection dialog is offered as a fallback.
 * Includes a tiny "seed" routine to populate the table if it is empty.
 */
public class MovieTableFrameMysql extends JFrame {

    // ---- Auto-connect defaults (change these if needed) ----
    // You can also override them via JVM system properties when running:
    //   -DJDBC_URL=jdbc:mysql://...
    //   -DDB_USER=root
    //   -DDB_PASS=secret
    private static final String DEFAULT_JDBC_URL =
            getPropOrEnv("JDBC_URL",
                    "jdbc:mysql://localhost:3306/dms_movies?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8");
    private static final String DEFAULT_DB_USER = getPropOrEnv("DB_USER", "root");
    private static final String DEFAULT_DB_PASS = getPropOrEnv("DB_PASS", "");

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField txtSearch;
    private final MovieDao dao;
    private final MovieService service;

    public MovieTableFrameMysql() {
        super("DMS – Movies (MySQL)");
        this.dao = new MysqlMovieDao();
        this.service = new MovieService(dao);

        // ---- Table model and UI wiring ----
        this.tableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Director", "Year", "Duration", "Genre", "Rating"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        this.txtSearch = new JTextField(20);

        setContentPane(buildContent());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(980, 520);
        setLocationRelativeTo(null);

        // ---- Auto-connect on startup, then load table data ----
        SwingUtilities.invokeLater(() -> {
            if (autoConnectAndLoad()) {
                // Connected and data loaded successfully.
            } else {
                // Fallback: show manual connection dialog if auto-connect fails.
                if (showConnectionDialogAndConnect()) {
                    // Ensure seed and then refresh after manual connection
                    ensureSeedIfEmpty();
                    safeRefreshAll();
                }
            }
        });
    }

    /**
     * Build the toolbar, search panel, and table area.
     */
    private JPanel buildContent() {
        // Toolbar
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        JButton btnConnect = new JButton("Connect");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnAverage = new JButton("Average Duration");
        JButton btnExit = new JButton("Exit");

        tb.add(btnConnect);
        tb.add(btnRefresh);
        tb.addSeparator();
        tb.add(btnAdd);
        tb.add(btnEdit);
        tb.add(btnDelete);
        tb.addSeparator();
        tb.add(btnAverage);
        tb.add(Box.createHorizontalGlue());
        tb.add(btnExit);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        searchPanel.add(new JLabel("Search title:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        // Center area with table
        JScrollPane scroll = new JScrollPane(table);
        JPanel center = new JPanel(new BorderLayout());
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        // Root container
        JPanel root = new JPanel(new BorderLayout());
        root.add(tb, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        // Actions
        btnConnect.addActionListener(e -> {
            if (showConnectionDialogAndConnect()) {
                ensureSeedIfEmpty();
                safeRefreshAll();
            }
        });
        btnRefresh.addActionListener(e -> safeRefreshAll());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnAverage.addActionListener(e -> onAverage());
        btnExit.addActionListener(e -> onExit());
        btnSearch.addActionListener(e -> onSearch());
        btnClear.addActionListener(e -> { txtSearch.setText(""); safeRefreshAll(); });
        txtSearch.addActionListener(e -> onSearch());
        return root;
    }

    /**
     * Attempts an automatic connection using the default URL/credentials.
     * If successful, seeds the DB if empty and loads all movies into the table.
     *
     * @return true if auto-connect succeeded; false otherwise.
     */
    private boolean autoConnectAndLoad() {
        try {
            service.connect(DEFAULT_JDBC_URL, DEFAULT_DB_USER, DEFAULT_DB_PASS);
            if (service.isConnected()) {
                ensureSeedIfEmpty();             // <-- Seed only when table is empty
                applyTableData(service.readAll());
                System.out.println("✅ Auto-connected to MySQL, seeded if empty, and loaded data.");
                return true;
            }
        } catch (Exception ex) {
            // Swallow here and let caller show manual dialog.
            System.err.println("Auto-connect failed: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Manual connection dialog for host/port/db/user/password.
     */
    private boolean showConnectionDialogAndConnect() {
        JTextField host = new JTextField("localhost");
        JTextField port = new JTextField("3306");
        JTextField db = new JTextField("dms_movies");
        JTextField user = new JTextField(DEFAULT_DB_USER);
        JPasswordField pass = new JPasswordField(DEFAULT_DB_PASS);

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Host:"));     p.add(host);
        p.add(new JLabel("Port:"));     p.add(port);
        p.add(new JLabel("Database:")); p.add(db);
        p.add(new JLabel("User:"));     p.add(user);
        p.add(new JLabel("Password:")); p.add(pass);

        int res = JOptionPane.showConfirmDialog(this, p, "Connect to MySQL",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return false;

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8",
                host.getText().trim(), port.getText().trim(), db.getText().trim());
        try {
            service.connect(jdbcUrl, user.getText(), new String(pass.getPassword()));
            JOptionPane.showMessageDialog(this, "Connected to MySQL successfully.",
                    "Connection", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IllegalArgumentException iae) {
            showError("Validation error:\n" + iae.getMessage());
        } catch (SQLException sqle) {
            showError("Database connection failed:\n" + friendlySql(sqle));
        } catch (Exception ex) {
            showError("Unexpected error:\n" + ex.getMessage());
        }
        return false;
    }

    /**
     * Seeds the database with at least one row if table 'movies' is empty.
     * This guarantees that a fresh DB still shows content on first run.
     * You can replace the single INSERT with your full 20-row seed if desired.
     */
    private void ensureSeedIfEmpty() {
        try (java.sql.Connection c =
                     java.sql.DriverManager.getConnection(
                             System.getProperty("JDBC_URL", DEFAULT_JDBC_URL),
                             System.getProperty("DB_USER", DEFAULT_DB_USER),
                             System.getProperty("DB_PASS", DEFAULT_DB_PASS))) {

            try (java.sql.Statement st = c.createStatement();
                 java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM movies")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    // Minimal seed (replace with your full 20 inserts if you want)
                    st.executeUpdate(
                            "INSERT INTO movies (movie_id,title,director,release_year,duration_minutes,genre,rating) VALUES " +
                                    "('INT2010','Inception','Christopher Nolan',2010,148,'Science Fiction',9.0)"
                    );
                    System.out.println("ℹ️ Seeded table 'movies' with a minimal row.");
                }
            }
        } catch (Exception ignore) {
            // Do not block the app if seeding fails; it's just a convenience.
            System.err.println("Seeding skipped/failed: " + ignore.getMessage());
        }
    }

    /**
     * Reloads all data into the table if connected.
     */
    private void safeRefreshAll() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        try {
            applyTableData(service.readAll());
        } catch (SQLException sqle) {
            showError("Failed to load data:\n" + friendlySql(sqle));
        }
    }

    /**
     * Search handler: filters by title using LIKE on the DAO side.
     */
    private void onSearch() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        String q = txtSearch.getText();
        if (q == null || q.isBlank()) { safeRefreshAll(); return; }
        try {
            applyTableData(service.searchByTitle(q.trim()));
        } catch (SQLException sqle) {
            showError("Search failed:\n" + friendlySql(sqle));
        } catch (IllegalArgumentException iae) {
            showError("Validation error:\n" + iae.getMessage());
        }
    }

    /**
     * Create handler: opens modal form and refreshes on success.
     */
    private void onAdd() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        MovieFormDialog.openCreate(this, service, this::safeRefreshAll);
    }

    /**
     * Update handler: requires a selected row, then opens modal edit.
     */
    private void onEdit() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        Movie selected = getSelectedMovieFromTable();
        if (selected == null) { showError("Select a row to edit."); return; }
        MovieFormDialog.openEdit(this, service, selected, this::safeRefreshAll);
    }

    /**
     * Delete handler: asks for confirmation, then deletes by movie_id (String).
     */
    private void onDelete() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        Movie selected = getSelectedMovieFromTable();
        if (selected == null) { showError("Select a row to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete movie with ID: " + selected.getMovieId() + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = service.deleteById(selected.getMovieId());
            if (ok) safeRefreshAll();
            else showError("Movie ID not found: " + selected.getMovieId());
        } catch (SQLException sqle) {
            showError("Delete failed:\n" + friendlySql(sqle));
        }
    }

    /**
     * Custom action: average duration of all movies.
     */
    private void onAverage() {
        if (!service.isConnected()) { showError("You are not connected to MySQL. Click Connect first."); return; }
        try {
            double avg = service.averageDuration();
            JOptionPane.showMessageDialog(this, String.format("Average duration: %.2f minutes", avg),
                    "Custom Feature", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException sqle) {
            showError("Unable to compute average:\n" + friendlySql(sqle));
        }
    }

    /**
     * Graceful exit: closes DB connection if open, then exits.
     */
    private void onExit() {
        try { service.close(); } catch (SQLException ignore) {}
        dispose();
        System.exit(0);
    }

    /**
     * Applies a list of Movie objects to the table model.
     */
    private void applyTableData(List<Movie> list) {
        tableModel.setRowCount(0);
        for (Movie m : list) {
            tableModel.addRow(new Object[]{
                    m.getMovieId(), m.getTitle(), m.getDirector(),
                    m.getReleaseYear(), m.getDurationMinutes(), m.getGenre(), m.getRating()
            });
        }
        table.clearSelection();
    }

    /**
     * Extracts the currently selected Movie from the table.
     */
    private Movie getSelectedMovieFromTable() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int row = table.convertRowIndexToModel(viewRow);
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        String title = String.valueOf(tableModel.getValueAt(row, 1));
        String director = String.valueOf(tableModel.getValueAt(row, 2));
        int year = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 3)));
        int duration = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 4)));
        String genre = String.valueOf(tableModel.getValueAt(row, 5));
        double rating = Double.parseDouble(String.valueOf(tableModel.getValueAt(row, 6)));
        return new Movie(id, title, director, year, duration, genre, rating);
    }

    /** Shows an error dialog with a friendly message. */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Truncates very long SQL messages to keep dialogs readable. */
    private static String friendlySql(SQLException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) return "Unknown SQL error.";
        if (msg.length() > 300) msg = msg.substring(0, 300) + "...";
        return msg;
    }

    /**
     * Utility: read from System property first, then env var, else default.
     * Example: -DDB_USER=root (or set environment variable DB_USER).
     */
    private static String getPropOrEnv(String key, String defVal) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) return v;
        v = System.getenv(key);
        if (v != null && !v.isBlank()) return v;
        return defVal;
    }
}
