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

import java.util.List;

import org.raml.impl.commons.model.BuiltInType;
import org.raml.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.impl.v10.nodes.types.builtin.NumericTypeNode;
import org.raml.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.KeyValueNodeImpl;
import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.StringNode;
import org.raml.nodes.StringNodeImpl;
import org.raml.nodes.snakeyaml.SYStringNode;
import org.raml.phase.Phase;

public class SugarRushPhase implements Phase
{

    @Override
    public Node apply(Node tree)
    {
        sweetenBuiltInTypes(tree);
        sweetenObjects(tree);
        sweetenTypeSystemObjects(tree);
        return tree;
    }

    private void sweetenTypeSystemObjects(Node tree)
    {
        final List<StringNode> basicSugar = tree.findDescendantsWith(StringNode.class);
        for (StringNode sugarNode : basicSugar)
        {
            if (isTypeSystemObjectProperty(sugarNode))
            {
                if (sugarNode.getChildren().isEmpty() && isValidTypeSystemObject(tree, sugarNode))
                {
                    Node newNode = new ObjectTypeNode();
                    newNode.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(sugarNode.getValue())));
                    // handleExample(sugarNode, newNode);
                    sugarNode.replaceWith(newNode);
                }
            }
        }
    }

    private boolean isValidTypeSystemObject(Node tree, StringNode sugarNode)
    {
        Node types = tree.get("types");
        // handling special union types, this will be resolved in the types transformation phase.
        String value = sugarNode.getValue();
        if (isUnion(sugarNode) || value.endsWith("[]"))
        {
            return true;
        }
        if (types != null)
        {
            Node object = types.get(value);
            return object != null && object instanceof ObjectNode;
        }
        return false;
    }

    private boolean isUnion(StringNode sugarNode)
    {
        String value = sugarNode.getValue();
        if (sugarNode.getParent() instanceof KeyValueNode)
        {
            KeyValueNode parent = (KeyValueNode) sugarNode.getParent();
            String key = ((StringNode) parent.getKey()).getValue();
            return value.contains("|") && !("type".equals(key) || "pattern".equals(key));
        }
        return false;
    }

    private boolean isTypeSystemObjectProperty(StringNode sugarNode)
    {
        // union type node, will be resolved in type transformation phase.
        if (isUnion(sugarNode))
        {
            return true;
        }
        Node properties = sugarNode.getParent().getParent().getParent();
        if (properties != null)
        {
            Node type = properties.getParent();
            if (sugarNode.getParent() instanceof KeyValueNode)
            {
                KeyValueNode parentNode = ((KeyValueNode) sugarNode.getParent());
                if (parentNode.getValue() instanceof StringNode && ((StringNode) parentNode.getValue()).getValue().equals(sugarNode.getValue()) && type.get("type") != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void sweetenBuiltInTypes(Node tree)
    {
        final List<StringNode> basicSugar = tree.findDescendantsWith(StringNode.class);

        for (StringNode sugarNode : basicSugar)
        {
            if (BuiltInType.isBuiltInType(sugarNode.getValue()) && !isTypePresentBasic(sugarNode))
            {
                if (sugarNode.getChildren().isEmpty())
                {
                    Node newNode = getSugarNode(sugarNode.getValue());
                    if (newNode != null)
                    {
                        newNode.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(sugarNode.getValue())));
                        handleExample(sugarNode, newNode);
                        sugarNode.replaceWith(newNode);
                    }
                }
            }
        }
    }


    private void sweetenObjects(Node tree)
    {
        final List<StringNode> basicSugar = tree.findDescendantsWith(StringNode.class);
        for (StringNode sugarNode : basicSugar)
        {
            if ("properties".equals(sugarNode.getValue()))
            {
                if (!isTypePresentObject(sugarNode))
                {
                    Node grandParent = sugarNode.getParent().getParent();
                    grandParent.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl("object")));
                }
            }
        }
    }

    private void handleExample(Node sugarNode, Node newNode)
    {
        Node example = sugarNode.getParent().getParent().get("example");
        if (example != null)
        {
            Node exampleRoot = example.getParent();
            exampleRoot.getParent().removeChild(exampleRoot);
            newNode.addChild(exampleRoot);
        }
    }

    private boolean isTypePresentBasic(Node sugarNode)
    {
        Node parent = sugarNode.getParent();
        if (parent instanceof KeyValueNode && ((KeyValueNode) parent).getKey() instanceof SYStringNode)
        {
            SYStringNode key = (SYStringNode) ((KeyValueNode) parent).getKey();
            return "type".equals(key.getValue());
        }
        return false;
    }

    private boolean isTypePresentObject(Node sugarNode)
    {
        return sugarNode.getParent().getParent().get("type") != null;
    }

    private Node getSugarNode(String typeNode)
    {
        if (BuiltInType.STRING.getType().equals(typeNode))
        {
            return new StringTypeNode();
        }
        else if (BuiltInType.NUMBER.getType().equals(typeNode) || BuiltInType.INTEGER.getType().equals(typeNode))
        {
            return new NumericTypeNode();
        }
        else if (BuiltInType.BOOLEAN.getType().equals(typeNode))
        {
            return new BooleanTypeNode();
        }
        else if ("object".equals(typeNode))
        {
            return new ObjectTypeNode();
        }
        else
        {
            return null;
        }
    }
}
