package ru.ntzw.spectrum.service;

import ru.ntzw.spectrum.Disposable;
import ru.ntzw.spectrum.Initializable;
import ru.ntzw.spectrum.model.Point;
import ru.ntzw.spectrum.model.Spectrum;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class SpectrumLibrary implements Initializable, Disposable, ListModel<Spectrum> {

    private final Path path;
    private final String filePostfix;
    private final ArrayList<Spectrum> spectrumList;
    private final ArrayList<ListDataListener> dataListeners;

    private static final String contentPrefix =
            ",GP550R" + System.lineSeparator() +
            "RamanShit,Intensity";

    public SpectrumLibrary(Path path, String filePostfix) {
        this.path = path;
        this.filePostfix = filePostfix;
        this.spectrumList = new ArrayList<>();
        this.dataListeners = new ArrayList<>();
    }

    public void init() throws IOException {
        loadAll();
    }

    @Override
    public void dispose() throws Exception {
        clear();
    }

    private void clear() {
        if(spectrumList.size() > 0) {
            ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, spectrumList.size() - 1);
            for(ListDataListener dataListener : dataListeners) {
                dataListener.intervalRemoved(event);
            }
            spectrumList.clear();
        }
    }

    public Spectrum get(String name) {
        for(Spectrum spectrum : spectrumList) {
            if(spectrum.getName().equals(name))
                return spectrum;
        }
        return null;
    }

    public void add(Spectrum spectrum) {
        int index = spectrumList.size();
        spectrumList.add(spectrum);
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
        for(ListDataListener dataListener : dataListeners) {
            dataListener.intervalAdded(event);
        }
    }

    public void save(String name) throws IOException {
        Spectrum spectrum = get(name);
        if(spectrum != null) {
            save(path.resolve(name + filePostfix), get(name));
        }
    }

    public void save(int index) throws IOException {
        Spectrum spectrum = spectrumList.get(index);
        save(path.resolve(spectrum.getName() + filePostfix), spectrum);
    }

    private void loadAll() throws IOException {
        Files.list(path)
                .filter(p -> Files.isRegularFile(p) && p.toString().endsWith(filePostfix))
                .forEach(p -> {
                    try {
                        add(Spectrum.load(p));
                    } catch(IOException e) {
                        System.err.println("Unable to load " + p);
                        e.printStackTrace();
                    }
                });
    }

    private void save(Path path, Spectrum spectrum) throws IOException {
        try {
            Files.createFile(path);
        } catch(FileAlreadyExistsException e) {
            //
        }
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(contentPrefix);
            writer.newLine();
            for(Point point : spectrum.getPoints()) {
                writer.write(point.getX() + "," + point.getY());
                writer.newLine();
            }
        }
    }

    public ArrayList<Spectrum> getSpectrumList() {
        return spectrumList;
    }

    @Override
    public int getSize() {
        return spectrumList.size();
    }

    @Override
    public Spectrum getElementAt(int index) {
        return spectrumList.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        dataListeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        dataListeners.remove(l);
    }
}
