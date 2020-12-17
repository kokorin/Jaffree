package com.github.kokorin.jaffree.process;

import com.zaxxer.nuprocess.NuProcess;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProcessAccessImpl implements ProcessAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessAccessImpl.class);
    
    @Nullable
    private volatile NuProcess process;
    
    public synchronized void setProcess(@Nullable NuProcess process) {
        this.process = process;
    }
    
    @Override
    public synchronized void stopForcefully() {
        NuProcess process = this.process;
        
        if (process == null) {
            LOGGER.error("No Process set yet, can't stop");
        } else if (process.isRunning()) {
            process.destroy(true);
        }
    }
    
    @Override
    public synchronized void stopGracefully() {
        NuProcess process = this.process;
        
        if (process == null) {
            LOGGER.error("No Process set yet, can't stop");
        } else if (process.isRunning()) {
            // wantWrite will lead to onStdinReady of the DelegatingProcessHandler to be called.
            // Hopefully the implementation is sound and never calls this anywhere else.
            process.wantWrite();
        }
    }
}
