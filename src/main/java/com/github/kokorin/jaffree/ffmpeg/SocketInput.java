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
import com.github.kokorin.jaffree.process.FFHelper;

public abstract class SocketInput<T extends SocketInput<T>> extends BaseInput<T> implements Input {
    private final TcpServer tcpServer;

    public SocketInput(String protocol, TcpNegotiator tcpNegotiator) {
        this(protocol, "", tcpNegotiator);
    }

    public SocketInput(String protocol, String suffix, TcpNegotiator tcpNegotiator) {
        this.tcpServer = new NegotiatingTcpServer(tcpNegotiator);
        super.setInput(protocol + "://" + tcpServer.getAddressAndPort() + suffix);
    }

    @Override
    public final FFHelper helperThread() {
        return tcpServer;
    }

    @Override
    public T setInput(String input) {
        throw new RuntimeException("SocketInput input can't be changed");
    }
}
