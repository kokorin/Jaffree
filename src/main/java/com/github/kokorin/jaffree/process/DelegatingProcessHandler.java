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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

class DelegatingProcessHandler extends DefaultProcessHandler {
    private final SimplifiedProcessHandler<?> delegate;
    private final ProcessAccessImpl processAccess;
    
    public DelegatingProcessHandler(@NotNull SimplifiedProcessHandler<?> delegate, @Nullable ProcessAccessImpl processAccess) {
        super();
        
        this.delegate = delegate;
        this.processAccess = processAccess;
    }
    
    @Override
    public void onStart(NuProcess nuProcess) {
        if (processAccess != null) {
            processAccess.setProcess(nuProcess);
        }
    }
    
    @Override
    public void onExit(int i) {
        if (processAccess != null) {
            processAccess.setProcess(null);
        }
        
        delegate.onExit();
    }
    
    @Override
    public void onStdout(ByteBuffer byteBuffer, boolean b) {
        delegate.onStdout(byteBuffer, b);
    }
    
    @Override
    public void onStderr(ByteBuffer byteBuffer, boolean b) {
        delegate.onStderr(byteBuffer, b);
    }
    
    @Override
    public boolean onStdinReady(ByteBuffer byteBuffer) {
        // This should only be called after wantWrite was called in ProcessAccessImpl.
        // It sends the quit signal to FFmpeg to shut down gracefully
        byteBuffer.put((byte) 'q');
        byteBuffer.flip();
        
        // Nothing more to write
        return false;
    }
}
