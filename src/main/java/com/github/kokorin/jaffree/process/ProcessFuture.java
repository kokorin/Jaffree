package com.github.kokorin.jaffree.process;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Future;

public interface ProcessFuture<V> extends Future<V> {
    @NotNull
    ProcessAccess getProcessAccess();
}
