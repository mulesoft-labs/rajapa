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
package org.raml.v2.utils;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.raml.v2.RamlBuilder;
import org.raml.v2.impl.commons.nodes.PayloadValidationResultNode;
import org.raml.v2.nodes.Node;

public class NodeValidatorTest
{
    private NodeValidator nodeValidator;
    private Node tree;

    @Before
    public void setUp() throws IOException
    {
        RamlBuilder builder = new RamlBuilder();
        tree = builder.build(new File(this.getClass().getClassLoader().getResource("org/raml/v2/parser/examples/include-json-schema/input.raml").getPath()));
        this.nodeValidator = new NodeValidator(builder.getResourceLoader(), builder.getActualPath());
    }

    @Test
    public void testParsingFailure()
    {
        PayloadValidationResultNode validationNode = this.nodeValidator.validatePayload(tree.get("types"), "User", "{\"name\":\"Federico\", \"age\": \"MyAge\"}");
        Assert.assertFalse(validationNode.validationSucced());
    }

    @Test
    public void testParsingOk()
    {
        PayloadValidationResultNode validationNode = this.nodeValidator.validatePayload(tree.get("types"), "User", "{\"name\":\"Federico\", \"age\": 10}");
        Assert.assertTrue(validationNode.validationSucced());
    }
}