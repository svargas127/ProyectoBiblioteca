import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Main {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtTitulo, txtAutor, txtGenero, txtAnio;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("Gestión de Biblioteca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Tabla de libros
        tableModel = new DefaultTableModel(new String[]{"ID", "Título", "Autor", "Género", "Año"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        txtTitulo = new JTextField();
        txtAutor = new JTextField();
        txtGenero = new JTextField();
        txtAnio = new JTextField();

        formPanel.add(new JLabel("Título:"));
        formPanel.add(txtTitulo);
        formPanel.add(new JLabel("Autor:"));
        formPanel.add(txtAutor);
        formPanel.add(new JLabel("Género:"));
        formPanel.add(txtGenero);
        formPanel.add(new JLabel("Año:"));
        formPanel.add(txtAnio);

        // Botones
        JButton btnAdd = new JButton("Añadir");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // Acciones de los botones
        btnAdd.addActionListener(this::addBook);
        btnEdit.addActionListener(this::editBook);
        btnDelete.addActionListener(this::deleteBook);
        btnRefresh.addActionListener(e -> loadBooks());

        // Construcción de la ventana
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(formPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // Cargar datos iniciales
        loadBooks();
    }

    private void loadBooks() {
        try (Connection conn = DataBase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM libros")) {


// limpia la tabla
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getString("anio_publicacion")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error al cargar los datos: " + ex.getMessage());
        }
    }

    private void addBook(ActionEvent e) {
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String genero = txtGenero.getText();
        String anio = txtAnio.getText();

        String query = "INSERT INTO libros (titulo, autor, genero, anio_publicacion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setString(3, genero);
            pstmt.setString(4, anio);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Libro añadido correctamente.");
            loadBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error al añadir libro: " + ex.getMessage());
        }
    }

    private void editBook(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Selecciona un libro para editar.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String titulo = txtTitulo.getText();
        String autor = txtAutor.getText();
        String genero = txtGenero.getText();
        String anio_publicacion = txtAnio.getText();

        String query = "UPDATE libros SET titulo = ?, autor = ?, genero = ?, anio_publicacion = ? WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, titulo);
            pstmt.setString(2, autor);
            pstmt.setString(3, genero);
            pstmt.setString(4, anio_publicacion);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Libro actualizado correctamente.");
            loadBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error al editar libro: " + ex.getMessage());
        }
    }

    private void deleteBook(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Selecciona un libro para eliminar.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Libro eliminado correctamente.");
            loadBooks();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error al eliminar libro: " + ex.getMessage());
        }
    }
}