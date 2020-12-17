package com.github.kokorin.jaffree.process;

import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class ProcessRunner<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRunner.class);
    
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    
    private final Path executable;
    private final SimplifiedProcessHandler<T> processHandler;
    
    private List<String> arguments = Collections.emptyList();
    private List<Runnable> helpers = Collections.emptyList();
    
    public ProcessRunner(@NotNull Path executable, @NotNull SimplifiedProcessHandler<T> processHandler) {
        Objects.requireNonNull(executable, "executable must not be null");
        Objects.requireNonNull(processHandler, "processHandler must not be null");
        
        this.executable = executable;
        this.processHandler = processHandler;
    }
    
    @NotNull
    public ProcessRunner<T> setArguments(@NotNull List<String> arguments) {
        Objects.requireNonNull(arguments, "arguments must not be null");
        
        this.arguments = arguments;
        return this;
    }
    
    @NotNull
    private List<String> getArguments() {
        return arguments;
    }
    
    private List<Runnable> getHelpers() {
        return helpers;
    }
    
    @NotNull
    public ProcessRunner<T> setHelpers(@NotNull List<Runnable> helpers) {
        Objects.requireNonNull(helpers, "helpers must not be null");
        
        this.helpers = helpers;
        return this;
    }
    
    @NotNull
    public synchronized ProcessFuture<T> executeAsync() {
        final List<String> command = new ArrayList<>();
        command.add(executable.toString());
        command.addAll(getArguments());
        
        LOGGER.info("Command constructed:\n{}", joinStrings(command));
        
        final ProcessAccessImpl processAccess = new ProcessAccessImpl();
        
        return new ProcessFutureImpl<>(forkJoinPool.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final List<ForkJoinTask<Void>> helperTasks = new ArrayList<>();
                for (final Runnable helper : getHelpers()) {
                    helperTasks.add(new RecursiveAction() {
                        @Override
                        protected void compute() {
                            helper.run();
                        }
                    });
                }
                
                for (ForkJoinTask<Void> helperTask : helperTasks) {
                    helperTask.fork();
                }
                
                LOGGER.info("Starting process: {}", executable);
                
                DelegatingProcessHandler actualProcessHandler = new DelegatingProcessHandler(processHandler, processAccess);
                NuProcess process = new NuProcessBuilder(actualProcessHandler, command).start();
                
                LOGGER.info("Waiting for process to finish");
                int status = process.waitFor(0, TimeUnit.SECONDS);
                LOGGER.info("Process has finished with status: {}", status);
                
                for (ForkJoinTask<Void> helper : helperTasks) {
                    helper.join();
                }
                
                if (status != 0 && processHandler.getException() != null) {
                    throw new RuntimeException("Execution failed with exception", processHandler.getException());
                }
                
                if (status != 0) {
                    throw new RuntimeException("Execution finished with non-zero status: " + status);
                }
                
                return Objects.requireNonNull(processHandler.getResult(), "The result must not be null");
            }
        }), processAccess);
    }
    
    private static String joinStrings(Collection<String> strings) {
        StringBuilder result = new StringBuilder();
        
        boolean first = true;
        for (String str : strings) {
            if (first) {
                first = false;
            } else {
                result.append(" ");
            }
            
            boolean shouldQuote = str.indexOf(' ') > -1;
            if (shouldQuote) {
                result.append('\"');
            }
            
            result.append(str);
            
            if (shouldQuote) {
                result.append('\"');
            }
        }
        
        return result.toString();
    }
}
