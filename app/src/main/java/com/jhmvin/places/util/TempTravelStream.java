package com.jhmvin.places.util;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class TempTravelStream {

    public final static String START_FILE = "<[start_file]>";
    public final static String START = "<[start]>";
    public final static String DATA = "<[data]>";
    public final static String END = "<[end]>";
    public final static String END_FILE = "<[end_file]>";

    private final Context mContext;
    private Writer mFileWriter;

    public TempTravelStream(Context context) {
        this.mContext = context;
    }

    public void start(String from, String to, long startMills) throws IOException {
        File tempStreamDir = TempTravelStream.getTempTravelStreamDirectory(this.mContext);
        File newStream = new File(tempStreamDir, String.valueOf(startMills) + ".placestravelstream");
        mFileWriter = new BufferedWriter(new FileWriter(newStream));
        mFileWriter.write(START_FILE + "\n");
        mFileWriter.write(START + "\n");
        mFileWriter.write(from + "\n");
        mFileWriter.write(to + "\n");
        mFileWriter.write(String.valueOf(startMills));
        mFileWriter.flush();
    }


    public void close() {
        if (mFileWriter != null) {
            try {
                mFileWriter.close();
            } catch (IOException e) {
                // ignore
            }
            mFileWriter = null;
        }
    }

    /**
     * Get the temporary travel stream directory.
     *
     * @param context
     * @return
     */
    public final static File getTempTravelStreamDirectory(Context context) {
        return context.getDir("travelstream", Context.MODE_PRIVATE);
    }


}
