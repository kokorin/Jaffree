/*
 *    Copyright  2018 Denis Kokorin
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.process;

import com.github.kokorin.jaffree.JaffreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Executor {
    private final Thread starter;
    private final String contextName;

    private final List<Exception> exceptions = new CopyOnWriteArrayList<>();
    private final List<Thread> threads = new CopyOnWriteArrayList<>();
    private final AtomicInteger runningCounter = new AtomicInteger();
    private final AtomicBoolean starterInterrupted = new AtomicBoolean();
    private volatile boolean stopped = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    public Executor(String contextName) {
        this.starter = Thread.currentThread();
        this.contextName = contextName;
    }

    /**
     * Executes provided Runnable.
     * <p>
     * <b>Note</b>: interrupts invoking thread if exception appears
     *
     * @param name     thread name suffix
     * @param runnable runnable to execute
     */
    public void execute(final String name, final Runnable runnable) {
        if (stopped) {
            throw new JaffreeException("Executor has been stopped already!");
        }

        final Thread starter = Thread.currentThread();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("{} thread has started", name);
                runningCounter.incrementAndGet();
                try {
                    runnable.run();
                } catch (Exception e) {
                    LOGGER.debug("Exception in thread {}, collecting for later report. Message: {}", name, e.getMessage());
                    exceptions.add(e);

                    // Starter thread MUST NOT be interrupted multiple times,
                    // otherwise main thread may be marked for interruption after exiting ProcessHandler logic.
                    if (!stopped && starterInterrupted.compareAndSet(false, true)) {
                        LOGGER.warn("Interrupting starter thread ({}) because of exception: {}", starter.getName(), e.getMessage());
                        starter.interrupt();
                    }
                } finally {
                    LOGGER.debug("{} thread has finished", name);
                    runningCounter.decrementAndGet();
                }
            }
        }, getThreadName(name));
        thread.setDaemon(true);
        thread.start();

        threads.add(thread);
    }

    public Exception getException() {
        if (exceptions.isEmpty()) {
            return null;
        }

        Exception result = new JaffreeException("Exception during execution", exceptions.get(0));
        for (int i = 1; i < exceptions.size(); i++) {
            result.addSuppressed(exceptions.get(i));
        }

        return result;
    }

    public void stop() {
        stopped = true;
        LOGGER.debug("Stopping execution");
        for (Thread thread : threads) {
            if (thread.isAlive() && !thread.isInterrupted()) {
                LOGGER.warn("Interrupting ALIVE thread: {}", thread.getName());
                thread.interrupt();
            }
        }
    }

    public boolean isRunning() {
        return runningCounter.get() > 0;
    }

    public List<String> getRunningThreadNames() {
        List<String> result = new ArrayList<>();
        for (Thread thread : threads) {
            if (thread.isAlive() && !thread.isInterrupted()) {
                result.add(thread.getName());
            }
        }

        return result;
    }

    private String getThreadName(String name) {
        if (contextName == null) {
            return name;
        }

        return contextName + "-" + name;
    }
}
