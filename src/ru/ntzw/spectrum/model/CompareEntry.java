package ru.ntzw.spectrum.model;

public class CompareEntry implements Comparable<CompareEntry> {

    private final String name;
    private final String comparedWithName;
    private final float correlation;

    public CompareEntry(String name, String comparedWithName, float correlation) {
        this.name = name;
        this.comparedWithName = comparedWithName;
        this.correlation = correlation;
    }

    public String getName() {
        return name;
    }

    public String getComparedWithName() {
        return comparedWithName;
    }

    public float getCorrelation() {
        return correlation;
    }

    @Override
    public String toString() {
        return String.format("%3.0f%%", Math.max(correlation, 0) * 100) + " - " + comparedWithName;
    }

    @Override
    public int compareTo(CompareEntry o) {
        return (int) Math.signum(o.correlation - correlation);
    }
}
