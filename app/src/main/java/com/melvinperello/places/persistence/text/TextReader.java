package com.melvinperello.places.persistence.text;

import java.io.Closeable;
import java.io.IOException;

public interface TextReader extends Closeable {
    void open() throws IOException;

    String read() throws IOException;

}
