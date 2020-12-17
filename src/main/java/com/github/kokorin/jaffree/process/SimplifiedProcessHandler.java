package com.github.kokorin.jaffree.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public interface SimplifiedProcessHandler<R> {
    void onStderr(@NotNull ByteBuffer buffer, boolean closed);
    
    void onStdout(@NotNull ByteBuffer buffer, boolean closed);
    
    void onExit();
    
    @NotNull
    R getResult();
    
    @Nullable
    Exception getException();
}
