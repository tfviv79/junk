
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
 *   http://kamifuji.dyndns.org/JSupport/JAVA_Swing/JTable/index.html#_0050
 *
 *   https://docs.oracle.com/javase/tutorial/uiswing/index.html
 *   https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/BorderLayout.html
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


    public static void debug(String fmt, Object ... args) {
        System.out.println(String.format(fmt, args));
    }
}


class PageSwitcher {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    public PageSwitcher() {
        this.cardPanel = new JPanel();
        this.cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
    }

    public JPanel panel() {
        return cardPanel;
    }

    public PageSwitcher add(JPanel comp, String name) {
        cardPanel.add(comp, name);
        return this;
    }

    public void show(String name) {
        cardLayout.show(cardPanel, name);
        Main.debug("### switch %s page", name);
    }
}

class MainFrame extends JFrame {
    public MainFrame() {
        super("Sample");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        var switcher = new PageSwitcher();

        switcher.add(new DisplayTsvFile(switcher), "tsvPanel");
        switcher.add(new SamplePanel(switcher), "sample");
        switcher.show("tsvPanel");

        add(switcher.panel());

        //Display the window.
        pack();
        setSize(600, 400);
        setVisible(true);
    }
}


class SamplePanel extends JPanel implements ActionListener {
    private PageSwitcher switcher;

    public SamplePanel(PageSwitcher switcher) {
        super(new BorderLayout());
        this.switcher = switcher;

        var changeButton = new JButton("toTsv");
        changeButton.addActionListener(this);
        changeButton.setActionCommand("tsvPanel");


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(changeButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.PAGE_START);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();
        switch (command) {
            case "tsvPanel":
                this.switcher.show("tsvPanel");
                break;
        }
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
    private PageSwitcher switcher;

    public DisplayTsvFile(PageSwitcher switcher) {
        super(new BorderLayout());
        this.switcher = switcher;

        tsvTable = new TsvTable();
        JScrollPane logScrollPane = new JScrollPane(tsvTable);

        final var pwd = new File("").getAbsolutePath();
        fc = new JFileChooser(pwd);

        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);
        openButton.setActionCommand("openFile");

        var changeButton = new JButton("toSample");
        changeButton.addActionListener(this);
        changeButton.setActionCommand("sample");


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(openButton, BorderLayout.WEST);
        buttonPanel.add(changeButton, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();
        switch (command) {
            case "openFile":
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    TsvFile tsvFile = TsvFile.load(file);
                    tsvTable.setData(tsvFile);
                }
                break;
            case "sample":
                this.switcher.show("sample");
                break;
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
