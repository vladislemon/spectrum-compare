package ru.ntzw.spectrum.view;

import ru.ntzw.spectrum.Disposable;
import ru.ntzw.spectrum.Initializable;
import ru.ntzw.spectrum.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class MainPresenter implements Initializable, Disposable {

    private MainView mainView;
    private Function<File, Boolean> chooseCallback;
    private Function<Void, Boolean> refreshCallback;
    private Function<Void, Boolean> correlationsCallback;
    private Function<Spectrum, CompareResult> compareCallback;
    private Function<String, Spectrum> nameToSpectrumCallback;
    private Supplier<String> spectrumExtensionSupplier;
    private Supplier<CompareContext> compareContextSupplier;

    private File lastChooseLocation = new File(".");

    public void init() {
        mainView = new MainView();
        initListeners();
    }

    public void setChooseCallback(Function<File, Boolean> chooseCallback) {
        this.chooseCallback = chooseCallback;
    }

    public void setRefreshCallback(Function<Void, Boolean> refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public void setCorrelationsCallback(Function<Void, Boolean> correlationsCallback) {
        this.correlationsCallback = correlationsCallback;
    }

    public void setCompareCallback(Function<Spectrum, CompareResult> compareCallback) {
        this.compareCallback = compareCallback;
    }

    public void setNameToSpectrumCallback(Function<String, Spectrum> nameToSpectrumCallback) {
        this.nameToSpectrumCallback = nameToSpectrumCallback;
    }

    public void setSpectrumExtensionSupplier(Supplier<String> spectrumExtensionSupplier) {
        this.spectrumExtensionSupplier = spectrumExtensionSupplier;
    }

    public void setCompareContextSupplier(Supplier<CompareContext> compareContextSupplier) {
        this.compareContextSupplier = compareContextSupplier;
    }

    private void initListeners() {
        mainView.getRefreshButton().addActionListener(this::onRefreshButton);
        mainView.getChooseButton().addActionListener(this::onChooseButton);
        mainView.getCorrelationsButton().addActionListener(this::onCorrelationsButton);
        mainView.getCompareButton().addActionListener(this::onCompareButton);
    }

    private void onRefreshButton(ActionEvent event) {
        if(!refreshCallback.apply(null)) {
            JOptionPane.showMessageDialog(mainView, "Unable to reload library", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onChooseButton(ActionEvent event) {
        String extension = spectrumExtensionSupplier.get().substring(1);
        JDialog dialog = new JDialog(mainView, String.format("Choose spectrum file ( %s )", extension), true);
        JFileChooser fileChooser = new JFileChooser(lastChooseLocation);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Spectrum file", extension));
        fileChooser.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File file = fileChooser.getSelectedFile();
                    if(chooseCallback.apply(file)) {
                        setBorderTitle((TitledBorder) mainView.getSpectrumPanel().getBorder(), mainView.getSpectrumPanel(), file.getPath());
                        lastChooseLocation = file.getParentFile();
                        dialog.dispose();
                    }
                    else
                        JOptionPane.showMessageDialog(mainView, "Invalid or empty file", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else if(e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                    dialog.dispose();
                }
            }
        });
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.add(fileChooser);
        dialog.pack();
        dialog.setLocationRelativeTo(mainView);
        dialog.setVisible(true);
    }

    private void onCorrelationsButton(ActionEvent event) {
        if(((TitledBorder)mainView.getSpectrumPanel().getBorder()).getTitle().equals("No spectrum")) {
            JOptionPane.showMessageDialog(mainView, "Select file first!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(!correlationsCallback.apply(null)) {
            JOptionPane.showMessageDialog(mainView, "Error occurred while calculating PCC", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCompareButton(ActionEvent event) {
        List<Spectrum> selected = new ArrayList<>();
        List<CompareEntry> selectedFromCorrelations = mainView.getCorrelationsList().getSelectedValuesList();
        if(!selectedFromCorrelations.isEmpty()) {
            for(CompareEntry entry : selectedFromCorrelations) {
                Spectrum spectrum = nameToSpectrumCallback.apply(entry.getComparedWithName());
                if(spectrum != null)
                    selected.add(spectrum);
            }
        }
        if(selected.isEmpty()) {
            selected = mainView.getLibraryList().getSelectedValuesList();
        }
        if(selected.isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Select items for compare", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Component relativeTo = mainView;
        for(Spectrum spectrum : selected) {
            CompareResult compareResult = compareCallback.apply(spectrum);
            if(compareResult == null) {
                JOptionPane.showMessageDialog(mainView, "Select file first!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            relativeTo = new CompareView(relativeTo, compareResult, compareContextSupplier.get());
        }
    }

    private void setBorderTitle(TitledBorder border, JComponent owner, String raw) {
        String title = raw;
        int size = (int) (owner.getWidth() / border.getTitleFont().getSize2D() * 1.5F);
        if(raw.length() > size) {
            title = "..." + raw.substring(raw.indexOf(File.separator, raw.length() - size + 3));
        }
        owner.setBorder(BorderFactory.createTitledBorder(border, title));
    }

    public void setLibraryListModel(ListModel<?> model) {
        mainView.getLibraryList().setModel(model);
    }

    public void setCorrelationsListData(Object[] data) {
        mainView.getCorrelationsList().setListData(data);
    }

    @Override
    public void dispose() throws Exception {

    }
}
