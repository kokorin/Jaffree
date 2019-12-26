package com.github.kokorin.jaffree.process;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncProcess<T> implements Future<T> {
    private Process process;
    private Future<T> result;

    public AsyncProcess(Process process, Future<T> result) {
        this.process = process;
        this.result = result;
    }

    public Process getProcess() {
        return process;
    }

    public Future<T> getResult() {
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return result.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return result.isCancelled();
    }

    @Override
    public boolean isDone() {
        return result.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return result.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return result.get(timeout, unit);
    }
}
