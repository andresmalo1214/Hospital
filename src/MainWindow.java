import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends JFrame {

    private JTable table;
    private JButton addButton, updateButton, deleteButton, addPatientButton, buscarPatientButton;

    public MainWindow() {
        setTitle("Gestión de Usuarios");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel bienvenida = new JLabel("¡Bienvenido a la ventana principal!");
        add(bienvenida, BorderLayout.NORTH);

        String[] columns = {"ID", "Nombre", "Contraseña", "ID Médico"};
        table = new JTable(new DefaultTableModel(columns, 0));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        addButton = new JButton("Agregar Usuario");
        updateButton = new JButton("Actualizar Usuario");
        deleteButton = new JButton("Eliminar Usuario");
        addPatientButton = new JButton("Agregar Paciente");
        buscarPatientButton = new JButton("Buscar Paciente"); // <-- Nuevo botón

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(addPatientButton);
        panel.add(buscarPatientButton); // <-- Agregado al panel

        add(panel, BorderLayout.SOUTH);

        // Menú inicial
        String[] opciones = {
                "Agregar Usuario",
                "Actualizar Usuario",
                "Eliminar Usuario",
                "Ver Todos",
                "Agregar Paciente",
                "Buscar Paciente"
        };

        int seleccion = JOptionPane.showOptionDialog(
                this,
                "¿Qué acción deseas realizar?",
                "Seleccionar acción",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        switch (seleccion) {
            case 0:
                new AddUserDialog(this).setVisible(true);
                break;
            case 1:
                JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla para actualizar.");
                break;
            case 2:
                JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla para eliminar.");
                break;
            case 3:
                break;
            case 4:
                new AddPatientDialog(this).setVisible(true);
                break;
            case 5:
                new SearchPatientDialog(this).setVisible(true); // <-- Acción del botón
                break;
            default:
                JOptionPane.showMessageDialog(this, "No se seleccionó ninguna acción. Cerrando...");
                dispose();
                return;
        }

        loadUsers();

        addButton.addActionListener(e -> {
            AddUserDialog dialog = new AddUserDialog(this);
            dialog.setVisible(true);
            loadUsers();
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) table.getValueAt(selectedRow, 0);
                UpdateUserDialog dialog = new UpdateUserDialog(MainWindow.this, userId);
                dialog.setVisible(true);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) table.getValueAt(selectedRow, 0);
                deleteUser(userId);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
            }
        });

        addPatientButton.addActionListener(e -> {
            AddPatientDialog dialog = new AddPatientDialog(this);
            dialog.setVisible(true);
        });

        buscarPatientButton.addActionListener(e -> {
            SearchPatientDialog dialog = new SearchPatientDialog(this);
            dialog.setVisible(true);
        });
    }

    public void loadUsers() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Nombre", "Contraseña", "ID Médico"});
        table.setModel(model);

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios")
        ) {
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getInt("id_usuario");
                row[1] = rs.getString("nombre");
                row[2] = rs.getString("contraseña");
                row[3] = rs.getInt("id_medico");
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los usuarios.");
        }
    }

    private void deleteUser(int userId) {
        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM usuarios WHERE id_usuario = ?")
        ) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el usuario con ID: " + userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar el usuario:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
