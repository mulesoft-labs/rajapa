/*
 *
 */
package org.raml.emitter.tck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.StringNode;
import org.raml.nodes.impl.KeyValueNodeImpl;
import org.raml.nodes.impl.MethodNode;
import org.raml.nodes.impl.ResourceNode;
import org.raml.nodes.impl.StringNodeImpl;
import org.raml.nodes.snakeyaml.SYNullNode;
import org.raml.nodes.snakeyaml.SYObjectNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

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
        return dump.toString();
    }

    private void dumpNode(Node node, StringBuilder dump, int depth)
    {
        if (node instanceof ObjectNode)
        {
            dumpObject((ObjectNode) node, dump, depth);
        }
        else if (node instanceof StringNode)
        {
            dumpString((StringNode) node, dump, depth);
        }
        else if (node instanceof SYNullNode)
        {
            dumpNullNode(dump);
        }
        else
        {
            throw new RuntimeException("Unsupported node type: " + node.getClass().getSimpleName());
        }
    }

    private void dumpNullNode(StringBuilder dump)
    {
        dump.append(START_MAP).append(END_MAP).append(COMMA_SEP);
    }

    private void dumpString(StringNode node, StringBuilder dump, int depth)
    {
        dump.append(sanitizeScalarValue(node.getValue())).append(COMMA_SEP);
    }

    private void dumpObject(ObjectNode objectNode, StringBuilder dump, int depth)
    {
        List<KeyValueNode> resourceNodes = new ArrayList<>();
        List<KeyValueNode> methodNodes = new ArrayList<>();

        dump.append(START_MAP).append(NEWLINE);

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
        dump.append(addNewline(dump) + indent(depth) + END_MAP + NEWLINE);
    }

    private void dumpCustomArrayIfPresent(StringBuilder dump, int depth, List<KeyValueNode> keyValueNodes, String key, String innerKey)
    {
        if (!keyValueNodes.isEmpty())
        {
            dump.append(addNewline(dump) + indent(depth)).append(sanitizeScalarValue(key))
                .append(COLON_SEP).append(START_ARRAY).append(NEWLINE).append(indent(depth + 1));

            for (KeyValueNode node : keyValueNodes)
            {
                Node copy = copy(node.getValue());
                if (copy instanceof SYNullNode)
                {
                    copy = new SYObjectNode(new MappingNode(Tag.MAP, new ArrayList<NodeTuple>(), null));
                }
                copy.insertChild(0, new KeyValueNodeImpl(new StringNodeImpl(innerKey), node.getKey()));
                dumpObject((ObjectNode) copy, dump, depth + 1);
            }

            dump.append(addNewline(dump) + indent(depth) + END_ARRAY + COMMA_SEP);
        }
    }

    private void dumpKeyValueNode(StringBuilder dump, int depth, KeyValueNode node)
    {
        // key
        KeyValueNode keyValueNode = node;
        String keyText = sanitizeScalarValue(((StringNode) keyValueNode.getKey()).getValue());
        dump.append(addNewline(dump) + indent(depth + 1)).append(keyText).append(COLON_SEP);

        // value
        dumpNode(keyValueNode.getValue(), dump, depth + 1);
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
