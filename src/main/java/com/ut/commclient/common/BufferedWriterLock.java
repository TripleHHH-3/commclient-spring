package com.ut.commclient.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
public class BufferedWriterLock extends BufferedWriter {
    public BufferedWriterLock(Writer out) {
        super(out);
    }

    public synchronized void writeFlush(String str) throws IOException {
        super.write(str);
        super.flush();
    }
}
