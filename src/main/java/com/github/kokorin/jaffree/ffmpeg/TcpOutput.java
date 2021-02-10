/*
 *    Copyright 2019-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.net.NegotiatingTcpServer;
import com.github.kokorin.jaffree.net.TcpNegotiator;
import com.github.kokorin.jaffree.net.TcpServer;
import com.github.kokorin.jaffree.process.ProcessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides possibility to consume ffmpeg output via TCP socket.
 * <p>
 * <b>Note</b> there are limitation because of non-seekable nature of TCP output.
 *
 * @param <T>
 */
public abstract class TcpOutput<T extends TcpOutput<T>> extends BaseOutput<T> implements Output {
    private final TcpServer tcpServer;

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpOutput.class);

    public TcpOutput(TcpNegotiator tcpNegotiator) {
        this("tcp", tcpNegotiator);
    }

    public TcpOutput(String protocol, TcpNegotiator tcpNegotiator) {
        this(protocol, "", tcpNegotiator);
    }

    public TcpOutput(String protocol, String suffix, TcpNegotiator tcpNegotiator) {
        this(protocol, suffix, NegotiatingTcpServer.onRandomPort(tcpNegotiator));
    }

    public TcpOutput(String protocol, String suffix, TcpServer tcpServer) {
        this.tcpServer = tcpServer;
        super.setOutput(protocol + "://" + tcpServer.getAddressAndPort() + suffix);
    }

    @Override
    public final ProcessHelper helperThread() {
        return tcpServer;
    }

    @Override
    public T setOutput(String output) {
        throw new RuntimeException("TcpOutput output can't be changed");
    }
}
