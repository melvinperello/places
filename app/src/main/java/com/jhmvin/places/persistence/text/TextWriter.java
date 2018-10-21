package com.jhmvin.places.persistence.text;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * An interface implementing basic text writing features.
 */
public interface TextWriter extends Closeable {
    /**
     * Open stram for this file.
     *
     */
    void open() throws IOException;

    /**
     * Write some plain text to a file.
     *
     * @param text simple text.
     */
    void write(String text) throws IOException;

    /**
     * The stream is maintained to be open,
     * flush the data to the file to clear the buffere if used.
     */
    void flush() throws IOException;
}
