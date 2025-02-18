package prospering;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

public class NotePad {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new NotePadLibrary();
        });
    }
}

class NotePadLibrary extends JFrame implements ActionListener {

    private JTextArea area;
    private JMenuItem saveItem, loadItem, aboutItem, exitItem, clearItem;

    public NotePadLibrary() {
        super("Notepad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setupMenuBar();
        setupTextArea();
        setVisible(true);
    }

    private void setupTextArea() {
        area = new JTextArea();
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createHelpMenu());
        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        saveItem = createMenuItem("Save", KeyEvent.VK_S, KeyEvent.VK_S, "icons/save.png");
        loadItem = createMenuItem("Open", KeyEvent.VK_O, KeyEvent.VK_O, "icons/open.png");
        clearItem = createMenuItem("Clear", KeyEvent.VK_C, KeyEvent.VK_C, "icons/clear.png");
        exitItem = createMenuItem("Exit", KeyEvent.VK_E, KeyEvent.VK_Q, "icons/exit.png");

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        aboutItem = createMenuItem("About", KeyEvent.VK_A, 0, "icons/about.png");
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private JMenuItem createMenuItem(String text, int mnemonic, int acceleratorKey, String iconPath) {
        JMenuItem item = new JMenuItem(text, new ImageIcon(iconPath));
        item.setMnemonic(mnemonic);
        if (acceleratorKey != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, ActionEvent.CTRL_MASK));
        }
        item.addActionListener(this);
        return item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == saveItem) {
                saveToFile();
            } else if (e.getSource() == loadItem) {
                loadFromFile();
            } else if (e.getSource() == exitItem) {
                System.exit(0);
            } else if (e.getSource() == aboutItem) {
                JOptionPane.showMessageDialog(this, "NotePad-\nVersion 1.2\nA simple text editing tool.", "About Notepad", JOptionPane.INFORMATION_MESSAGE);
            } else if (e.getSource() == clearItem) {
                area.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveToFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                area.write(writer);
            }
        }
    }

    private void loadFromFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                area.read(reader, null);
            }
        }
    }
}