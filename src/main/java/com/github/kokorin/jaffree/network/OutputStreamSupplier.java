package com.github.kokorin.jaffree.network;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamSupplier {
    void supply(OutputStream outputStream) throws IOException;
}
