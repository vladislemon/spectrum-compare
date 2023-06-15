package ru.ntzw.spectrum.view;

import ru.ntzw.spectrum.model.CompareContext;
import ru.ntzw.spectrum.model.CompareResult;

import javax.swing.*;
import java.awt.*;

class CompareView extends JFrame {

    CompareView(Component relativeTo, CompareResult compareResult, CompareContext compareContext) {
        super("\"" + compareResult.getSpectrum1().getName() + "\" ↔ \"" + compareResult.getSpectrum2().getName() +
                "\" | PCC = " + String.format("%1.2f", compareResult.getCorrelation()));
        setSize(740, 420);
        if(relativeTo == null) {
            setLocationRelativeTo(null);
        } else {
            Point location = relativeTo.getLocation();
            location.translate(10, 10);
            setLocation(location);
        }
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ComparePanel comparePanel = new ComparePanel(compareResult.getSpectrum1(), compareResult.getSpectrum2(), compareContext);
        setContentPane(comparePanel);
        setVisible(true);
    }
}
