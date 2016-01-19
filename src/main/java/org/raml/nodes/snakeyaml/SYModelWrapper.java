package org.raml.nodes.snakeyaml;

import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;
import static org.yaml.snakeyaml.nodes.NodeId.sequence;

import org.raml.nodes.impl.RamlKeyValueNodeImpl;
import org.raml.nodes.RamlNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SYModelWrapper
{

    public static final Tag INCLUDE_TAG = new Tag("!include");

    public RamlNode wrap(Node node)
    {
        if (node.getNodeId() == mapping)
        {
            return wrap((MappingNode) node);
        }
        if (node.getNodeId() == sequence)
        {
            return wrap((SequenceNode) node);
        }
        if (node.getNodeId() == scalar)
        {
            return wrap((ScalarNode) node);
        }
        else
        {
            throw new IllegalStateException("Invalid node type");
        }
    }

    private SYMappingNodeImpl wrap(MappingNode mappingNode)
    {
        SYMappingNodeImpl mapping = new SYMappingNodeImpl(mappingNode);
        for (NodeTuple nodeTuple : mappingNode.getValue())
        {
            RamlNode key = wrap(nodeTuple.getKeyNode());
            RamlNode value = wrap(nodeTuple.getValueNode());
            RamlKeyValueNodeImpl keyValue = new RamlKeyValueNodeImpl(key, value);
            mapping.addChild(keyValue);
        }
        return mapping;
    }

    private SYScalarNode wrap(ScalarNode scalarNode)
    {
        if (INCLUDE_TAG.equals(scalarNode.getTag()))
        {
            return new SYIncludeNode(scalarNode);
        }
        return new SYScalarNode(scalarNode);
    }

    private SYSequenceNode wrap(SequenceNode sequenceNode)
    {
        SYSequenceNode sequence = new SYSequenceNode(sequenceNode);
        for (Node node : sequenceNode.getValue())
        {
            sequence.addChild(wrap(node));
        }
        return sequence;
    }
}
