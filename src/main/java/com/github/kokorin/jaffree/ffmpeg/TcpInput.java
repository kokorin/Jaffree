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

public abstract class TcpInput<T extends TcpInput<T>> extends BaseInput<T> implements Input {
    private final TcpServer tcpServer;

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpInput.class);

    public TcpInput(TcpNegotiator tcpNegotiator) {
        this("tcp", tcpNegotiator);
    }

    public TcpInput(String protocol, TcpNegotiator tcpNegotiator) {
        this(protocol, "", tcpNegotiator);
    }

    public TcpInput(String protocol, String suffix, TcpNegotiator tcpNegotiator) {
        this(protocol, suffix, NegotiatingTcpServer.onRandomPort(tcpNegotiator));
    }

    public TcpInput(String protocol, String suffix, TcpServer tcpServer) {
        super(protocol + "://" + tcpServer.getAddressAndPort() + suffix);
        this.tcpServer = tcpServer;
    }

    @Override
    public final ProcessHelper helperThread() {
        return tcpServer;
    }
}
