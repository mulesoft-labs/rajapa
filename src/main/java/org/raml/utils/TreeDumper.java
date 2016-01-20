package org.raml.utils;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;
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
        dump.append("\n");
        if (node.getSource() != null)
        {
            indent();
            printIndent();
            dump.append("<Source>: ");
            indent();
            dump.append("\n");
            dump(node.getSource());
            dedent();
            dedent();
        }
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
