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
package org.raml.v2.api;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlVersion;
import org.raml.v2.internal.impl.commons.model.RamlValidationResult;
import org.raml.v2.internal.impl.commons.model.builder.ModelProxyBuilder;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.utils.StreamUtils;

/**
 * Entry point class to parse top level RAML descriptors.
 * Supports versions 0.8 and 1.0
 */
public class RamlModelBuilder
{

    private ResourceLoader resourceLoader;
    private RamlBuilder builder = new RamlBuilder();

    public RamlModelBuilder()
    {
        this(new DefaultResourceLoader());
    }

    public RamlModelBuilder(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Nonnull
    public RamlModelResult buildApi(String ramlLocation)
    {
        if (ramlLocation == null)
        {
            throw new IllegalArgumentException("ramlLocation cannot be null");
        }
        Node ramlNode = null;
        String content = getRamlContent(ramlLocation);
        if (content != null)
        {
            ramlNode = builder.build(content, resourceLoader, ramlLocation);
        }
        return generateRamlApiResult(ramlNode);
    }

    @Nonnull
    public RamlModelResult buildApi(File ramlFile)
    {
        Node ramlNode = builder.build(ramlFile, resourceLoader);
        return generateRamlApiResult(ramlNode);
    }

    @Nonnull
    public RamlModelResult buildApi(String content, String ramlLocation)
    {
        if (content == null)
        {
            return buildApi(ramlLocation);
        }
        Node ramlNode = builder.build(content, resourceLoader, ramlLocation);
        return generateRamlApiResult(ramlNode);
    }

    @Nonnull
    public RamlModelResult buildApi(Reader content, String ramlLocation)
    {
        if (content == null)
        {
            return buildApi(ramlLocation);
        }
        Node ramlNode = builder.build(content, resourceLoader, ramlLocation);
        return generateRamlApiResult(ramlNode);
    }

    private RamlModelResult generateRamlApiResult(Node ramlNode)
    {
        List<ValidationResult> validationResults = new ArrayList<>();
        if (ramlNode instanceof ErrorNode)
        {
            validationResults.add(new RamlValidationResult((ErrorNode) ramlNode));
        }
        else if (!(ramlNode instanceof RamlDocumentNode))
        {
            validationResults.add(new RamlValidationResult("Raml file is not a root document."));
        }
        else
        {
            List<ErrorNode> errors = ramlNode.findDescendantsWith(ErrorNode.class);
            for (ErrorNode errorNode : errors)
            {
                validationResults.add(new RamlValidationResult(errorNode));
            }
            if (validationResults.isEmpty())
            {
                return wrapTree((RamlDocumentNode) ramlNode);
            }
        }
        return new RamlModelResult(validationResults);
    }

    private RamlModelResult wrapTree(RamlDocumentNode ramlNode)
    {
        if (ramlNode.getVersion() == RamlVersion.RAML_10)
        {
            org.raml.v2.api.model.v10.api.Api apiV10 = ModelProxyBuilder.createRaml(org.raml.v2.api.model.v10.api.Api.class, ramlNode);
            return new RamlModelResult(apiV10);
        }
        else
        {
            org.raml.v2.api.model.v08.api.Api apiV08 = ModelProxyBuilder.createRaml(org.raml.v2.api.model.v08.api.Api.class, ramlNode);
            return new RamlModelResult(apiV08);
        }
    }

    private String getRamlContent(String ramlLocation)
    {
        InputStream ramlStream = resourceLoader.fetchResource(ramlLocation);
        if (ramlStream != null)
        {
            return StreamUtils.toString(ramlStream);
        }
        return null;
    }

}
