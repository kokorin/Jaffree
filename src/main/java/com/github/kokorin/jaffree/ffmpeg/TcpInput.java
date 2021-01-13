/*
 *    Copyright  2019-2021 Denis Kokorin
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

import com.github.kokorin.jaffree.network.NegotiatingTcpServer;
import com.github.kokorin.jaffree.network.TcpNegotiator;
import com.github.kokorin.jaffree.network.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TcpInput<T extends TcpInput<T>> extends BaseInput<T> implements Input {
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpInput.class);

    public TcpInput(TcpNegotiator tcpNegotiator) {
        this("tcp", tcpNegotiator);
    }

    private final TcpServer tcpServer;

    public TcpInput(String protocol, TcpNegotiator tcpNegotiator) {
        this(protocol, "", tcpNegotiator);
    }

    public TcpInput(String protocol, String suffix, TcpNegotiator tcpNegotiator) {
        this(protocol, suffix, new NegotiatingTcpServer(tcpNegotiator));
    }

    public TcpInput(String protocol, String suffix, TcpServer tcpServer) {
        this.tcpServer = tcpServer;
        super.setInput(protocol + "://" + tcpServer.getAddressAndPort() + suffix);
    }

    @Override
    public final Runnable helperThread() {
        return tcpServer;
    }

    @Override
    public T setInput(String input) {
        throw new RuntimeException("SocketInput input can't be changed");
    }
}
