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

package com.github.kokorin.jaffree.ffprobe.input;

import com.github.kokorin.jaffree.net.NegotiatingTcpServer;
import com.github.kokorin.jaffree.net.TcpNegotiator;
import com.github.kokorin.jaffree.net.TcpServer;
import com.github.kokorin.jaffree.process.ProcessHelper;

/**
 * Base implementation of {@link Input} for all TCP-based inputs.
 */
public abstract class TcpInput implements Input {
    private final String url;
    private final TcpServer tcpServer;

    /**
     * Creates {@link TcpInput}.
     *
     * @param negotiator tcp negotiator
     */
    public TcpInput(final TcpNegotiator negotiator) {
        this("tcp", negotiator);
    }

    /**
     * Creates {@link TcpInput}.
     *
     * @param protocol   protocol
     * @param negotiator tcp negotiator
     */
    public TcpInput(final String protocol, final TcpNegotiator negotiator) {
        this(protocol, "", negotiator);
    }

    /**
     * Creates {@link TcpInput}.
     *
     * @param protocol   protocol
     * @param suffix     url suffix
     * @param negotiator tcp negotiator
     */
    public TcpInput(final String protocol, final String suffix, final TcpNegotiator negotiator) {
        this(protocol, suffix, NegotiatingTcpServer.onRandomPort(negotiator));
    }

    protected TcpInput(final String protocol, final String suffix, final TcpServer tcpServer) {
        this.url = protocol + "://" + tcpServer.getAddressAndPort() + suffix;
        this.tcpServer = tcpServer;
    }

    @Override
    public final String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessHelper helperThread() {
        return tcpServer;
    }
}
