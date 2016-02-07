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
package org.raml.emitter.tck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.raml.nodes.*;
import org.raml.nodes.impl.KeyValueNodeImpl;
import org.raml.nodes.impl.MethodNode;
import org.raml.nodes.impl.ResourceNode;
import org.raml.nodes.impl.StringNodeImpl;
import org.raml.nodes.snakeyaml.SYArrayNode;
import org.raml.nodes.snakeyaml.SYNullNode;
import org.raml.nodes.snakeyaml.SYObjectNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TckEmitter
{

    private static final String INDENTATION = "    ";
    private static final String START_MAP = "{";
    private static final String END_MAP = "}";
    private static final String START_ARRAY = "[";
    private static final String END_ARRAY = "]";
    private static final String COMMA_SEP = ",\n";
    private static final String COLON_SEP = ": ";
    private static final String NEWLINE = "\n";

    public String dump(Node raml)
    {
        int depth = 0;
        StringBuilder dump = new StringBuilder();
        dumpObject((ObjectNode) raml, dump, depth);
        removeLastSeparator(dump);
        return dump.toString();
    }

    private void dumpNode(Node node, StringBuilder dump, int depth)
    {
        if (node instanceof ObjectNode)
        {
            dumpObject((ObjectNode) node, dump, depth);
        }
        else if (node instanceof SYArrayNode)
        {
            dumpArray((SYArrayNode) node, dump, depth);
        }
        else if (node instanceof ReferenceNode)
        {
            dumpReference((ReferenceNode) node, dump);
        }
        else if (node instanceof SYNullNode)
        {
            dumpNullNode(dump);
        }
        else if (node instanceof SimpleTypeNode)
        {
            dumpString((SimpleTypeNode) node, dump);
        }
        else
        {
            throw new RuntimeException("Unsupported node type: " + node.getClass().getSimpleName());
        }
    }

    private void dumpReference(ReferenceNode node, StringBuilder dump)
    {
        dump.append(sanitizeScalarValue(node.getRefName())).append(COMMA_SEP);
    }

    private void dumpArray(SYArrayNode arrayNode, StringBuilder dump, int depth)
    {
        dump.append(START_ARRAY);
        for (Node node : arrayNode.getChildren())
        {
            dumpNode(node, dump, depth + 1);
        }
        removeLastSeparator(dump);
        dump.append(END_ARRAY).append(COMMA_SEP);
    }

    private void dumpNullNode(StringBuilder dump)
    {
        dump.append(START_MAP).append(END_MAP).append(COMMA_SEP);
    }

    private void dumpString(SimpleTypeNode node, StringBuilder dump)
    {
        dump.append(sanitizeScalarValue(node.getValue())).append(COMMA_SEP);
    }

    private void dumpObject(ObjectNode objectNode, StringBuilder dump, int depth)
    {
        List<KeyValueNode> resourceNodes = new ArrayList<>();
        List<KeyValueNode> methodNodes = new ArrayList<>();

        startMap(dump, depth);

        for (Node node : objectNode.getChildren())
        {
            if (!(node instanceof KeyValueNode))
            {
                throw new RuntimeException();
            }
            if (node instanceof ResourceNode)
            {
                resourceNodes.add((ResourceNode) node);
                continue;
            }
            if (node instanceof MethodNode)
            {
                methodNodes.add((MethodNode) node);
                continue;
            }

            dumpKeyValueNode(dump, depth, (KeyValueNode) node);

        }
        dumpCustomArrayIfPresent(dump, depth + 1, methodNodes, "methods", "method");
        dumpCustomArrayIfPresent(dump, depth + 1, resourceNodes, "resources", "relativeUri");


        removeLastSeparator(dump);
        dump.append(addNewline(dump)).append(indent(depth)).append(END_MAP).append(COMMA_SEP);
    }

    private void dumpCustomArrayIfPresent(StringBuilder dump, int depth, List<KeyValueNode> keyValueNodes, String key, String innerKey)
    {
        if (!keyValueNodes.isEmpty())
        {
            dump.append(addNewline(dump)).append(indent(depth)).append(sanitizeScalarValue(key))
                .append(COLON_SEP).append(START_ARRAY).append(NEWLINE).append(indent(depth + 1));

            for (KeyValueNode node : keyValueNodes)
            {
                Node copy = copy(node.getValue());
                if (copy instanceof SYNullNode)
                {
                    copy = new SYObjectNode(new MappingNode(Tag.MAP, new ArrayList<NodeTuple>(), null));
                }
                copy.addChild(0, new KeyValueNodeImpl(new StringNodeImpl(innerKey), node.getKey()));
                dumpObject((ObjectNode) copy, dump, depth + 1);
            }
            removeLastSeparator(dump);
            dump.append(addNewline(dump)).append(indent(depth)).append(END_ARRAY).append(COMMA_SEP);
        }
    }

    private void dumpKeyValueNode(StringBuilder dump, int depth, KeyValueNode node)
    {
        // key
        String keyText = sanitizeScalarValue(((StringNode) node.getKey()).getValue());
        dump.append(addNewline(dump)).append(indent(depth + 1)).append(keyText).append(COLON_SEP);

        // value
        dumpNode(node.getValue(), dump, depth + 1);
    }

    private Node copy(Node node)
    {
        // TODO implement actual cloning and remove SY dependency
        if (node instanceof SYNullNode)
        {
            node = new SYObjectNode(new MappingNode(Tag.MAP, new ArrayList<NodeTuple>(), null));
        }
        return node;
    }


    // *******
    // helpers
    // *******

    private void startMap(StringBuilder dump, int depth)
    {
        if (dump.toString().endsWith(COMMA_SEP))
        {
            dump.append(indent(depth));
        }
        dump.append(START_MAP).append(NEWLINE);
    }

    private void removeLastSeparator(StringBuilder dump)
    {
        if (dump.toString().endsWith(COMMA_SEP))
        {
            int dumpLength = dump.length();
            dump.delete(dumpLength - COMMA_SEP.length(), dumpLength);
        }
    }

    private String indent(int depth)
    {
        return StringUtils.repeat(INDENTATION, depth);
    }

    private String addNewline(StringBuilder dump)
    {
        return dump.toString().endsWith("\n") ? "" : "\n";
    }

    private String sanitizeScalarValue(Object value)
    {
        String result = handleCustomScalar(value);
        if (result != null)
        {
            return jsonEscape(result);
        }
        else
        {
            result = jsonEscape(String.valueOf(value));
        }
        return result;
    }

    private String jsonEscape(String text)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            return objectMapper.writeValueAsString(text);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    private String handleCustomScalar(Object value)
    {
        if (value instanceof BigDecimal)
        {
            return ((BigDecimal) value).stripTrailingZeros().toString();
        }
        return null;
    }

}
