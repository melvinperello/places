package com.jhmvin.places.feature.tempTravel;

import android.content.Context;

import com.jhmvin.places.persistence.text.TextReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TempTravelReader implements TextReader {

    private File mTempTravelFile;
    private BufferedReader mReader;


    /**
     * Constructs a travel stream writer logging the current running location service.
     *
     * @param context  activity or service context where this will be executed.
     * @param fileName give a simple file name without extension,
     *                 this constructor will automatically append .temp to the file.
     *                 Marking it as incomplete.
     */
    public TempTravelReader(Context context, String fileName) {
        File tempDirectory = TempTravelDirectory.getWorkingDirectory(context);
        this.mTempTravelFile = new File(tempDirectory, fileName);
    }

    public TempTravelReader(File file) {
        this.mTempTravelFile = file;
    }

    @Override
    public void open() throws IOException {
        this.mReader = new BufferedReader(new FileReader(mTempTravelFile));
    }

    @Override
    public String read() throws IOException {
        return this.mReader.readLine();
    }

    @Override
    public void close() throws IOException {
        this.mReader.close();
    }
}
