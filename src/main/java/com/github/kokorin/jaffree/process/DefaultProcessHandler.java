package com.github.kokorin.jaffree.process;

import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessHandler;

import java.nio.ByteBuffer;

public class DefaultProcessHandler implements NuProcessHandler {
    @Override
    public void onPreStart(NuProcess nuProcess) {
    }
    
    @Override
    public void onStart(NuProcess nuProcess) {
    }
    
    @Override
    public void onExit(int i) {
    }
    
    @Override
    public void onStdout(ByteBuffer byteBuffer, boolean b) {
    }
    
    @Override
    public void onStderr(ByteBuffer byteBuffer, boolean b) {
    }
    
    @Override
    public boolean onStdinReady(ByteBuffer byteBuffer) {
        return false;
    }
}
