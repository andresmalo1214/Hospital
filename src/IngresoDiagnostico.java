import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class IngresoDiagnostico extends JFrame {

    private JTextField nombrePacienteField;
    private JTextArea observacionesField;
    private JButton ingresarButton;

    public IngresoDiagnostico() {
        // Configuración de la ventana
        setTitle("Ingreso de Diagnóstico");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Mejor que EXIT_ON_CLOSE para ventanas secundarias
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Crear los componentes
        JLabel nombrePacienteLabel = new JLabel("Nombre del Paciente:");
        nombrePacienteField = new JTextField(20);
        JLabel observacionesLabel = new JLabel("Observaciones:");
        observacionesField = new JTextArea(5, 20);
        ingresarButton = new JButton("Ingresar Diagnóstico");

        // Agregar los componentes a la ventana
        add(nombrePacienteLabel);
        add(nombrePacienteField);
        add(observacionesLabel);
        add(new JScrollPane(observacionesField)); // Hacemos que el área de texto sea desplazable
        add(ingresarButton);

        // Acción al presionar el botón
        ingresarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombrePaciente = nombrePacienteField.getText().trim();
                String observaciones = observacionesField.getText().trim();

                if (nombrePaciente.isEmpty() || observaciones.isEmpty()) {
                    JOptionPane.showMessageDialog(IngresoDiagnostico.this, "Todos los campos son obligatorios.");
                    return;
                }

                ingresarDiagnostico(nombrePaciente, observaciones);
            }
        });
    }

    private void ingresarDiagnostico(String nombrePaciente, String observaciones) {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // Buscar el ID del paciente
            String sqlSelect = "SELECT id_paciente FROM pacientes WHERE nombre_completo = ?";
            selectStmt = conn.prepareStatement(sqlSelect);
            selectStmt.setString(1, nombrePaciente);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                int idPaciente = rs.getInt("id_paciente");

                // Insertar el diagnóstico
                String sqlInsert = "INSERT INTO diagnosticos (fecha, hora, observaciones, id_medico, id_paciente) VALUES (CURDATE(), CURTIME(), ?, 1, ?)";
                insertStmt = conn.prepareStatement(sqlInsert);
                insertStmt.setString(1, observaciones);
                insertStmt.setInt(2, idPaciente);

                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Diagnóstico ingresado exitosamente.");
                dispose(); // Cerrar la ventana al finalizar
            } else {
                JOptionPane.showMessageDialog(this, "Paciente no encontrado.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ingresar diagnóstico:\n" + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectStmt != null) selectStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Método público para establecer el nombre del paciente
    public void setNombrePaciente(String nombre) {
        nombrePacienteField.setText(nombre);
        nombrePacienteField.setEditable(false); // Opcional: bloquear edición
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IngresoDiagnostico().setVisible(true));
    }
}
