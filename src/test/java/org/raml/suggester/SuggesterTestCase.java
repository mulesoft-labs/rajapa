/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.suggester;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.flipkart.zjsonpatch.JsonDiff;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.RamlSuggester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class SuggesterTestCase {

    public static final String CURSOR_KEYWORD = "<cursor>";
    private File input;
    private File expected;


    public SuggesterTestCase(File input, File output, String name) {
        this.input = input;
        this.expected = output;
    }


    @Test
    public void verifySuggestion() throws IOException {
        final RamlSuggester ramlSuggester = new RamlSuggester();
        final String content = IOUtils.toString(new FileInputStream(input), "UTF-8");
        final int offset = content.indexOf(CURSOR_KEYWORD);
        final String document = content.substring(0, offset) + content.substring(offset + CURSOR_KEYWORD.length());
        final List<Suggestion> suggestions = ramlSuggester.suggestions(document, offset - 1);
        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String dump = ow.writeValueAsString(suggestions);
        final String expected = IOUtils.toString(new FileInputStream(this.expected));
        System.out.println("dump = \n" + dump);
        Assert.assertTrue(jsonEquals(dump, expected));
    }

    private boolean jsonEquals(String produced, String expected) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode beforeNode = mapper.readTree(expected);
            JsonNode afterNode = mapper.readTree(produced);
            JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
            String diffs = patch.toString();
            if ("[]".equals(diffs)) {
                return true;
            }
            System.out.println("json diff: " + diffs);
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> data() throws URISyntaxException {
        final URI baseFolder = SuggesterTestCase.class.getResource("").toURI();
        final File testFolder = new File(baseFolder);
        final File[] scenarios = testFolder.listFiles();
        List<Object[]> result = new ArrayList<>();
        for (File scenario : scenarios) {
            if (scenario.isDirectory()) {
                result.add(new Object[]{
                        new File(scenario, "input.raml"),
                        new File(scenario, "output.json"),
                        scenario.getName()
                });
            }
        }
        return result;
    }
}
