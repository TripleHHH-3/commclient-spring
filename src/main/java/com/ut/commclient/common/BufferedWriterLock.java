package com.ut.commclient.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class BufferedWriterLock extends BufferedWriter {
    public BufferedWriterLock(Writer out) {
        super(out);
    }

    public synchronized void writeFlush(String str) throws IOException {
        super.write(str);
        super.flush();
    }
}
