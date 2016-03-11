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
package org.raml.impl.commons.phase;

import java.io.IOException;
import java.io.InputStream;

import org.raml.loader.ResourceLoader;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.nodes.StringNodeImpl;
import org.raml.nodes.snakeyaml.RamlNodeParser;
import org.raml.nodes.snakeyaml.SYIncludeNode;
import org.raml.phase.Transformer;
import org.raml.utils.StreamUtils;


public class IncludeResolver implements Transformer
{

    private final ResourceLoader resourceLoader;
    private final String resourceLocation;

    public IncludeResolver(ResourceLoader resourceLoader, String resourceLocation)
    {
        this.resourceLoader = resourceLoader;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public boolean matches(Node tree)
    {
        return tree instanceof SYIncludeNode;
    }

    @Override
    public Node transform(Node tree)
    {

        SYIncludeNode includeNode = (SYIncludeNode) tree;
        String resourcePath = resolvePath(includeNode.getIncludePath());
        try (InputStream inputStream = resourceLoader.fetchResource(resourcePath))
        {
            Node result;
            if (inputStream == null)
            {
                String msg = "Include cannot be resolved: " + resourcePath;
                result = new ErrorNode(msg);
            }
            else if (resourcePath.endsWith(".raml") || resourcePath.endsWith(".yaml") || resourcePath.endsWith(".yml"))
            {
                result = RamlNodeParser.parse(inputStream);
            }
            else
            // scalar value
            {
                String newValue = StreamUtils.toString(inputStream);
                result = new StringNodeImpl(newValue);
            }

            if (result == null)
            {
                String msg = "Include file is empty: " + resourcePath;
                result = new ErrorNode(msg);
            }

            return result;
        }
        catch (IOException e)
        {
            String msg = String.format("Include cannot be resolved: %s. (%s)", resourcePath, e.getMessage());
            return new ErrorNode(msg);
        }
    }

    private String resolvePath(String includePath)
    {
        // TODO works for relative only for now
        int lastSlash = resourceLocation.lastIndexOf("/");
        if (lastSlash != -1)
        {
            return resourceLocation.substring(0, lastSlash + 1) + includePath;
        }
        return includePath;
    }
}
