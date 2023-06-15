package ru.ntzw.spectrum.view;

import ru.ntzw.spectrum.model.CompareColorPalette;
import ru.ntzw.spectrum.model.CompareContext;
import ru.ntzw.spectrum.model.Point;
import ru.ntzw.spectrum.model.Spectrum;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ComparePanel extends JPanel {

    private final Spectrum s1, s2;
    private final CompareColorPalette colorPalette;
    private final double gridStepX;
    private final double gridStepY;
    private final double sMinX;
    private final double sMaxX;
    private final double sMinY;
    private final double sMaxY;

    private int[] s1XPoints;
    private int[] s1YPoints;
    private int[] s2XPoints;
    private int[] s2YPoints;

    private int lastWidth, lastHeight;

    private static final float FILL_RATIO = 0.9F;

    ComparePanel(Spectrum s1, Spectrum s2, CompareContext compareContext) {
        this.s1 = s1;
        this.s2 = s2;
        this.colorPalette = compareContext.getColorPalette();
        this.gridStepX = compareContext.getGridStepX();
        this.gridStepY = compareContext.getGridStepY();
        sMinX = Math.min(s1.getFirst().getX(), s2.getFirst().getX());
        sMaxX = Math.max(s1.getLast().getX(), s2.getLast().getX());
        sMinY = Math.min(
                Arrays.stream(s1.getPoints()).min((o1, o2) -> (int) Math.signum(o1.getY() - o2.getY())).get().getY(),
                Arrays.stream(s2.getPoints()).min((o1, o2) -> (int) Math.signum(o1.getY() - o2.getY())).get().getY()
        );
        sMaxY = Math.max(
                Arrays.stream(s1.getPoints()).max((o1, o2) -> (int) Math.signum(o1.getY() - o2.getY())).get().getY(),
                Arrays.stream(s2.getPoints()).max((o1, o2) -> (int) Math.signum(o1.getY() - o2.getY())).get().getY()
        );
        s1XPoints = new int[s1.getPoints().length];
        s1YPoints = new int[s1.getPoints().length];
        s2XPoints = new int[s2.getPoints().length];
        s2YPoints = new int[s2.getPoints().length];

        setBackground(colorPalette.getBackgroundColor());
    }
    
    private void buildSpectrumPoints(int[] x, int[] y, Point[] points) {
        double width = getWidth();
        double height = getHeight();
        double xInterval = sMaxX - sMinX;
        double yInterval = sMaxY - sMinY;
        for(int i = 0; i < points.length; i++) {
            Point p = points[i];
            x[i] = (int) Math.round((width*(1-FILL_RATIO)/2) + (p.getX() - sMinX) / xInterval * width * FILL_RATIO);
            y[i] = (int) Math.round(height - ((height*(1-FILL_RATIO)/2) + (p.getY() - sMinY) / yInterval * height * FILL_RATIO));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(getWidth() != lastWidth || getHeight() != lastHeight) {
            buildSpectrumPoints(s1XPoints, s1YPoints, s1.getPoints());
            buildSpectrumPoints(s2XPoints, s2YPoints, s2.getPoints());
            lastWidth = getWidth();
            lastHeight = getHeight();
        }
        
        drawGridAndXAxis(g);
        drawSpectra(g);
        drawNames(g);
    }
    
    private void drawGridAndXAxis(Graphics g) {
        //float width = getWidth() * FILL_RATIO;
        float height = getHeight() * FILL_RATIO;
        //float xBorder = (getWidth() - width) / 2;
        float yBorder = (getHeight() - height) / 2;
        double leftBorder = ((int)(sMinX / gridStepX)) * gridStepX;
        double rightBorder = ((int)(sMaxX / gridStepX) + 1) * gridStepX;
        double xInterval = rightBorder - leftBorder;
        double yInterval = sMaxY - sMinY;
        //float xStep = (float) (gridStepX / xInterval * width);
        float yStep = (float) (gridStepY / yInterval * height);
        float zeroYLevel = (float) ((1 + sMinY / yInterval) * height);
        int xAxisYLevel = (int) (zeroYLevel + yBorder);

        //g.setColor(colorPalette.getFontColor());
        //g.drawString(String.valueOf(0), 2, xAxisYLevel + 13);
        //verticals
        int textCounter = 0;
        int textCountRemainder = (int) Math.max(1, 80 / (gridStepX / xInterval * getWidth()));
        for(double x = leftBorder; x <= rightBorder; x += gridStepX) {
            int roundedX = (int) Math.round((x - leftBorder)/ xInterval * getWidth());
            g.setColor(colorPalette.getGridColor());
            g.drawLine(roundedX, 0, roundedX, getHeight());
            if(textCounter++ % textCountRemainder == 0) {
                g.setColor(colorPalette.getFontColor());
                g.drawString(String.valueOf((int)x), roundedX + 2, xAxisYLevel + 13);
            }
        }
        //if(zeroYLevel >= height) zeroYLevel -= ((zeroYLevel - height) / yStep + 1) * yStep;
        g.setColor(colorPalette.getGridColor());

        //horizontals
        for(float y = zeroYLevel + yBorder; y >= 0; y -= yStep) {
            int roundedY = Math.round(y);
            g.drawLine(0, roundedY, getWidth(), roundedY);
        }
        for(float y = zeroYLevel + yBorder + yStep; y < getHeight() - yBorder; y += yStep) {
            int roundedY = Math.round(y);
            g.drawLine(0, roundedY, getWidth(), roundedY);
        }

        g.setColor(colorPalette.getAxesColor());
        //x axis
        g.drawLine(0, xAxisYLevel, getWidth(), xAxisYLevel);
        //g.drawLine(0, xAxisYLevel+1, getWidth(), xAxisYLevel+1);
    }

    private void drawSpectra(Graphics g) {
        g.setColor(colorPalette.getSpectrum1Color());
        g.drawPolyline(s1XPoints, s1YPoints, s1.getPoints().length);

        g.setColor(colorPalette.getSpectrum2Color());
        g.drawPolyline(s2XPoints, s2YPoints, s2.getPoints().length);
    }
    
    private void drawNames(Graphics g) {
        //g.setColor(colorPalette.getBackgroundColor());
        //g.fillRect(0, 0, 200, 40);

        g.setColor(colorPalette.getSpectrum1Color());
        g.fillRect(10, 10, 10, 10);

        g.setColor(colorPalette.getSpectrum2Color());
        g.fillRect(10, 30, 10, 10);

        g.setColor(colorPalette.getFontColor());
        g.drawString(s1.getName(), 25, 20);
        g.drawString(s2.getName(), 25, 40);
    }


}
