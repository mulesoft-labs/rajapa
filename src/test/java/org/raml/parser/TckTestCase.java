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
import org.raml.emitter.tck.TckEmitter;
import org.raml.nodes.Node;

@RunWith(Parameterized.class)
public class TckTestCase
{


    private File input;
    private File expected;
    private String name;

    public TckTestCase(File input, File expected, String name)
    {
        this.input = input;
        this.expected = expected;
        this.name = name;
    }

    @Test
    public void runTest() throws IOException
    {
        if (!expected.exists())
        {
            return;
        }

        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(input);
        assertThat(raml, notNullValue());
        String dump = new TckEmitter().dump(raml);


        String expected = IOUtils.toString(new FileInputStream(this.expected));
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
        final URI baseFolder = TckTestCase.class.getResource("").toURI();
        final File testFolder = new File(baseFolder);
        List<Object[]> result = new ArrayList<>();
        addScenarios(testFolder.listFiles(), result, "output.json", "");
        return result;
    }

    private static void addScenarios(File[] scenarios, List<Object[]> result, String outputFileName, String parentScenario)
    {
        for (File scenario : scenarios)
        {
            if (scenario.isDirectory())
            {
                String scenarioName = parentScenario + scenario.getName();
                File input = new File(scenario, "input.raml");
                File output = new File(scenario, outputFileName);
                if (input.isFile() && output.isFile())
                {
                    result.add(new Object[] {input, output, scenarioName});
                }
                File[] subdirs = scenario.listFiles(new FilenameFilter()
                {
                    @Override
                    public boolean accept(File dir, String name)
                    {
                        return new File(dir, name).isDirectory();
                    }
                });
                if (subdirs != null && subdirs.length > 0)
                {
                    addScenarios(subdirs, result, outputFileName, scenarioName + "/");
                }
            }
        }
    }

}