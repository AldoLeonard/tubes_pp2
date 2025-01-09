
package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DropboxView {
    @SuppressWarnings("CallToPrintStackTrace")
    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/admin_db", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!");
        }
        return conn;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void loadData(DefaultTableModel tableModel) {
        // Hapus semua baris sebelumnya di tabel
        tableModel.setRowCount(0);
        
        String query = "SELECT * FROM dropbox";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("kapasitas"),
                    rs.getString("status"),
                    rs.getString("alamat")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data!");
        }
    }

    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace", "Convert2Lambda"})
    public static void open() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Dropbox Management");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Nama Dropbox", "Kapasitas", "Status", "Alamat Dropbox"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        
        // Menambahkan garis grid pada tabel
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);

        // Menonaktifkan editor tabel untuk menjadikannya read-only
        table.setDefaultEditor(Object.class, null);

        // Mengatur lebar kolom
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nama Dropbox
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Kapasitas
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(200); // Alamat Dropbox
        
        // Menambahkan JScrollPane untuk tabel
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(750, 400));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Tambah Data");
        JButton updateButton = new JButton("Ubah Data");
        JButton deleteButton = new JButton("Hapus Data");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panggil loadData di sini untuk memuat data saat aplikasi dibuka
        loadData(tableModel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField namaField = new JTextField(10);
                JTextField kapasitasField = new JTextField(10);
                JTextField statusField = new JTextField(10);
                JTextField alamatField = new JTextField(10);

                JPanel inputPanel = new JPanel(new GridLayout(4, 2));
                inputPanel.add(new JLabel("Nama Dropbox:"));
                inputPanel.add(namaField);
                inputPanel.add(new JLabel("Kapasitas:"));
                inputPanel.add(kapasitasField);
                inputPanel.add(new JLabel("Status:"));
                inputPanel.add(statusField);
                inputPanel.add(new JLabel("Alamat Dropbox:"));
                inputPanel.add(alamatField);

                int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Tambah Data", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String nama = namaField.getText();
                    String kapasitas = kapasitasField.getText();
                    String status = statusField.getText();
                    String alamat = alamatField.getText();

                    try (Connection conn = connect(); 
                         PreparedStatement ps = conn.prepareStatement("INSERT INTO dropbox (nama, kapasitas, status, alamat) VALUES (?, ?, ?, ?)");) {
                        ps.setString(1, nama);
                        ps.setString(2, kapasitas);
                        ps.setString(3, status);
                        ps.setString(4, alamat);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Data berhasil ditambahkan!");

                        // Panggil loadData untuk memperbarui tabel setelah menambahkan data
                        loadData(tableModel);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Gagal menambahkan data!");
                    }
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Fitur ubah data belum diimplementasikan.");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Fitur hapus data belum diimplementasikan.");
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        open();
    }
}
