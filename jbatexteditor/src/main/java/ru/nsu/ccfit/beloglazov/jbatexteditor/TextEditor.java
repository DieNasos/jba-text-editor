package ru.nsu.ccfit.beloglazov.jbatexteditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TextEditor extends JFrame {
    private JTextField searchField;
    private final JCheckBox useRegEx = new JCheckBox("Use regex");
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private String filename = null;
    private final ArrayList<IntPair> matchs = new ArrayList<>();
    private int currentMatch;

    public TextEditor() {
        super("Text Editor :: select file");
        setMinimumSize(new Dimension(1000, 550));
        setPreferredSize(new Dimension(1000, 550));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {
        fileChooser = new JFileChooser();
        fileChooser.setName("FileChooser");
        add(fileChooser);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JMenuItem menuLoad = new JMenuItem("Open");
        menuLoad.setName("MenuOpen");
        menuLoad.addActionListener(event -> readFromFile());
        JMenuItem menuSave = new JMenuItem("Save");
        menuSave.setName("MenuSave");
        menuSave.addActionListener(event -> writeToFile());
        JMenuItem menuExit = new JMenuItem("Exit");
        menuExit.setName("MenuExit");
        menuExit.addActionListener(event -> dispose());
        JMenu menuFile = new JMenu("File");
        menuFile.setName("MenuFile");
        menuFile.add(menuLoad);
        menuFile.add(menuSave);
        menuFile.addSeparator();
        menuFile.add(menuExit);
        JMenuItem menuStartSearch = new JMenuItem("Start search");
        menuStartSearch.setName("MenuStartSearch");
        menuStartSearch.addActionListener(event -> search());
        JMenuItem menuPreviousMatch = new JMenuItem("Previous match");
        menuPreviousMatch.setName("MenuPreviousMatch");
        menuPreviousMatch.addActionListener(event -> selectPrevious());
        JMenuItem menuNextMatch = new JMenuItem("Next match");
        menuNextMatch.setName("MenuNextMatch");
        menuNextMatch.addActionListener(event -> selectNext());
        JMenuItem menuUseRegExp = new JMenuItem("Use regex");
        menuUseRegExp.setName("MenuUseRegExp");
        menuUseRegExp.addActionListener(event -> useRegEx.doClick());
        JMenu menuSearch = new JMenu("Search");
        menuSearch.setName("MenuSearch");
        menuSearch.add(menuStartSearch);
        menuSearch.add(menuPreviousMatch);
        menuSearch.add(menuNextMatch);
        menuSearch.add(menuUseRegExp);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuSearch);
        panel1.add(menuBar);
        add(panel1, BorderLayout.NORTH);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton saveButton = new JButton();
        try {
            File saveImageFile = new File("src/main/resources/saveicon16x16.gif");
            if (saveImageFile.exists()) {
                Image saveImage = ImageIO.read(saveImageFile);
                ImageIcon saveIcon = new ImageIcon(saveImage);
                saveButton.setIcon(saveIcon);
            } else {
                saveButton.setText("Save");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(new Dimension(70, 25));
        saveButton.addActionListener(e -> writeToFile());
        JButton openButton = new JButton();
        try {
            File openImageFile = new File("src/main/resources/openicon16x16.gif");
            if (openImageFile.exists()) {
                Image openImage = ImageIO.read(openImageFile);
                ImageIcon openIcon = new ImageIcon(openImage);
                openButton.setIcon(openIcon);
            } else {
                openButton.setText("Open");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        openButton.setName("OpenButton");
        openButton.setPreferredSize(new Dimension(70, 25));
        openButton.addActionListener(e -> readFromFile());
        searchField = new JTextField();
        searchField.setName("SearchField");
        searchField.setColumns(15);
        Font font = searchField.getFont().deriveFont(16f);
        searchField.setFont(font);
        JButton searchButton = new JButton();
        try {
            File searchImageFile = new File("src/main/resources/searchicon16x16.gif");
            if (searchImageFile.exists()) {
                Image searchImage = ImageIO.read(searchImageFile);
                ImageIcon searchIcon = new ImageIcon(searchImage);
                searchButton.setIcon(searchIcon);
            } else {
                searchButton.setText("Search");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchButton.setName("StartSearchButton");
        searchButton.setPreferredSize(new Dimension(80, 25));
        searchButton.addActionListener(e -> search());
        JButton previousButton = new JButton();
        try {
            File previousImageFile = new File("src/main/resources/previousicon16x16.gif");
            if (previousImageFile.exists()) {
                Image previousImage = ImageIO.read(previousImageFile);
                ImageIcon previousIcon = new ImageIcon(previousImage);
                previousButton.setIcon(previousIcon);
            } else {
                previousButton.setText("<");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        previousButton.setName("PreviousMatchButton");
        previousButton.setPreferredSize(new Dimension(50, 25));
        previousButton.addActionListener(e -> selectPrevious());
        JButton nextButton = new JButton();
        try {
            File nextImageFile = new File("src/main/resources/nexticon16x16.gif");
            if (nextImageFile.exists()) {
                Image nextImage = ImageIO.read(nextImageFile);
                ImageIcon nextIcon = new ImageIcon(nextImage);
                nextButton.setIcon(nextIcon);
            } else {
                nextButton.setText(">");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        nextButton.setName("NextMatchButton");
        nextButton.setPreferredSize(new Dimension(50, 25));
        nextButton.addActionListener(e -> selectNext());
        useRegEx.setName("UseRegExCheckbox");
        panel2.add(saveButton);
        panel2.add(openButton);
        panel2.add(searchField);
        panel2.add(searchButton);
        panel2.add(previousButton);
        panel2.add(nextButton);
        panel2.add(useRegEx);
        add(panel2, BorderLayout.CENTER);

        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        panel3.setBorder(new EmptyBorder(5, 10, 10, 10));
        textArea = new JTextArea();
        textArea.setName("TextArea");
        textArea.setColumns(10);
        textArea.setRows(25);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel3.add(scrollPane);
        add(panel3, BorderLayout.SOUTH);
    }

    private void readFromFile() {
        filename = fileChoose();
        if (filename == null) {
            return;
        }
        setTitle("Text Editor :: " + filename);
        try (FileReader fr = new FileReader(filename)) {
            BufferedReader br = new BufferedReader(fr);
            textArea.read(br, null);
            textArea.requestFocus();
        } catch (IOException e) {
            textArea.setText("");
            e.printStackTrace();
        }
    }

    private void writeToFile() {
        filename = fileChoose();
        if (filename == null) {
            return;
        }
        try (FileWriter fw = new FileWriter(filename)) {
            BufferedWriter bw = new BufferedWriter(fw);
            textArea.write(bw);
            textArea.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void search() {
        currentMatch = 0;
        matchs.clear();
        String sub = searchField.getText();
        String text = textArea.getText();

        int start = 0, end = 0;
        while (start != -1) {
            if (useRegEx.isSelected()) {
                Pattern pattern = Pattern.compile(sub);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find(start)) {
                    start = matcher.start();
                    end = matcher.end();
                }
            } else {
                start = text.indexOf(sub, start);
                end = start + sub.length();
            }
            if (start != -1) {
                IntPair match = new IntPair(start, end);
                if (matchs.contains(match)) {
                    start = -1;
                } else {
                    matchs.add(match);
                    start = end;
                }
            }
        }

        selectCurrentMatch();
    }

    private void selectPrevious() {
        currentMatch--;
        if (currentMatch == -1) {
            currentMatch = matchs.size() - 1;
        }

        selectCurrentMatch();
    }

    private void selectNext() {
        currentMatch++;
        if (currentMatch == matchs.size()) {
            currentMatch = 0;
        }

        selectCurrentMatch();
    }

    private void selectCurrentMatch() {
        if (matchs.size() > 0) {
            IntPair match = matchs.get(currentMatch);
            int start = match.first;
            int end = match.second;
            textArea.setCaretPosition(end);
            textArea.select(start, end);
            textArea.grabFocus();
        }
    }

    private String fileChoose() {
        int ret = fileChooser.showDialog(null, "Open file");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return file.getAbsolutePath();
        }
        return null;
    }

    private static class IntPair {
        public int first;
        public int second;

        public IntPair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "IntPair{first = " + first + ", second = " + second + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntPair intPair = (IntPair) o;
            return first == intPair.first && second == intPair.second;
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
}