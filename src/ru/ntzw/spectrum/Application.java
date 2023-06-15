package ru.ntzw.spectrum;

import ru.ntzw.spectrum.model.*;
import ru.ntzw.spectrum.service.SpectrumLibrary;
import ru.ntzw.spectrum.service.properties.PropertiesService;
import ru.ntzw.spectrum.service.properties.SimplePropertiesService;
import ru.ntzw.spectrum.view.MainPresenter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Application implements Runnable {

    private MainPresenter mainPresenter;
    private PropertiesService propertiesService;
    private SpectrumLibrary spectrumLibrary;
    private Spectrum analyzedSpectrum;

    Application() {
        propertiesService = new SimplePropertiesService(Paths.get("settings.cfg"));
        initPropertiesService();
        spectrumLibrary = new SpectrumLibrary(
                Paths.get(propertiesService.getString("libraryPath", "library")),
                getSpectrumExtension());
        createShutdownHook();
        mainPresenter = new MainPresenter();
    }

    private void initPropertiesService() {
        SimplePropertiesService service = (SimplePropertiesService) propertiesService;
        try {
            service.init();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initSpectrumLibrary() {
        try {
            spectrumLibrary.init();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void onExit() {
        try {
            ((Disposable) propertiesService).dispose();
            spectrumLibrary.dispose();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::onExit));
    }

    @Override
    public void run() {
        mainPresenter.init();
        initSpectrumLibrary();
        mainPresenter.setLibraryListModel(spectrumLibrary);
        mainPresenter.setChooseCallback(this::onFileChoose);
        mainPresenter.setRefreshCallback(this::onRefreshLibrary);
        mainPresenter.setCorrelationsCallback(this::onCorrelationsCalc);
        mainPresenter.setCompareCallback(this::onCompare);
        mainPresenter.setNameToSpectrumCallback(spectrumLibrary::get);
        mainPresenter.setSpectrumExtensionSupplier(this::getSpectrumExtension);
        mainPresenter.setCompareContextSupplier(this::getCompareContext);
    }

    private boolean onFileChoose(File file) {
        try {
            analyzedSpectrum = Spectrum.load(file.toPath());
            mainPresenter.setCorrelationsListData(new Object[]{});
            return analyzedSpectrum.getPoints().length != 0;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean onRefreshLibrary(Void v) {
        try {
            spectrumLibrary.dispose();
            spectrumLibrary.init();
            mainPresenter.setCorrelationsListData(new Object[]{});
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean onCorrelationsCalc(Void v) {
        List<CompareEntry> compareList = spectrumLibrary.getSpectrumList().stream()
                .map(analyzedSpectrum::compare)
                .filter(p -> p.getCorrelation() * 100 >= 1)
                .sorted()
                .collect(Collectors.toList());
        int length = propertiesService.getInteger("correlationsListSize", -1);
        length = Math.min(length < 1 ? compareList.size() : length, compareList.size());
        CompareEntry[] result = new CompareEntry[length];
        for(int i = 0; i < length; i++) {
            result[i] = compareList.get(i);
        }
        mainPresenter.setCorrelationsListData(result);
        return true;
    }

    private CompareResult onCompare(Spectrum fromLibrary) {
        if(analyzedSpectrum == null)
            return null;
        return new CompareResult(analyzedSpectrum, fromLibrary, analyzedSpectrum.compare(fromLibrary).getCorrelation());
    }

    private String getSpectrumExtension() {
        return propertiesService.getString("spectrumFilesPostfix", ".csv");
    }

    private CompareContext getCompareContext() {
        return new CompareContext(new CompareColorPalette(
                propertiesService.getColor("compareBackgroundColor", Color.white),
                propertiesService.getColor("compareGridColor", Color.lightGray),
                propertiesService.getColor("compareAxesColor", Color.black),
                propertiesService.getColor("compareSpectrum1Color", Color.red),
                propertiesService.getColor("compareSpectrum2Color", Color.blue),
                propertiesService.getColor("compareFontColor", Color.black)
        ),
                propertiesService.getDouble("compareGridStepX", 200.),
                propertiesService.getDouble("compareGridStepY", 2000.)
        );
    }
}
