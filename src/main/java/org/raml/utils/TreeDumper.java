package org.raml.utils;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
import org.raml.nodes.ReferenceNode;
import org.raml.nodes.StringNode;


public class TreeDumper
{

    public static final int TAB_SPACES = 4;
    private StringBuilder dump;
    private int indent = 0;

    public TreeDumper(StringBuilder dump)
    {
        this.dump = dump;
    }

    public TreeDumper()
    {
        this(new StringBuilder());
    }

    public String dump(Node node)
    {
        printIndent();
        dumpNode(node);
        dump.append(" (");
        if (node.getStartPosition() != null)
        {
            dump.append("Start: ").append(node.getStartPosition().getIndex());
        }

        if (node.getEndPosition() != null)
        {
            dump.append(" , End: ").append(node.getEndPosition().getIndex());
        }
        if (node.getSource() != null)
        {
            dump.append(", Source: ");
            dump.append(node.getSource().getClass().getSimpleName());
        }
        dump.append(")");
        dump.append("\n");
        indent();
        Collection<Node> children = node.getChildren();
        for (Node child : children)
        {
            dump(child);
        }
        dedent();
        return dump.toString();
    }

    private void dumpNode(Node node)
    {

        dump.append(node.getClass().getSimpleName());
        if (node instanceof StringNode)
        {
            dump.append(": \"").append(((StringNode) node).getValue()).append("\"");
        }
        else if (node instanceof ErrorNode)
        {
            dump.append(": \"").append(((ErrorNode) node).getErrorMessage()).append("\"");
        }
        else if (node instanceof ReferenceNode)
        {
            final Node refNode = ((ReferenceNode) node).getRefNode();
            dump.append(" -> {").append(refNode == null ? "null" : refNode.getClass().getSimpleName());
            if (refNode != null)
            {
                if (refNode.getStartPosition() != null)
                {
                    dump.append(" RefStart: ").append(refNode.getStartPosition().getIndex());
                }

                if (refNode.getEndPosition() != null)
                {
                    dump.append(" , RefEnd: ").append(refNode.getEndPosition().getIndex());
                }
            }
            dump.append("}");
        }
    }

    private void dedent()
    {
        indent--;
    }

    private void indent()
    {
        indent++;
    }

    private void printIndent()
    {
        dump.append(StringUtils.repeat(" ", indent * TAB_SPACES));
    }

}
