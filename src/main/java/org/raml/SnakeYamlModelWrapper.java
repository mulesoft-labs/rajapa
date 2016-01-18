package org.raml;

import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;
import static org.yaml.snakeyaml.nodes.NodeId.sequence;

import org.raml.nodes.RamlIncludeNode;
import org.raml.nodes.RamlKeyValueNode;
import org.raml.nodes.RamlMappingNode;
import org.raml.nodes.RamlNode;
import org.raml.nodes.RamlScalarNode;
import org.raml.nodes.RamlSequenceNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SnakeYamlModelWrapper
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

    private RamlMappingNode wrap(MappingNode mappingNode)
    {
        RamlMappingNode mapping = new RamlMappingNode(mappingNode);
        for (NodeTuple nodeTuple : mappingNode.getValue())
        {
            RamlNode key = wrap(nodeTuple.getKeyNode());
            RamlNode value = wrap(nodeTuple.getValueNode());
            RamlKeyValueNode keyValue = new RamlKeyValueNode(key, value);
            mapping.addChild(keyValue);
        }
        return mapping;
    }

    private RamlScalarNode wrap(ScalarNode scalarNode)
    {
        if (INCLUDE_TAG.equals(scalarNode.getTag()))
        {
            return new RamlIncludeNode(scalarNode);
        }
        return new RamlScalarNode(scalarNode);
    }

    private RamlSequenceNode wrap(SequenceNode sequenceNode)
    {
        RamlSequenceNode sequence = new RamlSequenceNode(sequenceNode);
        for (Node node : sequenceNode.getValue())
        {
            sequence.addChild(wrap(node));
        }
        return sequence;
    }
}
