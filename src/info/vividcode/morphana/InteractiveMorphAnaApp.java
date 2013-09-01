/*
 * Copyright 2013 Nobuoka Y.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package info.vividcode.morphana;
// morphological analysis

import info.vividcode.util.json.JsonArray;
import info.vividcode.util.json.JsonBoolean;
import info.vividcode.util.json.JsonNull;
import info.vividcode.util.json.JsonNumber;
import info.vividcode.util.json.JsonObject;
import info.vividcode.util.json.JsonSerializer;
import info.vividcode.util.json.JsonString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

public class InteractiveMorphAnaApp {
    public static void main(String[] args) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
        new InteractiveMorphAnaApp().tokenizeEachLine(r, System.out);
    }

    private final Tokenizer tokenizer;

    public InteractiveMorphAnaApp() {
        tokenizer = Tokenizer.builder().build();
    }

    public void tokenizeEachLine(BufferedReader input, PrintStream output) throws IOException {
        String line;
        while ((line = input.readLine()) != null) {
            List<Token> tokens = tokenizer.tokenize(line);
            JsonArray tokenJsonArray = convertTokensToJsonArray(tokens);
            output.println(JsonSerializer.serialize(tokenJsonArray));
        }
    }

    private JsonArray convertTokensToJsonArray(List<Token> tokens) {
        JsonArray tokenJsonArray = new JsonArray();
        for (Token token : tokens) {
            JsonObject tokenJson = new JsonObject();
            tokenJson.put("position", new JsonNumber(token.getPosition()));
            tokenJson.put("part_of_speech", new JsonString(token.getPartOfSpeech()));
            String reading = token.getReading();
            tokenJson.put("reading", reading != null ? new JsonString(reading) : JsonNull.VALUE);
            String baseForm = token.getBaseForm();
            tokenJson.put("base_form", baseForm != null ? new JsonString(baseForm) : JsonNull.VALUE);
            tokenJson.put("surface_form", new JsonString(token.getSurfaceForm()));

            JsonArray featureJsonArray = new JsonArray();
            for (String feature : token.getAllFeaturesArray()) {
                featureJsonArray.add(new JsonString(feature));
            }
            tokenJson.put("all_features", featureJsonArray);

            tokenJson.put("is_known", token.isKnown() ? JsonBoolean.TRUE : JsonBoolean.FALSE);
            tokenJson.put("is_unknown", token.isUnknown() ? JsonBoolean.TRUE : JsonBoolean.FALSE);
            tokenJson.put("is_user", token.isUser() ? JsonBoolean.TRUE : JsonBoolean.FALSE);
            tokenJsonArray.add(tokenJson);
        }
        return tokenJsonArray;
    }
}
