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
        return tree;
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
