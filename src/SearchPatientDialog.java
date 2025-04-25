import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class SearchPatientDialog extends JDialog {

    private JTextField searchField;
    private JButton searchButton;
    private JButton addDiagnosisButton;
    private JTable resultTable;

    public SearchPatientDialog(JFrame parent) {
        super(parent, "Buscar Paciente", true);
        setSize(900, 450);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Buscar por nombre o número de documento:"));
        searchField = new JTextField(20);
        searchButton = new JButton("Buscar");
        addDiagnosisButton = new JButton("Agregar Diagnóstico");

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addDiagnosisButton);

        add(topPanel, BorderLayout.NORTH);

        resultTable = new JTable(new DefaultTableModel(
                new String[]{"Tipo Doc", "Número Doc", "Nombre", "Edad", "Género", "Fecha/Hora Diagnóstico", "Observaciones", "Médico"}, 0));
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchButton.addActionListener(this::buscarPaciente);
        addDiagnosisButton.addActionListener(this::abrirIngresoDiagnostico);
    }

    private void buscarPaciente(ActionEvent e) {
        String valor = searchField.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre o número de documento.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0); // Limpiar tabla

        String query = """
        SELECT p.tipo_documento, p.numero_documento, p.nombre_completo, p.edad, p.genero,
               CONCAT(d.fecha, ' ', d.hora) AS fecha_hora,
               d.observaciones, m.nombre_medico
        FROM pacientes p
        LEFT JOIN diagnosticos d ON p.id_paciente = d.id_paciente
        LEFT JOIN medicos m ON d.id_medico = m.id_medico
        WHERE p.nombre_completo LIKE ? OR p.numero_documento = ?
        """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setString(1, "%" + valor + "%");
            stmt.setString(2, valor);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("tipo_documento"),
                        rs.getString("numero_documento"),
                        rs.getString("nombre_completo"),
                        rs.getInt("edad"),
                        rs.getString("genero"),
                        rs.getString("fecha_hora"),
                        rs.getString("observaciones"),
                        rs.getString("nombre_medico")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron pacientes.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al buscar pacientes:\n" + ex.getMessage());
        }
    }

    private void abrirIngresoDiagnostico(ActionEvent e) {
        int selectedRow = resultTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un paciente de la tabla.");
            return;
        }

        String nombrePaciente = resultTable.getValueAt(selectedRow, 2).toString();

        IngresoDiagnostico ingreso = new IngresoDiagnostico();
        ingreso.setNombrePaciente(nombrePaciente); // Usa el método público
        ingreso.setVisible(true);
    }
}
