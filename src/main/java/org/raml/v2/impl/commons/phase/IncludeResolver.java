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
package org.raml.v2.impl.commons.phase;

import java.io.IOException;
import java.io.InputStream;

import org.raml.v2.impl.commons.RamlHeader;
import org.raml.v2.loader.ResourceLoader;
import org.raml.v2.nodes.IncludeErrorNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.StringNodeImpl;
import org.raml.v2.nodes.snakeyaml.RamlNodeParser;
import org.raml.v2.nodes.snakeyaml.SYIncludeNode;
import org.raml.v2.phase.Transformer;
import org.raml.v2.utils.StreamUtils;


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
            if (inputStream == null)
            {
                String msg = "Include cannot be resolved: " + resourcePath;
                return new IncludeErrorNode(msg);
            }
            Node result;
            String includeContent = StreamUtils.toString(inputStream);
            if (resourcePath.endsWith(".raml") || resourcePath.endsWith(".yaml") || resourcePath.endsWith(".yml"))
            {
                boolean supportUses = false;
                try
                {
                    RamlHeader ramlHeader = RamlHeader.parse(includeContent);
                    supportUses = ramlHeader.getFragment() != null;
                }
                catch (RamlHeader.InvalidHeaderException e)
                {
                    // no valid header defined => !supportUses
                }
                result = RamlNodeParser.parse(includeContent, supportUses);
            }
            else
            // scalar value
            {
                result = new StringNodeImpl(includeContent);
            }

            if (result == null)
            {
                String msg = "Include file is empty: " + resourcePath;
                result = new IncludeErrorNode(msg);
            }

            return result;
        }
        catch (IOException e)
        {
            String msg = String.format("Include cannot be resolved: %s. (%s)", resourcePath, e.getMessage());
            return new IncludeErrorNode(msg);
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
