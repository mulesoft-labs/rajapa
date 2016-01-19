package org.raml.utils;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.RamlNode;
import org.raml.nodes.RamlScalarNode;

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

    public String dump(RamlNode node)
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
        Collection<RamlNode> children = node.getChildren();
        for (RamlNode child : children)
        {
            dump(child);
        }
        dedent();
        return dump.toString();
    }

    private void dumpNode(RamlNode node)
    {

        dump.append(node.getClass().getSimpleName());
        if (node instanceof RamlScalarNode)
        {
            dump.append(": \"").append(((RamlScalarNode) node).getValue()).append("\"");
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
