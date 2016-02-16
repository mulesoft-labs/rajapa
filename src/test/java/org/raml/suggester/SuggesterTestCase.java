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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.RamlSuggester;
import org.raml.dataprovider.TestDataProvider;

@RunWith(Parameterized.class)
public class SuggesterTestCase extends TestDataProvider
{

    public static final String CURSOR_KEYWORD = "<cursor>";
    public static final String INPUT_FILE_NAME = "input.raml";
    public static final String OUTPUT_FILE_NAME = "output.json";

    public SuggesterTestCase(File input, File expecteOutput, String name)
    {
        super(input, expecteOutput, name);
    }

    @Test
    public void verifySuggestion() throws IOException
    {
        final RamlSuggester ramlSuggester = new RamlSuggester();
        final String content = IOUtils.toString(new FileInputStream(input), "UTF-8");
        final int offset = content.indexOf(CURSOR_KEYWORD);
        final String document = content.substring(0, offset) + content.substring(offset + CURSOR_KEYWORD.length());
        final List<Suggestion> suggestions = ramlSuggester.suggestions(document, offset - 1);
        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String dump = ow.writeValueAsString(suggestions);
        final String expected = IOUtils.toString(new FileInputStream(this.expectedOutput));
        System.out.println("dump = \n" + dump);
        Assert.assertTrue(jsonEquals(dump, expected));
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(SuggesterTestCase.class.getResource("").toURI(), INPUT_FILE_NAME, OUTPUT_FILE_NAME);
    }
}
