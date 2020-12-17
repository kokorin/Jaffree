package com.github.kokorin.jaffree.process;

import com.zaxxer.nuprocess.NuProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

class DelegatingProcessHandler extends DefaultProcessHandler {
    private final SimplifiedProcessHandler<?> delegate;
    private final ProcessAccessImpl processAccess;
    
    public DelegatingProcessHandler(@NotNull SimplifiedProcessHandler<?> delegate, @Nullable ProcessAccessImpl processAccess) {
        super();
        
        this.delegate = delegate;
        this.processAccess = processAccess;
    }
    
    @Override
    public void onStart(NuProcess nuProcess) {
        if (processAccess != null) {
            processAccess.setProcess(nuProcess);
        }
    }
    
    @Override
    public void onExit(int i) {
        if (processAccess != null) {
            processAccess.setProcess(null);
        }
        
        delegate.onExit();
    }
    
    @Override
    public void onStdout(ByteBuffer byteBuffer, boolean b) {
        delegate.onStdout(byteBuffer, b);
    }
    
    @Override
    public void onStderr(ByteBuffer byteBuffer, boolean b) {
        delegate.onStderr(byteBuffer, b);
    }
    
    @Override
    public boolean onStdinReady(ByteBuffer byteBuffer) {
        // This should only be called after wantWrite was called in ProcessAccessImpl.
        // It sends the quit signal to FFmpeg to shut down gracefully
        byteBuffer.put((byte) 'q');
        byteBuffer.flip();
        
        // Nothing more to write
        return false;
    }
}
