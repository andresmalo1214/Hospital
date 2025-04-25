import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddPatientDialog extends JDialog {
    private MainWindow mainWindow;
    private JTextField nombreField, documentoField, edadField, contactoField, correoField, direccionField;
    private JComboBox<String> tipoDocumentoCombo, generoCombo;
    private JButton guardarButton, cancelarButton;

    public AddPatientDialog(MainWindow parent) {
        super(parent, "Agregar paciente", true);
        this.mainWindow = parent;

        setLayout(new BorderLayout());

        // Panel con los campos de entrada
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        nombreField = new JTextField();
        tipoDocumentoCombo = new JComboBox<>(new String[]{"CC", "TI", "CE"});
        documentoField = new JTextField();
        edadField = new JTextField();
        generoCombo = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});
        contactoField = new JTextField();
        correoField = new JTextField();
        direccionField = new JTextField();

        formPanel.add(new JLabel("Nombre completo:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Tipo de documento:"));
        formPanel.add(tipoDocumentoCombo);
        formPanel.add(new JLabel("Número de documento:"));
        formPanel.add(documentoField);
        formPanel.add(new JLabel("Edad:"));
        formPanel.add(edadField);
        formPanel.add(new JLabel("Género:"));
        formPanel.add(generoCombo);
        formPanel.add(new JLabel("Número de contacto:"));
        formPanel.add(contactoField);
        formPanel.add(new JLabel("Correo:"));
        formPanel.add(correoField);
        formPanel.add(new JLabel("Dirección:"));
        formPanel.add(direccionField);

        // Botones
        guardarButton = new JButton("Guardar");
        cancelarButton = new JButton("Cancelar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(guardarButton);
        buttonPanel.add(cancelarButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Acción al hacer clic en "Guardar"
        guardarButton.addActionListener(e -> savePatient());
        cancelarButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    private void savePatient() {
        String nombre = nombreField.getText();
        String documento = (String) tipoDocumentoCombo.getSelectedItem();
        String numeroDocumento = documentoField.getText();
        String edadStr = edadField.getText();
        String genero = (String) generoCombo.getSelectedItem();
        String contacto = contactoField.getText();
        String correo = correoField.getText();
        String direccion = direccionField.getText();

        if (nombre.isEmpty() || numeroDocumento.isEmpty() || edadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios.");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO pacientes (nombre_completo, tipo_documento, numero_documento, edad, genero, numero_contacto, correo, direccion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, documento);
            stmt.setString(3, numeroDocumento);
            stmt.setInt(4, Integer.parseInt(edadStr));
            stmt.setString(5, genero);
            stmt.setString(6, contacto);
            stmt.setString(7, correo);
            stmt.setString(8, direccion);
            stmt.executeUpdate();
            stmt.close();
            conn.close();

            JOptionPane.showMessageDialog(this, "Paciente registrado exitosamente.");
            mainWindow.loadUsers(); // Ahora sí, seguro.

            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar paciente.");
        }
    }
}
