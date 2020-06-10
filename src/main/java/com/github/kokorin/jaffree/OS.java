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

package com.github.kokorin.jaffree;

public class OS {
    // Test and values taken from org.apache.commons.lang3.SystemUtils
    public static final String OS_NAME = System.getProperty("os.name");

    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
    public static final boolean IS_MAC = OS_NAME.startsWith("Mac");
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux") || OS_NAME.startsWith("LINUX");
}