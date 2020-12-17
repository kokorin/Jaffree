package com.github.kokorin.jaffree.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class LinesProcessHandler<R> implements SimplifiedProcessHandler<R> {
    private static final byte CARRIAGE_RETURN = (byte) '\r';
    private static final byte NEWLINE = (byte) '\n';
    
    private volatile R result;
    private volatile Exception exception;
    
    private final List<byte[]> currentStderrBytes = new ArrayList<>();
    private final List<byte[]> currentStdoutBytes = new ArrayList<>();
    
    @Override
    public final void onStderr(@NotNull ByteBuffer buffer, boolean closed) {
        while (buffer.hasRemaining()) {
            if (addToBytes(buffer, currentStderrBytes) && currentStderrBytes.size() > 0) {
                onStderrLine(new String(mergeBytes(currentStderrBytes)));
                currentStderrBytes.clear();
            }
        }
        
        if (closed && currentStderrBytes.size() > 0) {
            onStderrLine(new String(mergeBytes(currentStderrBytes)));
        }
    }
    
    @Override
    public final void onStdout(@NotNull ByteBuffer buffer, boolean closed) {
        while (buffer.hasRemaining()) {
            if (addToBytes(buffer, currentStdoutBytes) && currentStdoutBytes.size() > 0) {
                onStdoutLine(new String(mergeBytes(currentStdoutBytes)));
                currentStdoutBytes.clear();
            }
        }
        
        if (closed && currentStdoutBytes.size() > 0) {
            onStdoutLine(new String(mergeBytes(currentStdoutBytes)));
        }
    }
    
    private static boolean addToBytes(ByteBuffer source, List<byte[]> target) {
        int newLinePos = -1;
        for (int pos = source.position(); pos < source.limit(); pos++) {
            byte byteAtPos = source.get(pos);
            if (byteAtPos == CARRIAGE_RETURN || byteAtPos == NEWLINE) {
                newLinePos = pos;
                break;
            }
        }
        
        byte[] bytes = new byte[newLinePos > -1 ? newLinePos - source.position() : source.remaining()];
        source.get(bytes);
        target.add(bytes);
        
        if (newLinePos > -1) {
            // Consume line break characters and put cursor at the start of the next line in buffer
            for (int pos = source.position(); pos < source.limit(); pos++) {
                byte byteAtPos = source.get();
                if (byteAtPos != CARRIAGE_RETURN && byteAtPos != NEWLINE) {
                    // buffer position already advanced past first character in next
                    // line so need to reset here
                    source.position(pos);
                    break;
                }
            }
        }
        
        return newLinePos > -1;
    }
    
    private static byte[] mergeBytes(List<byte[]> listOfBytes) {
        byte[] result = new byte[sumBytes(listOfBytes)];
        int bytesMerged = 0;
        for (byte[] bytes : listOfBytes) {
            System.arraycopy(bytes, 0, result, bytesMerged, bytes.length);
            bytesMerged += bytes.length;
        }
        
        return result;
    }
    
    private static int sumBytes(List<byte[]> listOfBytes) {
        int result = 0;
        for (byte[] bytes : listOfBytes) {
            result += bytes.length;
        }
        
        return result;
    }
    
    protected void setResult(@NotNull R result) {
        this.result = result;
    }
    
    @NotNull
    @Override
    public R getResult() {
        return result;
    }
    
    protected void setException(@NotNull Exception exception) {
        if (this.exception == null) {
            this.exception = exception;
        } else {
            this.exception.addSuppressed(exception);
        }
    }
    
    @Nullable
    @Override
    public Exception getException() {
        return exception;
    }
    
    public abstract void onStderrLine(@NotNull String line);
    
    public abstract void onStdoutLine(@NotNull String line);
}
