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
package org.raml.grammar.rule;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.raml.grammar.rule.xml.XsdResourceResolver;
import org.raml.loader.ResourceLoader;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.StringNode;
import org.raml.suggester.RamlParsingContext;
import org.raml.suggester.Suggestion;
import org.xml.sax.SAXException;

public class XmlSchemaValidationRule extends Rule
{

    private Schema schema;

    public XmlSchemaValidationRule(String schema, ResourceLoader resourceLoader, String actualPath)
    {
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new XsdResourceResolver(resourceLoader, actualPath));
            this.schema = factory.newSchema(new StreamSource(new StringReader(schema)));
        }
        catch (SAXException e)
        {
            this.schema = null;
        }
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Lists.newArrayList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return false;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (schema == null)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode("Invalid XmlSchema");
        }
        Node source = node.getSource();
        if (source == null)
        {
            if (!(node instanceof ObjectNode))
            {
                return ErrorNodeFactory.createInvalidXmlExampleNode("Source was null");
            }
            else
            {
                if (node.getChildren().size() == 1 &&
                    node.getChildren().get(0) instanceof KeyValueNode &&
                    (((KeyValueNode) node.getChildren().get(0)).getValue()) instanceof StringNode)
                {
                    source = ((KeyValueNode) node.getChildren().get(0)).getValue();
                }
            }
        }
        if (source instanceof StringNode)
        {
            internalValidateExample(node, (StringNode) source);
        }
        return node;
    }

    private void internalValidateExample(@Nonnull Node node, StringNode source)
    {
        String value = source.getValue();
        try
        {
            schema.newValidator().validate(new StreamSource(new StringReader(value)));
        }
        catch (SAXException | IOException e)
        {
            node.replaceWith(ErrorNodeFactory.createInvalidXmlExampleNode(e.getMessage()));
        }
    }

    @Override
    public String getDescription()
    {
        return null;
    }
}
