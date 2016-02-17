package org.raml.types.factories;

import org.raml.grammar.rule.NodeFactory;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;
import org.raml.types.builtin.BooleanTypeNode;
import org.raml.types.builtin.NumericTypeNode;
import org.raml.types.builtin.ObjectTypeNode;
import org.raml.types.builtin.StringTypeNode;
import org.raml.utils.NodeSelector;

public class TypeNodeFactory implements NodeFactory
{

    @Override
    public Node create(Object... args)
    {
        StringNode type = (StringNode) NodeSelector.selectFrom("type", (Node) args[0]);
        String value = type.getValue();
        switch (value){
            case "object" :
                return new ObjectTypeNode();
            case "string" :
                return new StringTypeNode();
            case "number": case "integer":
                return new NumericTypeNode();
            case "boolean":
                return new BooleanTypeNode();
            default:
                return new StringTypeNode();
        }
    }
}
