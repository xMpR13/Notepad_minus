package mpuruprosper;

// prospermpuru was here🙃🙃

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NotePad {

    public static void main(String[] args) {
        new NotePadLibrary();
    }

}

class NotePadLibrary extends JFrame implements ActionListener {

    private final JTextArea area;
    private JMenuBar menuBar;
    private final JMenuItem saveItem;
    private final JMenuItem loadItem;
    private final JMenuItem aboutItem;
    private final JMenuItem exitItem;
    private final JScrollPane xy;
    private final JMenu FileMenu;
    private final JMenu aboutMenu;
    private BufferedWriter writer;
    private BufferedReader reader;
    private JFileChooser fileChooser;

    public NotePadLibrary() {

        JFrame frame = new JFrame("Notepad");
        frame.setDefaultCloseOperation(3);
        //frame.setAlwaysOnTop(true);
        frame.setSize(400, 250);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        //---------------Menu bar
        menuBar = new JMenuBar();

        //file menu
        FileMenu = new JMenu("File");

        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(this);
        loadItem = new JMenuItem("Load");
        loadItem.addActionListener(this);
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);

        //adds items to menu bar
        FileMenu.add(saveItem);
        FileMenu.add(loadItem);
        FileMenu.addSeparator();
        FileMenu.add(exitItem);

        //---------------About menu bar
        menuBar = new JMenuBar();

        //file menu
        aboutMenu = new JMenu("About");

        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);

        aboutMenu.add(aboutItem);

        //adds menubar to frame
        menuBar.add(FileMenu);
        menuBar.add(aboutMenu);
        //---------------Menu bar

        //---------------text area
        area = new JTextArea(25, 25);
        area.setWrapStyleWord(true);
        xy = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //---------------text area

        frame.setJMenuBar(menuBar);
        frame.add(xy);

        //frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (saveItem == e.getSource()) {

            String text = area.getText();

            try {

                fileChooser = new JFileChooser();

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                    File file = fileChooser.getSelectedFile();

                    writer = new BufferedWriter(new FileWriter(file, false));

                    writer.write(text);
                    writer.newLine();
                    writer.close();
                    JOptionPane.showMessageDialog(null, "File saved");
                }

            } catch (IOException ex) {
                Logger.getLogger(NotePadLibrary.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (loadItem == e.getSource()) {

            String data = "", str;
            fileChooser = new JFileChooser();

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                try {
                    File file = fileChooser.getSelectedFile();

                    reader = new BufferedReader(new FileReader(file));

                    while ((str = reader.readLine()) != null) {
                        data += str + "\n";
                    }
                    reader.close();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(NotePadLibrary.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(NotePadLibrary.class.getName()).log(Level.SEVERE, null, ex);
                }

                area.setText(data);

            }
        } else if (exitItem == e.getSource()) {
            System.exit(0);
        } else if (aboutItem == e.getSource()) {
            JOptionPane.showMessageDialog(null, "prosper was here doing stuff");
        }
    }
}
