/*
 *    Copyright  2017 Denis Kokorin
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

import com.github.kokorin.jaffree.JaffreeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ThrowingStdReader<T> implements StdReader<T> {

    @Override
    public T read(InputStream stdOut) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdOut));
        StringBuilder message = new StringBuilder();
        boolean messageIsEmpty = true;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[") || line.contains("Last message repeated")) {
                    continue;
                }

                if (!messageIsEmpty) {
                    message.append('\n');
                } else {
                    messageIsEmpty = false;
                }
                message.append(line);
            }
        } catch (IOException e) {
            throw new JaffreeException("Failed to read stdout (stderr)", e);
        }

        if (!messageIsEmpty) {
            throw new JaffreeException("Finished with error message: " + message.toString());
        }

        return null;
    }
}
