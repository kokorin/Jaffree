package com.github.kokorin.jaffree.process;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class ProcessFutureImpl<V> implements ProcessFuture<V> {
    private final Future<V> delegate;
    private final ProcessAccess processAccess;
    
    public ProcessFutureImpl(@NotNull Future<V> delegate, @NotNull ProcessAccess processAccess) {
        Objects.requireNonNull(delegate, "delegate must not be null");
        
        this.delegate = delegate;
        this.processAccess = processAccess;
    }
    
    @Override
    @NotNull
    public ProcessAccess getProcessAccess() {
        return processAccess;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning) {
            getProcessAccess().stopForcefully();
        } else {
            getProcessAccess().stopGracefully();
        }
        
        return delegate.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return delegate.isDone();
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }
    
    @Override
    public V get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
