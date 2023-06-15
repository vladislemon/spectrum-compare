package ru.ntzw.spectrum.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Spectrum {

    private final String name;
    private Point[] points;

    public Spectrum(String name, Point... points) {
        this.name = name;
        this.points = points;
        setPoints(points);
    }

    public Point[] getPoints() {
        return points;
    }

    private void setPoints(Point[] points) {
        this.points = points;
        Arrays.sort(points);
    }

    public Point getFirst() {
        return points[0];
    }

    public Point getLast() {
        return points[points.length - 1];
    }

    public Spectrum newRange(Spectrum spectrum) {
        ArrayList<Point> result = new ArrayList<>();
        int i = 0;
        for(Point p : spectrum.points) {
            for(; i < points.length && points[i].getX() < p.getX(); i++);
            if(i < points.length) {
                if(points[i].getX() == p.getX()) {
                    result.add(points[i]);
                }
                else if(i > 0) {
                    result.add(lerp(points[i-1], points[i], p));
                }
            }
        }
        return new Spectrum(name, result.toArray(new Point[]{}));
    }

    private Point lerp(Point p1, Point p2, Point o) {
        return new Point(o.getX(), p1.getY() + (p2.getY() - p1.getY()) * (o.getX() - p1.getX()) / (p2.getX() - p1.getX()));
    }

    public CompareEntry compare(Spectrum another) {
        Spectrum normalized = another.newRange(this);
        float correlation = pcc(this.points, normalized.points, 0, normalized.points.length);
        return new CompareEntry(name, another.name, correlation);
    }

    private float pcc(Point[] p1, Point[] p2, int offset, int length) {
        double p1Mean = mean(p1);
        double p2Mean = mean(p2);

        double upperSum = 0, downLeftSum = 0, downRightSum = 0;
        double diffP1, diffP2;

        for(int i = offset; i < offset + length; i++) {
            diffP1 = p1[i].getY() - p1Mean;
            diffP2 = p2[i].getY() - p2Mean;
            upperSum += diffP1 * diffP2;
            downLeftSum += diffP1 * diffP1;
            downRightSum += diffP2 * diffP2;
        }

        float result = (float) (upperSum / Math.sqrt(downLeftSum) / Math.sqrt(downRightSum));
        if(Float.isNaN(result))
            return 0;
        return result;
    }

    private float mean(Point[] points) {
        float mean = 0;
        for(int i = 0; i < points.length; i++) {
            mean += ((points[i].getY()) - mean) / (i + 1);
        }
        return mean;
    }

    public String getName() {
        return name;
    }

    public static Spectrum load(Path path) throws IOException {
        ArrayList<Point> points = new ArrayList<>();
        try(BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] xy = line.split(",");
                if(xy.length > 1) {
                    try {
                        double x = Double.parseDouble(xy[0].trim());
                        double y = Double.parseDouble(xy[1].trim());
                        points.add(new Point(x, y));
                    } catch(NumberFormatException e) {
                        //
                    }
                }
            }
        }
        return new Spectrum(getSpectrumName(path), points.toArray(new Point[]{}));
    }

    private static String getSpectrumName(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Spectrum spectrum = (Spectrum) o;
        return Objects.equals(name, spectrum.name) &&
                Arrays.equals(points, spectrum.points);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(points);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
