package ru.ntzw.spectrum.model;

import java.awt.*;

public class CompareColorPalette {

    private final Color backgroundColor;
    private final Color gridColor;
    private final Color axesColor;
    private final Color spectrum1Color;
    private final Color spectrum2Color;
    private final Color fontColor;

    public CompareColorPalette(Color backgroundColor, Color gridColor, Color axesColor, Color spectrum1Color, Color spectrum2Color, Color fontColor) {
        this.backgroundColor = backgroundColor;
        this.gridColor = gridColor;
        this.axesColor = axesColor;
        this.spectrum1Color = spectrum1Color;
        this.spectrum2Color = spectrum2Color;
        this.fontColor = fontColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public Color getAxesColor() {
        return axesColor;
    }

    public Color getSpectrum1Color() {
        return spectrum1Color;
    }

    public Color getSpectrum2Color() {
        return spectrum2Color;
    }

    public Color getFontColor() {
        return fontColor;
    }
}
