import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateUserDialog extends JDialog {

    private JTextField nameField, passwordField;
    private JComboBox<Integer> doctorIdCombo;
    private JButton saveButton;
    private int userId;

    public UpdateUserDialog(JFrame parent, int userId) {
        super(parent, "Actualizar Usuario", true);
        this.userId = userId;  // Guarda el ID del usuario

        setSize(400, 300);
        setLayout(new FlowLayout());

        // Crear los campos de la interfaz
        JLabel nameLabel = new JLabel("Nombre:");
        nameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordField = new JTextField(20);

        JLabel doctorIdLabel = new JLabel("ID Médico:");
        doctorIdCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});  // Asume que tienes IDs de médicos predefinidos

        saveButton = new JButton("Guardar");

        // Agregar componentes a la ventana
        add(nameLabel);
        add(nameField);
        add(passwordLabel);
        add(passwordField);
        add(doctorIdLabel);
        add(doctorIdCombo);
        add(saveButton);

        // Cargar la información del usuario
        loadUserData();

        // Acción del botón Guardar
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateUser();
            }
        });
    }

    private void loadUserData() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            System.out.println("Cargando datos para el usuario con ID: " + userId); // DEBUG
            String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("nombre"));
                passwordField.setText(rs.getString("contraseña"));
                doctorIdCombo.setSelectedItem(rs.getInt("id_medico"));
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el usuario con ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los datos del usuario.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUser() {
        String name = nameField.getText();
        String password = passwordField.getText();
        int doctorId = (int) doctorIdCombo.getSelectedItem();

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE usuarios SET nombre = ?, contraseña = ?, id_medico = ? WHERE id_usuario = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setInt(3, doctorId);
            stmt.setInt(4, userId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar el usuario.");
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
