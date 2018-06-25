package com.github.kokorin.jaffree.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Executor<T> {
    private final String contextName;
    private final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
    private final AtomicLong workingThreadCount = new AtomicLong();
    private final List<Thread> threads = new CopyOnWriteArrayList<>();
    private volatile boolean stopped = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    public Executor(String contextName) {
        this.contextName = contextName;
    }

    public void execute(final String name, final Runnable runnable) {
        if (stopped) {
            throw new RuntimeException("Executor has been stopped already!");
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("StdErr thread has started");
                try {
                    runnable.run();
                } catch (Exception e) {
                    boolean set = exceptionRef.compareAndSet(null, e);
                    LOGGER.warn("Failed to process {}, will {}rethrow", name, set ? "" : "NOT ", e);
                } finally {
                    workingThreadCount.decrementAndGet();
                    LOGGER.debug("StdErr thread has finished");
                }
            }
        }, getThreadName(name));
        thread.setDaemon(true);
        threads.add(thread);

        workingThreadCount.incrementAndGet();
        thread.start();
    }

    public boolean isRunning() {
        return !stopped && workingThreadCount.get() > 0;
    }

    public Exception getFirstException() {
        return exceptionRef.get();
    }

    public void stop() {
        stopped = true;
        LOGGER.debug("Interrupting existing threads");
        for (Thread thread : threads) {
            if (thread.isAlive() && !thread.isInterrupted()) {
                LOGGER.warn("Interrupting ALIVE thread: {}", thread.getName());
                thread.interrupt();
            }
        }
    }

    private String getThreadName(String name) {
        if (contextName == null) {
            return name;
        }

        return contextName + "-" + name;
    }
}
