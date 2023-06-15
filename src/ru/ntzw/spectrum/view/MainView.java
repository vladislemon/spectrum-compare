package ru.ntzw.spectrum.view;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainView extends JFrame {

    private JList libraryList;
    private JButton refreshButton;
    private JSplitPane splitPane;
    private JButton chooseButton;
    private JList correlationsList;
    private JButton correlationsButton;
    private JButton compareButton;
    private JPanel spectrumPanel;

    MainView() {
        super("Full Spectrum");
        setContentPane(splitPane);
        setSize(680, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                System.exit(0);
            }
        });
        setVisible(true);
    }

    JSplitPane getSplitPane() {
        return splitPane;
    }

    JList getLibraryList() {
        return libraryList;
    }

    JButton getRefreshButton() {
        return refreshButton;
    }

    JButton getChooseButton() {
        return chooseButton;
    }

    JList getCorrelationsList() {
        return correlationsList;
    }

    JButton getCorrelationsButton() {
        return correlationsButton;
    }

    JButton getCompareButton() {
        return compareButton;
    }

    JPanel getSpectrumPanel() {
        return spectrumPanel;
    }
}
