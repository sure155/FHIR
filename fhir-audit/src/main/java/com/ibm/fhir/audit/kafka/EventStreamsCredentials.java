/*
 * (C) Copyright IBM Corp. 2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.fhir.audit.kafka;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import com.ibm.fhir.exception.FHIRException;

public class EventStreamsCredentials {

    private String apiKey, user, password;
    private String[] kafkaBrokersSasl;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getKafkaBrokersSasl() {
        return kafkaBrokersSasl;
    }

    public void setKafkaBrokersSasl(String[] kafkaBrokersSasl) {
        this.kafkaBrokersSasl = kafkaBrokersSasl;
    }

    /**
     * Generates JSON from this object.
     */
    public static class Writer {
        private static final Map<java.lang.String, Object> properties =
                Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);
        private static final JsonGeneratorFactory PRETTY_PRINTING_GENERATOR_FACTORY =
                Json.createGeneratorFactory(properties);

        private Writer() {
            // No Operation
        }

        /**
         * @param obj
         * @return
         * @throws IOException
         */
        public static String generate(EventStreamsCredentials obj)
                throws IOException {
            String o = "{}";
            try (StringWriter writer = new StringWriter();) {
                try (JsonGenerator generator =
                        PRETTY_PRINTING_GENERATOR_FACTORY.createGenerator(writer);) {
                    generator.writeStartObject();

                    if (obj.getApiKey() != null) {
                        generator.write("api_key", obj.getApiKey());
                    }

                    if (obj.getPassword() != null) {
                        generator.write("password", obj.getPassword());
                    }

                    if (obj.getUser() != null) {
                        generator.write("user", obj.getUser());
                    }

                    if (obj.getKafkaBrokersSasl() != null) {
                        generator.writeStartArray("kafka_brokers_sasl");

                        for (String kafkaBrokersSasl : obj.getKafkaBrokersSasl()) {
                            generator.write(kafkaBrokersSasl);
                        }

                        generator.writeEnd();
                    }

                    generator.writeEnd();
                }
                o = writer.toString();
            }
            return o;
        }

    }

    /**
     * Parser
     */
    public static class Parser {
        private static final JsonReaderFactory JSON_READER_FACTORY = Json.createReaderFactory(null);

        private Parser() {
            // No Impl
        }

        public static EventStreamsCredentials parse(String in)
                throws FHIRException {
            StringReader stringReader = new StringReader(in);
            try (JsonReader jsonReader =
                    JSON_READER_FACTORY.createReader(stringReader)) {
                JsonObject jsonObject = jsonReader.readObject();
                EventStreamsCredentials.Builder builder =
                        EventStreamsCredentials.builder();

                if (jsonObject.get("api_key") != null) {
                    String apiKey = jsonObject.getString("api_key");
                    builder.apiKey(apiKey);
                }

                if (jsonObject.get("kafka_brokers_sasl") != null) {
                    JsonArray kafkaBrokersSasl = jsonObject.getJsonArray("kafka_brokers_sasl");
                    List<String> tmpStrs =
                            kafkaBrokersSasl.getValuesAs(JsonString.class).stream().map(e -> e.getString())
                                    .collect(Collectors.toList());
                    builder.kafkaBrokersSasl(tmpStrs.toArray(new String[0]));
                }

                if (jsonObject.get("user") != null) {
                    String user = jsonObject.getString("user");
                    builder.user(user);
                }

                if (jsonObject.get("password") != null) {
                    String password = jsonObject.getString("password");
                    builder.password(password);
                }

                return builder.build();
            } catch (javax.json.stream.JsonParsingException jpe) {
                throw new FHIRException("Parsed invalid ", jpe);
            }
        }
    }

    /**
     * Builder is a convenience pattern to assemble to Java Object
     */
    public static class Builder {
        private EventStreamsCredentials eventStreamsCredentials = new EventStreamsCredentials();

        private Builder() {
            // Intentionally hiding from external callers.
        }

        public Builder apiKey(String apiKey) {
            eventStreamsCredentials.setApiKey(apiKey);
            return this;
        }

        public Builder kafkaBrokersSasl(String[] kafkaBrokersSasl) {
            eventStreamsCredentials.setKafkaBrokersSasl(kafkaBrokersSasl);
            return this;
        }

        public Builder user(String user) {
            eventStreamsCredentials.setUser(user);
            return this;
        }

        public Builder password(String password) {
            eventStreamsCredentials.setPassword(password);
            return this;
        }

        public EventStreamsCredentials build() {
            return eventStreamsCredentials;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
