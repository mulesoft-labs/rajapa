/*
 *
 */
package org.raml.nodes;

import javax.annotation.Nullable;
import java.util.Map;

public class ExecutionContext
{

    private Map<String, String> parameters;

    public ExecutionContext(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }

    public void addVariable(String name, String value)
    {
        parameters.put(name, value);
    }

    @Nullable
    public String getVariable(String name)
    {
        return parameters.get(name);
    }

    public boolean containsVariable(String variable)
    {
        return parameters.containsKey(variable);
    }
}
