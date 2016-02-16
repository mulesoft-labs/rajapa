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

package org.raml.parser;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.RamlBuilder;
import org.raml.dataprovider.TestDataProvider;
import org.raml.emitter.tck.TckEmitter;
import org.raml.nodes.Node;

@RunWith(Parameterized.class)
public class TckTestCase extends TestDataProvider
{

    private static final String INPUT_FILE_NAME = "input.raml";
    private static final String OUTPUT_FILE_NAME = "output.json";

    public TckTestCase(File input, File expected, String name)
    {
        super(input, expected, name);
    }

    @Test
    public void runTest() throws IOException
    {
        if (!expectedOutput.exists())
        {
            return;
        }

        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(input);
        assertThat(raml, notNullValue());
        String dump = new TckEmitter().dump(raml);


        String expected = IOUtils.toString(new FileInputStream(this.expectedOutput));
        System.out.println("dump = \n" + dump);
        System.out.println("expected = \n" + expected);
        Assert.assertTrue(jsonEquals(dump, expected));

    }

    private boolean jsonEquals(String produced, String expected)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            JsonNode beforeNode = mapper.readTree(expected);
            JsonNode afterNode = mapper.readTree(produced);
            JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
            String diffs = patch.toString();
            if ("[]".equals(diffs))
            {
                return true;
            }
            System.out.println("json diff: " + diffs);
            return false;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> data() throws URISyntaxException
    {
        return getData(TckTestCase.class.getResource("").toURI(), INPUT_FILE_NAME, OUTPUT_FILE_NAME);
    }

}
