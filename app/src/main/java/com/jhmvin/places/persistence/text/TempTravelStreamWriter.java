package com.jhmvin.places.persistence.text;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A Writer that writes the location to the internal storage,
 * while the location is being recorded.
 * <p>
 * This will make use of a buffered writer.
 */
public class TempTravelStreamWriter implements TextWriter {

    public final static String DIR_TEMP_TRAVEL_STREAMS = "temp_travel_streams";

    private final File tempTravelStream;
    private BufferedWriter writer;


    /**
     * Constructs a travel stream writer logging the current running location service.
     *
     * @param context  activity or service context where this will be executed.
     * @param fileName give a simple file name without extension,
     *                 this constructor will automatically append .temp to the file.
     *                 Marking it as incomplete.
     */
    public TempTravelStreamWriter(Context context, String fileName) {
        File tempDirectory = context.getDir(DIR_TEMP_TRAVEL_STREAMS, Context.MODE_PRIVATE);
        this.tempTravelStream = new File(tempDirectory, fileName + ".temp");
    }

    @Override
    public void open() throws IOException {
        this.writer = new BufferedWriter(new FileWriter(this.tempTravelStream));
    }

    @Override
    public void write(String text) throws IOException {
        this.writer.write(text);
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }


}
