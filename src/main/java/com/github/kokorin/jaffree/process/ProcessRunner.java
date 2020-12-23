/*
 *    Copyright  2020 Alex Katlein
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

import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;
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
    
    public ProcessRunner(Path executable, SimplifiedProcessHandler<T> processHandler) {
        Objects.requireNonNull(executable, "executable must not be null");
        Objects.requireNonNull(processHandler, "processHandler must not be null");
        
        this.executable = executable;
        this.processHandler = processHandler;
    }
    
    public ProcessRunner<T> setArguments(List<String> arguments) {
        Objects.requireNonNull(arguments, "arguments must not be null");
        
        this.arguments = arguments;
        return this;
    }
    
    private List<String> getArguments() {
        return arguments;
    }
    
    private List<Runnable> getHelpers() {
        return helpers;
    }
    
    public ProcessRunner<T> setHelpers(List<Runnable> helpers) {
        Objects.requireNonNull(helpers, "helpers must not be null");
        
        this.helpers = helpers;
        return this;
    }
    
    public synchronized ProcessFuture<T> executeAsync() {
        final List<String> command = new ArrayList<>();
        command.add(executable.toString());
        command.addAll(getArguments());
        
        LOGGER.info("Command constructed:\n{}", joinStrings(command));
        
        final ProcessAccessImpl processAccess = new ProcessAccessImpl();
        
        return new ProcessFutureImpl<>(forkJoinPool.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                final List<RecursiveAction> helperTasks = new ArrayList<>();
                for (final Runnable helper : getHelpers()) {
                    RecursiveAction helperTask = new RecursiveAction() {
                        @Override
                        protected void compute() {
                            helper.run();
                        }
                    };
                    
                    helperTasks.add(helperTask);
                    helperTask.fork();
                }
                
                LOGGER.info("Starting process: {}", executable);
                
                DelegatingProcessHandler actualProcessHandler = new DelegatingProcessHandler(processHandler, processAccess);
                NuProcess process = new NuProcessBuilder(actualProcessHandler, command).start();
                
                LOGGER.info("Waiting for process to finish");
                int status = process.waitFor(0, TimeUnit.SECONDS);
                LOGGER.info("Process has finished with status: {}", status);
                
                for (RecursiveAction helper : helperTasks) {
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
