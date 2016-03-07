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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.raml.grammar.rule.ErrorNodeFactory;
import org.raml.impl.commons.RamlHeader;
import org.raml.impl.v08.Raml08Builder;
import org.raml.impl.v10.Raml10Builder;
import org.raml.loader.ClassPathResourceLoader;
import org.raml.loader.CompositeResourceLoader;
import org.raml.loader.FileResourceLoader;
import org.raml.loader.ResourceLoader;
import org.raml.loader.UrlResourceLoader;
import org.raml.nodes.Node;

/**
 * RamlBuilder create a Node representation of your raml.
 *
 * @see Node
 */
public class RamlBuilder
{

    public static int FIRST_PHASE = 1;
    public static int SECOND_PHASE = 2;

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
        return build(content, resourceLoader, "");
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
            if (RamlHeader.RAML_10_VERSION.equals(ramlHeader.getVersion()))
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
    }


}
