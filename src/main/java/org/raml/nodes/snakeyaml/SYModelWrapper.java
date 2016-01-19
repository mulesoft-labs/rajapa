package org.raml.nodes.snakeyaml;

import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;
import static org.yaml.snakeyaml.nodes.NodeId.sequence;

import org.raml.nodes.impl.RamlKeyValueNodeImpl;
import org.raml.nodes.Node;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SYModelWrapper
{

    public static final Tag INCLUDE_TAG = new Tag("!include");

    public Node wrap(org.yaml.snakeyaml.nodes.Node node)
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

    private SYMappingNode wrap(MappingNode mappingNode)
    {
        SYMappingNode mapping = new SYMappingNode(mappingNode);
        for (NodeTuple nodeTuple : mappingNode.getValue())
        {
            Node key = wrap(nodeTuple.getKeyNode());
            Node value = wrap(nodeTuple.getValueNode());
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
        for (org.yaml.snakeyaml.nodes.Node node : sequenceNode.getValue())
        {
            sequence.addChild(wrap(node));
        }
        return sequence;
    }
}
