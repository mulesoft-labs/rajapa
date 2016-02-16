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
package org.raml.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.runners.Parameterized;

public abstract class TestDataProvider
{

    protected File input;
    protected File expectedOutput;
    protected String name;

    public TestDataProvider(File input, File expectedOutput, String name)
    {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.name = name;
    }

    public static Collection<Object[]> getData(URI baseFolder, String inputFileName, String outputFileName) throws URISyntaxException
    {
        return scanPath(StringUtils.EMPTY, baseFolder, inputFileName, outputFileName);
    }

    private static List<Object[]> scanPath(String folderPath, URI baseFolder, String inputFileName, String outputFileName)
    {
        final File testFolder = new File(baseFolder);
        final File[] scenarios = testFolder.listFiles();
        List<Object[]> result = new ArrayList<>();
        for (File scenario : scenarios)
        {
            if (scenario.isDirectory())
            {
                File input = new File(scenario, inputFileName);
                File output = new File(scenario, outputFileName);
                if (input.isFile() && output.isFile())
                {
                    result.add(new Object[] {input, output, folderPath + scenario.getName()});
                }
                else if (scenario.listFiles().length > 0)
                {
                    result.addAll(scanPath(scenario.getName() + ".", scenario.toURI(), inputFileName, outputFileName));
                }
            }
        }
        return result;
    }


    protected boolean jsonEquals(String produced, String expected)
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

}
