

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddUserDialog extends JDialog {
    private JTextField nombreField;
    private JPasswordField contraseñaField;
    private JTextField idMedicoField;
    private MainWindow mainWindow;

    public AddUserDialog(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setTitle("Agregar Usuario");
        setSize(300, 200);
        setModal(true);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        add(nombreField);

        add(new JLabel("Contraseña:"));
        contraseñaField = new JPasswordField();
        add(contraseñaField);

        add(new JLabel("ID Médico:"));
        idMedicoField = new JTextField();
        add(idMedicoField);

        JButton guardarButton = new JButton("Guardar");
        guardarButton.addActionListener(e -> guardarUsuario());
        add(guardarButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dispose());
        add(cancelarButton);
    }

    private void guardarUsuario() {
        String nombre = nombreField.getText();
        String contraseña = new String(contraseñaField.getPassword());
        int idMedico = Integer.parseInt(idMedicoField.getText());

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO usuarios (nombre, contraseña, id_medico) VALUES (?, ?, ?)"
            );
            stmt.setString(1, nombre);
            stmt.setString(2, contraseña);
            stmt.setInt(3, idMedico);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
            dispose(); // cierra la ventana
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar usuario.");
        }
    }
}
