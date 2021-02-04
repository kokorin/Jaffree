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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.net.NegotiatingTcpServer;
import com.github.kokorin.jaffree.net.TcpNegotiator;
import com.github.kokorin.jaffree.net.TcpServer;
import com.github.kokorin.jaffree.process.ProcessHelper;

public abstract class TcpInput implements Input {
    private final String url;
    private final TcpServer tcpServer;

    public TcpInput(TcpNegotiator negotiator) {
        this("tcp", negotiator);
    }

    public TcpInput(String protocol, TcpNegotiator negotiator) {
        this(protocol, "", negotiator);
    }

    public TcpInput(String protocol, String suffix, TcpNegotiator negotiator) {
        this(protocol, suffix, NegotiatingTcpServer.onRandomPort(negotiator));
    }

    protected TcpInput(String protocol, String suffix, TcpServer tcpServer) {
        this.url = protocol + "://" + tcpServer.getAddressAndPort() + suffix;
        this.tcpServer = tcpServer;
    }

    @Override
    public final String getUrl() {
        return url;
    }

    @Override
    public ProcessHelper helperThread() {
        return tcpServer;
    }
}
