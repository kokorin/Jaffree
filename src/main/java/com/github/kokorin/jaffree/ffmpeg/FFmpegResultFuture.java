/*
 *    Copyright  2020 Denis Kokorin
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

package com.github.kokorin.jaffree.ffmpeg;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FFmpegResultFuture {
    private final Future<FFmpegResult> resultFuture;
    private final Runnable stopper;

    public FFmpegResultFuture(Future<FFmpegResult> resultFuture, Runnable stopper) {
        this.resultFuture = resultFuture;
        this.stopper = stopper;
    }

    public void forceStop() {
        stop(true);
    }

    public void graceStop() {
        stop(false);
    }

    public void stop(boolean forceStop) {
        if (forceStop) {
            resultFuture.cancel(true);
        }

        stopper.run();
    }

    @Deprecated
    public boolean cancel(boolean forceStop) {
        stop(forceStop);
        return true;
    }

    public FFmpegResult get() throws InterruptedException, ExecutionException {
        return resultFuture.get();
    }

    public FFmpegResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return resultFuture.get(timeout, unit);
    }

    public boolean isCancelled() {
        return resultFuture.isCancelled();
    }

    public boolean isDone() {
        return resultFuture.isDone();
    }
}
