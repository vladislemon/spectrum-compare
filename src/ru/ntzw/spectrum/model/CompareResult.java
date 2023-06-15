package ru.ntzw.spectrum.model;

public class CompareResult {

    private final Spectrum spectrum1;
    private final Spectrum spectrum2;
    private final float correlation;

    public CompareResult(Spectrum spectrum1, Spectrum spectrum2, float correlation) {
        this.spectrum1 = spectrum1;
        this.spectrum2 = spectrum2;
        this.correlation = correlation;
    }

    public Spectrum getSpectrum1() {
        return spectrum1;
    }

    public Spectrum getSpectrum2() {
        return spectrum2;
    }

    public float getCorrelation() {
        return correlation;
    }
}
