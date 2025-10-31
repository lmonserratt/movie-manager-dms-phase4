package dms.gui;

import dms.model.Movie;
import dms.service.MovieService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Modal dialog for CREATE/EDIT of Movie. */
public class MovieFormDialog extends JDialog {
    private JTextField txtId, txtTitle, txtDirector, txtGenre;
    private JSpinner spYear, spDuration, spRating;
    private final MovieService service;
    private final boolean editMode;
    private final Runnable onSuccess;

    public static void openCreate(Window owner, MovieService service, Runnable onSuccess) {
        MovieFormDialog dlg = new MovieFormDialog(owner, service, null, onSuccess);
        dlg.setVisible(true);
    }
    public static void openEdit(Window owner, MovieService service, Movie existing, Runnable onSuccess) {
        MovieFormDialog dlg = new MovieFormDialog(owner, service, existing, onSuccess);
        dlg.setVisible(true);
    }

    public MovieFormDialog(Window owner, MovieService service, Movie existing, Runnable onSuccess) {
        super(owner, (existing == null ? "Add Movie" : "Edit Movie"), ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.editMode = (existing != null);
        this.onSuccess = (onSuccess != null ? onSuccess : () -> {});
        initUi();
        setLocationRelativeTo(owner);
        if (editMode) loadExisting(existing);
    }

    private void initUi() {
        txtId = new JTextField(12);
        txtTitle = new JTextField(20);
        txtDirector = new JTextField(20);
        txtGenre = new JTextField(16);
        spYear = new JSpinner(new SpinnerNumberModel(2010, 1888, 2100, 1));
        spDuration = new JSpinner(new SpinnerNumberModel(120, 1, 999, 1));
        spRating = new JSpinner(new SpinnerNumberModel(7.5, 0.0, 10.0, 0.1));
        JButton btnSave = new JButton(editMode ? "Update" : "Create");
        JButton btnCancel = new JButton("Cancel");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8); c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        addRow(form, c, row++, new JLabel("Movie ID:"), txtId);
        addRow(form, c, row++, new JLabel("Title:"), txtTitle);
        addRow(form, c, row++, new JLabel("Director:"), txtDirector);
        addRow(form, c, row++, new JLabel("Release Year:"), spYear);
        addRow(form, c, row++, new JLabel("Duration (min):"), spDuration);
        addRow(form, c, row++, new JLabel("Genre:"), txtGenre);
        addRow(form, c, row++, new JLabel("Rating (0.0–10.0):"), spRating);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancel); buttons.add(btnSave);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        root.add(form, BorderLayout.CENTER); root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root); pack(); setResizable(false);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());
        getRootPane().setDefaultButton(btnSave);
    }

    private static void addRow(JPanel p, GridBagConstraints c, int row, JComponent label, JComponent field) {
        c.gridy=row; c.gridx=0; c.weightx=0.0; p.add(label,c);
        c.gridx=1; c.weightx=1.0; p.add(field,c);
    }

    private void loadExisting(Movie m) {
        txtId.setText(m.getMovieId()); txtId.setEnabled(false);
        txtTitle.setText(m.getTitle()); txtDirector.setText(m.getDirector());
        spYear.setValue(m.getReleaseYear()); spDuration.setValue(m.getDurationMinutes());
        txtGenre.setText(m.getGenre()); spRating.setValue(m.getRating());
    }

    private void onSave() {
        try {
            String id = txtId.getText();
            String title = txtTitle.getText();
            String director = txtDirector.getText();
            int year = ((Number) spYear.getValue()).intValue();
            int duration = ((Number) spDuration.getValue()).intValue();
            String genre = txtGenre.getText();
            double rating = ((Number) spRating.getValue()).doubleValue();

            Movie movie = new Movie(id, title, director, year, duration, genre, rating);

            if (editMode) {
                service.update(movie);
                JOptionPane.showMessageDialog(this, "Movie updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                service.create(movie);
                JOptionPane.showMessageDialog(this, "Movie created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            onSuccess.run(); dispose();

        } catch (IllegalArgumentException iae) {
            showError("Validation error:\n" + iae.getMessage());
        } catch (SQLException sqle) {
            showError("Database error:\n" + friendlySqlMessage(sqle));
        } catch (Exception ex) {
            showError("Unexpected error:\n" + ex.getMessage());
        }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }

    private static String friendlySqlMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) return "Unknown SQL error.";
        if (msg.length() > 300) msg = msg.substring(0, 300) + "...";
        return msg;
    }
}
