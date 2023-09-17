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

package com.github.kokorin.jaffree.ffmpeg.output;

import com.github.kokorin.jaffree.net.NegotiatingTcpServer;
import com.github.kokorin.jaffree.net.TcpNegotiator;
import com.github.kokorin.jaffree.net.TcpServer;
import com.github.kokorin.jaffree.process.ProcessHelper;

/**
 * {@link TcpOutput} allows to consume ffmpeg output via local TCP socket on loopback address.
 * <p>
 * <b>Note</b> there are limitation because of non-seekable nature of TCP output.
 *
 * @param <T> self
 */
public abstract class TcpOutput<T extends TcpOutput<T>> extends BaseOutput<T> implements Output {
    private final TcpServer tcpServer;

    /**
     * Creates {@link TcpOutput}.
     *
     * @param tcpNegotiator tcp negotiator
     */
    public TcpOutput(final TcpNegotiator tcpNegotiator) {
        this("tcp", tcpNegotiator);
    }

    /**
     * Creates {@link TcpOutput}.
     *
     * @param protocol      protocol
     * @param tcpNegotiator tcp negotiator
     */
    public TcpOutput(final String protocol, final TcpNegotiator tcpNegotiator) {
        this(protocol, "", tcpNegotiator);
    }

    /**
     * Creates {@link TcpOutput}.
     *
     * @param protocol      protocol
     * @param suffix        url suffix
     * @param tcpNegotiator tcp negotiator
     */
    public TcpOutput(final String protocol, final String suffix,
                     final TcpNegotiator tcpNegotiator) {
        this(protocol, suffix, NegotiatingTcpServer.onRandomPort(tcpNegotiator));
    }

    protected TcpOutput(final String protocol, final String suffix, final TcpServer tcpServer) {
        super(protocol + "://" + tcpServer.getAddressAndPort() + suffix);
        this.tcpServer = tcpServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ProcessHelper helperThread() {
        return tcpServer;
    }
}
