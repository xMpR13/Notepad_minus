package prospering;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.prefs.Preferences;

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
    private JCheckBoxMenuItem darkModeItem, wordWrapItem;
    private JLabel statusBar;
    private int lastMatchIndex = -1;
  //  private JMenuItem zoomInItem, zoomOutItem;
    //private int fontSize = 14;
    private Preferences preferences;

    public NotePadLibrary() {
        super("Notepad");
        preferences = Preferences.userNodeForPackage(NotePadLibrary.class);
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
        area.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateWordAndCharCount();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateWordAndCharCount();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateWordAndCharCount();
            }
        });
    
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textPanel.add(new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
    
        statusBar = new JLabel("Words: 0 | Characters: 0");
        textPanel.add(statusBar, BorderLayout.SOUTH);
    
        add(textPanel, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createPreferencesMenu());
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

    private JMenu createPreferencesMenu() {
        JMenu preferencesMenu = new JMenu("Preferences");
        preferencesMenu.setMnemonic(KeyEvent.VK_P);

        JMenuItem fontStyleItem = new JMenuItem("Font Style");
        JMenuItem fontSizeItem = new JMenuItem("Font Size");
        JMenuItem fontColorItem = new JMenuItem("Font Color");

        fontStyleItem.addActionListener(e -> changeFontStyle());
        fontSizeItem.addActionListener(e -> changeFontSize());
        fontColorItem.addActionListener(e -> changeFontColor());

        darkModeItem = new JCheckBoxMenuItem("Dark Mode");
        darkModeItem.addActionListener(e -> toggleDarkMode());

        preferencesMenu.add(fontStyleItem);
        preferencesMenu.add(fontSizeItem);
        preferencesMenu.add(fontColorItem);
        preferencesMenu.addSeparator();
        preferencesMenu.add(darkModeItem);

        return preferencesMenu;
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
            item.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, KeyEvent.CTRL_DOWN_MASK));
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
                JOptionPane.showMessageDialog(this, "Notepad Minus\nVersion 1.4\nA simple text editing tool.", "About Notepad", JOptionPane.INFORMATION_MESSAGE);
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
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
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

    private void changeFontStyle() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(this, "Choose Font Style:", "Font Style",
                JOptionPane.PLAIN_MESSAGE, null, fonts, area.getFont().getFamily());
        if (selectedFont != null) {
            area.setFont(new Font(selectedFont, area.getFont().getStyle(), area.getFont().getSize()));
        }
    }

    private void updateWordAndCharCount() {
        String text = area.getText();
        int charCount = text.length();
        String[] words = text.trim().split("\\s+");
        int wordCount = (text.trim().isEmpty()) ? 0 : words.length;
        statusBar.setText("Words: " + wordCount + " | Characters: " + charCount);
    }

    private void changeFontSize() {
        String[] sizes = {"12", "14", "16", "18", "20", "24", "28", "32"};
        String selectedSize = (String) JOptionPane.showInputDialog(this, "Choose Font Size:", "Font Size",
                JOptionPane.PLAIN_MESSAGE, null, sizes, String.valueOf(area.getFont().getSize()));
        if (selectedSize != null) {
            area.setFont(new Font(area.getFont().getFamily(), area.getFont().getStyle(), Integer.parseInt(selectedSize)));
        }
    }

    private void changeFontColor() {
        Color color = JColorChooser.showDialog(this, "Choose Font Color", area.getForeground());
        if (color != null) {
            area.setForeground(color);
        }
    }

    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
    
        JMenuItem findReplaceItem = new JMenuItem("Find and Replace");
        findReplaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        findReplaceItem.addActionListener(e -> openFindReplaceDialog());
    
        editMenu.add(findReplaceItem);
    
        return editMenu;
    }

    private void openFindReplaceDialog() {
        JDialog dialog = new JDialog(this, "Find and Replace", false);
        dialog.setLayout(new GridLayout(3, 2));
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);
    
        JTextField findField = new JTextField();
        JTextField replaceField = new JTextField();
        JButton findNextButton = new JButton("Find Next");
        JButton replaceButton = new JButton("Replace");
        JButton replaceAllButton = new JButton("Replace All");
    
        dialog.add(new JLabel("Find:"));
        dialog.add(findField);
        dialog.add(new JLabel("Replace With:"));
        dialog.add(replaceField);
        dialog.add(findNextButton);
        dialog.add(replaceButton);
        dialog.add(new JLabel());
        dialog.add(replaceAllButton);
    
        findNextButton.addActionListener(e -> findNext(findField.getText()));
        replaceButton.addActionListener(e -> replace(findField.getText(), replaceField.getText()));
        replaceAllButton.addActionListener(e -> replaceAll(findField.getText(), replaceField.getText()));
    
        dialog.setVisible(true);
    }

    private void findNext(String searchText) {
        String content = area.getText();
        lastMatchIndex = content.indexOf(searchText, lastMatchIndex + 1);
    
        if (lastMatchIndex >= 0) {
            area.setCaretPosition(lastMatchIndex + searchText.length());
            area.select(lastMatchIndex, lastMatchIndex + searchText.length());
            area.grabFocus();
        } else {
            lastMatchIndex = -1;
            JOptionPane.showMessageDialog(this, "No more occurrences found.");
        }
    }

    private void replace(String searchText, String replaceText) {
        if (area.getSelectedText() != null && area.getSelectedText().equals(searchText)) {
            area.replaceSelection(replaceText);
        }
        findNext(searchText);
    }

    private void replaceAll(String searchText, String replaceText) {
        area.setText(area.getText().replaceAll(searchText, replaceText));
    }

    private void toggleDarkMode() {
        if (darkModeItem.isSelected()) {
            area.setBackground(Color.DARK_GRAY);
            area.setForeground(Color.WHITE);
        } else {
            area.setBackground(Color.WHITE);
            area.setForeground(Color.BLACK);
        }
    }

    private void toggleWordWrap() {
        area.setLineWrap(wordWrapItem.isSelected());
        area.setWrapStyleWord(wordWrapItem.isSelected());
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
        wordWrapItem.setSelected(preferences.getBoolean("wordWrap", false));
        wordWrapItem.addActionListener(e -> toggleWordWrap());

        viewMenu.add(wordWrapItem);

        return viewMenu;
    }
}