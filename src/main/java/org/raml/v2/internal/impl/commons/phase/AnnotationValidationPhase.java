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
package org.raml.v2.impl.commons.phase;


import java.util.List;

import org.raml.v2.grammar.rule.ErrorNodeFactory;
import org.raml.v2.grammar.rule.Rule;
import org.raml.v2.impl.commons.nodes.AnnotationNode;
import org.raml.v2.impl.commons.nodes.AnnotationTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.phase.Phase;

public class AnnotationValidationPhase implements Phase
{

    @Override
    public Node apply(Node tree)
    {
        final List<AnnotationNode> annotations = tree.findDescendantsWith(AnnotationNode.class);
        for (AnnotationNode annotation : annotations)
        {
            String annotationName = annotation.getName();
            AnnotationTypeNode annotationTypeNode = annotation.getAnnotationTypeNode();
            if (annotationTypeNode == null)
            {
                annotation.replaceWith(ErrorNodeFactory.createMissingAnnotationType(annotationName));
                continue;
            }
            TypeNode typeNode = annotationTypeNode.getTypeNode();
            Rule rule = typeNode.visit(new TypeToRuleVisitor());
            Node value = annotation.getValue();
            Node transform = rule.apply(value);
            value.replaceWith(transform);
        }
        return tree;
    }

}
