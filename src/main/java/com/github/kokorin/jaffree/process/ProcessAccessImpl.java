/*
 *    Copyright  2020 Denis Kokorin, Alex Katlein
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProcessAccessImpl implements ProcessAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessAccessImpl.class);
    
    private volatile NuProcess process;
    
    public synchronized void setProcess(NuProcess process) {
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
