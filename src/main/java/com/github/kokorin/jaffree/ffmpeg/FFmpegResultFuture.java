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

import com.github.kokorin.jaffree.process.Stopper;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FFmpegResultFuture {
    private final Future<FFmpegResult> resultFuture;
    private final Stopper stopper;

    public FFmpegResultFuture(Future<FFmpegResult> resultFuture, Stopper stopper) {
        this.resultFuture = resultFuture;
        this.stopper = stopper;
    }

    /**
     * Immediately stops ffmpeg process.
     *
     * <b>Note</b output media may be corrupted.
     */
    public void forceStop() {
        stopper.forceStop();
    }

    /**
     * Requests ffmpeg process to stop.
     *
     * <b>Note</b> output media finalization may take some time - up to several seconds.
     */
    public void graceStop() {
        stopper.graceStop();
    }

    /**
     * Stops ffmpeg process.
     *
     * @param forcefully true to stop immediately
     */
    public void stop(boolean forcefully) {
        if (forcefully) {
            forceStop();
        }

        graceStop();
    }

    /**
     * @param forceStop true to stop immediately
     * @return true
     * @deprecated This method is left for backward compatibility. May be removed.
     * Use {@link FFmpegResultFuture#stop(boolean)},
     * {@link FFmpegResultFuture#forceStop()} or {@link FFmpegResultFuture#graceStop()}
     */
    @Deprecated
    public boolean cancel(boolean forceStop) {
        stop(forceStop);
        return true;
    }

    /**
     * Waits if necessary for ffmpeg process to complete, and then
     * retrieves its result.
     *
     * @return ffmpeg result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     */
    public FFmpegResult get() throws InterruptedException, ExecutionException {
        return resultFuture.get();
    }

    /**
     * Waits if necessary for at most the given time for ffmpeg process
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    public FFmpegResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return resultFuture.get(timeout, unit);
    }

    // TODO check if required or replace with more suitable method
    public boolean isCancelled() {
        return resultFuture.isCancelled();
    }

    // TODO check if required or replace with more suitable method
    public boolean isDone() {
        return resultFuture.isDone();
    }
}
