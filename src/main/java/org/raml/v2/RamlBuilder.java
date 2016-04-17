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
package org.raml.v2;

import static org.raml.v2.impl.commons.RamlVersion.RAML_10;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.raml.v2.grammar.rule.ErrorNodeFactory;
import org.raml.v2.impl.commons.RamlHeader;
import org.raml.v2.impl.v08.Raml08Builder;
import org.raml.v2.impl.v10.Raml10Builder;
import org.raml.v2.loader.CompositeResourceLoader;
import org.raml.v2.loader.DefaultResourceLoader;
import org.raml.v2.loader.FileResourceLoader;
import org.raml.v2.loader.ResourceLoader;
import org.raml.v2.nodes.Node;

/**
 * RamlBuilder create a Node representation of your raml.
 *
 * @see Node
 */
public class RamlBuilder
{

    public static int FIRST_PHASE = 1;
    public static int SECOND_PHASE = 2;
    public static int SUGAR_PHASE = 3;

    private int maxPhaseNumber;

    public RamlBuilder()
    {
        maxPhaseNumber = Integer.MAX_VALUE;
    }

    public RamlBuilder(int maxPhaseNumber)
    {
        this.maxPhaseNumber = maxPhaseNumber;
    }

    public Node build(File ramlFile) throws IOException
    {
        final ResourceLoader resourceLoader = new CompositeResourceLoader(
                new DefaultResourceLoader(),
                new FileResourceLoader(ramlFile.getParent()));
        try (FileReader reader = new FileReader(ramlFile))
        {
            return build(reader, resourceLoader, ramlFile.getName());
        }
    }

    public Node build(String content)
    {
        return build(content, "");
    }

    public Node build(String content, String resourceLocation)
    {
        return build(content, new DefaultResourceLoader(), resourceLocation);
    }

    public Node build(String content, ResourceLoader resourceLoader, String resourceLocation)
    {
        return build(new StringReader(content), resourceLoader, resourceLocation);
    }

    public Node build(Reader content, ResourceLoader resourceLoader, String resourceLocation)
    {
        try
        {
            final String stringContent = IOUtils.toString(content);
            RamlHeader ramlHeader = RamlHeader.parse(stringContent);
            if (RAML_10 == ramlHeader.getVersion())
            {
                return new Raml10Builder().build(stringContent, ramlHeader.getFragment(), resourceLoader, resourceLocation, maxPhaseNumber);
            }
            return new Raml08Builder().build(stringContent, resourceLoader, resourceLocation, maxPhaseNumber);
        }
        catch (IOException ioe)
        {
            return ErrorNodeFactory.createInvalidInput(ioe);
        }
        catch (RamlHeader.InvalidHeaderVersionException e)
        {
            return ErrorNodeFactory.createUnsupportedVersion(e.getMessage());
        }
        catch (RamlHeader.InvalidHeaderFragmentException e)
        {
            return ErrorNodeFactory.createInvalidFragmentName(e.getMessage());
        }
        catch (RamlHeader.MissingHeaderException e)
        {
            return ErrorNodeFactory.createEmptyDocument();
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            return ErrorNodeFactory.createInvalidHeader(e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(content);
        }
    }


}
