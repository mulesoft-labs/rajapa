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
package org.raml.v2.internal.framework.nodes.snakeyaml;

import java.io.Reader;
import java.io.StringReader;

import javax.annotation.Nullable;

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.DefaultPosition;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

public class RamlNodeParser
{


    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, Reader reader)
    {
        try
        {
            Yaml yamlParser = new Yaml();
            org.yaml.snakeyaml.nodes.Node composedNode = yamlParser.compose(reader);
            if (composedNode == null)
            {
                return null;
            }
            else
            {
                return new SYModelWrapper(resourceLoader, resourcePath).wrap(composedNode);
            }
        }
        catch (final MarkedYAMLException e)
        {
            return buildYamlErrorNode(e);
        }
    }

    private static Node buildYamlErrorNode(MarkedYAMLException e)
    {
        final ErrorNode errorNode = new ErrorNode("Underlying error while parsing YAML syntax: '" + e.getMessage() + "'");
        final Mark problemMark = e.getProblemMark();
        errorNode.setStartPosition(new DefaultPosition(problemMark.getIndex(), problemMark.getLine(), 0, "", new DefaultResourceLoader()));
        errorNode.setEndPosition(new DefaultPosition(problemMark.getIndex() + 1, problemMark.getLine(), problemMark.getColumn(), "", new DefaultResourceLoader()));
        return errorNode;
    }

    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, String content)
    {
        return parse(resourceLoader, resourcePath, new StringReader(content));
    }


}
