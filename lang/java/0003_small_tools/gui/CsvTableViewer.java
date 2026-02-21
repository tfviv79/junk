import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;



public class CsvTableViewer extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton loadButton;

    public CsvTableViewer() {
        setTitle("CSV Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.tableModel = new DefaultTableModel();
        this.table = new JTable(this.tableModel);
        JScrollPane scrollPane = new JScrollPane(this.table);

        this.loadButton = new JButton("Select csv a file");
        this.loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("select csv file");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(File f) {
                        final boolean isCsvFile = f.isFile() && f.getName().toLowerCase().endsWith(".csv");
                        final boolean isFolder = f.isDirectory();
                        return isFolder || isCsvFile;
                    }
                    public String getDescription() {
                        return "CSV File(*.csv)";
                    }
                });

                int result = fileChooser.showOpenDialog(CsvTableViewer.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    readCsvFile(selectedFile);
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(this.loadButton, BorderLayout.SOUTH);
        add(panel);
    }

    private void readCsvFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = splitLine(line);
                this.tableModel.addRow(parts);
                if (this.tableModel.getRowCount() == 1) {
                    this.tableModel.setColumnIdentifiers(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "error in reading csv file: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] splitLine(String line) {
        List<String> parts = new ArrayList<>();
        for (String p : line.split(",")) {
            parts.add(p);
        }

        return parts.toArray(new String[]{});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CsvTableViewer viewer = new CsvTableViewer();
                viewer.setVisible(true);
            }
        });
    }
}
