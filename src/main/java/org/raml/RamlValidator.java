/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.emitter.tck.TckEmitter;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;

public class RamlValidator
{

    public static final String USAGE = "Arguments: [-dump] file|url";

    public static void main(String[] args) throws IOException
    {
        Arguments arguments = parseArguments(args);

        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(new File(arguments.ramlLocation));

        List<ErrorNode> errors = raml.findDescendantsWith(ErrorNode.class);
        if (!errors.isEmpty())
        {
            logErrors(errors);
            return;
        }

        if (arguments.dump)
        {
            String json = new TckEmitter().dump(raml);
            System.out.println(StringUtils.repeat("=", 120));
            System.out.println(json);
            System.out.println(StringUtils.repeat("=", 120));
        }
        else
        {
            System.out.println("No errors found.");
        }
    }

    private static void logErrors(List<ErrorNode> errors)
    {
        String label = errors.size() > 1 ? "errors" : "error";
        System.out.format("%d %s found:\n\n", errors.size(), label);
        for (ErrorNode error : errors)
        {
            String message = error.getErrorMessage();
            int idx = message.indexOf(". Options are");
            if (idx != -1)
            {
                message = message.substring(0, idx);
            }
            System.out.format("\t- %s %s\n\n", message, error.getSource().getStartPosition());
        }
    }

    private static Arguments parseArguments(String[] args)
    {
        boolean dump = false;
        String ramlLocation;
        if (args.length < 1 || args.length > 2)
        {
            throw new IllegalArgumentException(USAGE);
        }
        if (args.length == 2)
        {
            if (!"-dump".equals(args[0]))
            {
                throw new IllegalArgumentException(USAGE);
            }
            dump = true;
            ramlLocation = args[1];
        }
        else
        {
            ramlLocation = args[0];
        }
        return new Arguments(dump, ramlLocation);
    }

    private static class Arguments
    {
        boolean dump;
        String ramlLocation;

        public Arguments(boolean dump, String ramlLocation)
        {
            this.dump = dump;
            this.ramlLocation = ramlLocation;
        }
    }
}
