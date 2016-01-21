/*
 *
 */
package org.raml.grammar.rule;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MappingRule extends Rule
{


    private List<KeyValueRule> fields;

    public MappingRule()
    {

        this.fields = new ArrayList<>();
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof ObjectNode;
    }

    @Override
    public Node transform(Node node)
    {
        Node result = node;
        if (getFactory() != null)
        {
            result = getFactory().create();
        }
        final List<Node> children = node.getChildren();
        for (Node child : children)
        {
            final Rule matchingRule = findMatchingRule(fields, child);
            if (matchingRule != null)
            {
                final Node newChild = matchingRule.transform(child);
                child.replaceWith(newChild);
            }
            else
            {
                final Collection<String> options = Collections2.transform(fields, new Function<KeyValueRule, String>()
                {
                    @Override
                    public String apply(KeyValueRule rule)
                    {
                        return rule.getKeyRule().getDescription();
                    }
                });
                child.replaceWith(ErrorNodeFactory.createUnexpectedKey(((KeyValueNode) child).getKey(), options));
            }
        }

        return result;
    }

    @Nullable
    private Rule findMatchingRule(List<? extends Rule> rootRule, Node node)
    {
        for (Rule rule : rootRule)
        {
            if (rule.matches(node))
            {
                return rule;
            }
        }
        return null;
    }


    public MappingRule with(KeyValueRule field)
    {
        this.fields.add(field);
        return this;
    }

    @Override
    public String getDescription()
    {
        return "Mapping";
    }
}
