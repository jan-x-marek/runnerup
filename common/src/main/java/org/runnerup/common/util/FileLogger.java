package org.runnerup.common.util;

import android.content.Context;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileLogger {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    private StringBuilder buffer;

    private final Context ctx;

    private final int size;

    private final String fileName;

    public FileLogger(Context ctx, String name, int size) {
        this.ctx = ctx;
        this.size = size;
        fileName = name + "_" + df.format(System.currentTimeMillis()) + ".txt";
        resetBuffer();
    }

    private void resetBuffer() {
        buffer = new StringBuilder();
    }

    private void persist() {
        try {
            write(buffer.toString());
        }
        catch (Exception e) {
            Log.e("FileLogger: ", "", e);
        }
        finally {
            resetBuffer();
        }
    }

    public synchronized void update(String s) {
        buffer.append(s);
        if (buffer.length() >= size) {
            persist();
        }
    }

    public synchronized void flush() {
        if (buffer.length() > 0) {
            persist();
        }
    }

    private void write(String s) throws IOException {

        String externalFilesDir = ctx.getExternalFilesDir(null).getAbsolutePath();
        File logDir = new File(externalFilesDir + "/gpslog/");
        logDir.mkdirs();
        File logFile = new File(logDir, fileName);

        try (FileChannel out = new FileOutputStream(logFile, logFile.length() > 0).getChannel()) {
            if (out.size() > 0) {
                out.position(out.size());
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(s.getBytes());
            out.write(byteBuffer);
        }
    }
}
