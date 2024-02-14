
import java.util.Arrays;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.awt.*;
import javax.swing.*;
/**
 *
 *
 *
 * refs:
 *   https://docs.oracle.com/javase/tutorial/uiswing/components/index.html
 *   https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemoProject/src/components/FileChooserDemo.java
 *
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                new MainFrame();
            }
        });
    }
}

class MainFrame extends JFrame {
    public MainFrame() {
        super("Sample");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //Add content to the window.
        add(new DisplayTsvFile());
        //Display the window.
        pack();

        setSize(600, 400);
        setVisible(true);
    }
}

class TsvTable extends JPanel {
    private JScrollPane scrollPane;

    public TsvTable() {
        super(new GridLayout(1,0));


        JTable table = new JTable();
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    public void setData(TsvFile tsvData) {
        remove(scrollPane);
        scrollPane = null;
        repaint();

        JTable table = new JTable(tsvData.records.toArray(new String[][]{}), tsvData.headers);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        scrollPane = new JScrollPane(table);
        add(scrollPane);
        revalidate();
    }
}

class DisplayTsvFile extends JPanel implements ActionListener {
    JButton openButton;
    TsvTable tsvTable;
    JFileChooser fc;

    public DisplayTsvFile() {
        super(new BorderLayout());

        tsvTable = new TsvTable();
        JScrollPane logScrollPane = new JScrollPane(tsvTable);

        final var pwd = new File("").getAbsolutePath();
        fc = new JFileChooser(pwd);

        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(openButton);
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                TsvFile tsvFile = TsvFile.load(file);
                tsvTable.setData(tsvFile);
            }
        }
    }
}


class TsvFile {
    public String[] headers;
    public java.util.List<String[]> records;
    public TsvFile() {
        this.headers = null;
        this.records = new java.util.ArrayList<>();
    }


    public static TsvFile load(File file) {
        TsvFile ret = new TsvFile();
        try (InputStream is = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                ) {
            int rowNum = 0;
            String line;
            while ((line = br.readLine()) != null) {
                rowNum++;
                String[] rows = line.split("\t");
                if (rowNum  == 1) {
                    ret.headers = rows;
                    continue;
                }
                ret.records.add(rows);
            }
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
