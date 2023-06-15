package ru.ntzw.spectrum.model;

public class CompareContext {

    private final CompareColorPalette colorPalette;
    private final double gridStepX;
    private final double gridStepY;

    public CompareContext(CompareColorPalette colorPalette, double gridStepX, double gridStepY) {
        this.colorPalette = colorPalette;
        this.gridStepX = gridStepX;
        this.gridStepY = gridStepY;
    }

    public CompareColorPalette getColorPalette() {
        return colorPalette;
    }

    public double getGridStepX() {
        return gridStepX;
    }

    public double getGridStepY() {
        return gridStepY;
    }
}
