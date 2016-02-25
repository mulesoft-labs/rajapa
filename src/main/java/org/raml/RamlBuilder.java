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
package org.raml;

import org.apache.commons.io.IOUtils;
import org.raml.grammar.rule.ErrorNodeFactory;
import org.raml.impl.v08.Raml08Builder;
import org.raml.impl.v10.Raml10Builder;
import org.raml.loader.*;
import org.raml.nodes.Node;

import java.io.*;
import java.util.StringTokenizer;

/**
 * RamlBuilder create a Node representation of your raml.
 *
 * @see Node
 */
public class RamlBuilder
{

    public static final String RAML_10_VERSION = "1.0";
    public static final String RAML_08_VERSION = "0.8";
    public static final String RAML_HEADER_PREFIX = "#%RAML";

    public static int FIRST_PHASE = 1;

    private int maxPhaseNumber;

    public RamlBuilder()
    {
        maxPhaseNumber = Integer.MAX_VALUE;
    }

    public RamlBuilder(int maxPhaseNumber)
    {
        this.maxPhaseNumber = maxPhaseNumber;
    }

    public Node build(File ramlFile) throws FileNotFoundException
    {
        final ResourceLoader resourceLoader = new CompositeResourceLoader(new UrlResourceLoader(),
                new ClassPathResourceLoader(),
                new FileResourceLoader("."),
                new FileResourceLoader(ramlFile.getParent()));
        return build(new FileReader(ramlFile), resourceLoader, "");
    }

    public Node build(String content)
    {
        final ResourceLoader resourceLoader = new CompositeResourceLoader(new UrlResourceLoader(),
                new ClassPathResourceLoader(),
                new FileResourceLoader(".")
                );
        return build(new StringReader(content), resourceLoader, "");
    }


    public Node build(Reader content, ResourceLoader resourceLoader, String resourceLocation)
    {
        try
        {
            final String stringContent = IOUtils.toString(content);
            final StringTokenizer lines = new StringTokenizer(stringContent, "\n");
            if (lines.hasMoreElements())
            {
                final String header = lines.nextToken().trim();
                final StringTokenizer headerParts = new StringTokenizer(header);
                if (headerParts.hasMoreTokens())
                {
                    final String raml = headerParts.nextToken();
                    if (RAML_HEADER_PREFIX.equals(raml))
                    {
                        if (headerParts.hasMoreTokens())
                        {
                            final String version = headerParts.nextToken();
                            if (RAML_10_VERSION.equals(version))
                            {
                                final String fragmentText = headerParts.hasMoreTokens() ? headerParts.nextToken() : "";
                                return new Raml10Builder().build(stringContent, fragmentText, resourceLoader, resourceLocation, maxPhaseNumber);
                            }
                            else if (RAML_08_VERSION.equals(version))
                            {
                                return new Raml08Builder().build(stringContent, resourceLoader, resourceLocation, maxPhaseNumber);
                            }
                            else
                            {
                                return ErrorNodeFactory.createUnsupportedVersion(version);
                            }
                        }
                    }
                }
                return ErrorNodeFactory.createInvalidHeader(header);
            }
            else
            {
                return ErrorNodeFactory.createEmptyDocument();
            }
        }
        catch (IOException ioe)
        {
            return ErrorNodeFactory.createInvalidInput(ioe);
        }
    }


}
