/*
 *    Copyright 2021 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe.data;

import com.github.kokorin.jaffree.JaffreeException;
import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonFormatParser implements FormatParser {
    @Override
    public String getFormatName() {
        return "json";
    }

    @Override
    public ProbeData parse(InputStream inputStream) {
        JsonObject jsonObject;
        try {
            jsonObject = JsonParser.object().from(inputStream);
        } catch (JsonParserException e) {
            throw new JaffreeException("Failed to parse JSON output", e);
        }

        return new ProbeDataJson(jsonObject);
    }

    public static class ProbeDataJson extends AbstractProbeData implements ProbeData {
        private final JsonObject data;

        public ProbeDataJson(JsonObject data) {
            this.data = data;
        }

        @Override
        public Object getValue(String name) {
            if (data == null) {
                return null;
            }
            return data.get(name);
        }

        @Override
        public List<ProbeData> getSubDataList(String name) {
            if (data == null) {
                return null;
            }

            JsonArray jsonArray = data.getArray(name);
            if (jsonArray == null) {
                return null;
            }

            List<ProbeData> result = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                result.add(new ProbeDataJson(jsonArray.getObject(i)));
            }
            return result;
        }

        @Override
        public ProbeData getSubData(String name) {
            if (data == null) {
                return null;
            }

            JsonObject subData = data.getObject(name);
            if (subData == null) {
                return null;
            }

            return new ProbeDataJson(subData);
        }
    }
}
