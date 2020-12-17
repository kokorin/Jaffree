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
import com.zaxxer.nuprocess.NuProcessHandler;

import java.nio.ByteBuffer;

public class DefaultProcessHandler implements NuProcessHandler {
    @Override
    public void onPreStart(NuProcess nuProcess) {
    }
    
    @Override
    public void onStart(NuProcess nuProcess) {
    }
    
    @Override
    public void onExit(int i) {
    }
    
    @Override
    public void onStdout(ByteBuffer byteBuffer, boolean b) {
    }
    
    @Override
    public void onStderr(ByteBuffer byteBuffer, boolean b) {
    }
    
    @Override
    public boolean onStdinReady(ByteBuffer byteBuffer) {
        return false;
    }
}
