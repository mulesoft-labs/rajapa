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
/*
 *
 */
package org.raml.transformer;

import static org.raml.transformer.ResourceTypesTraitsMerger.merge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.ReferenceNode;
import org.raml.nodes.impl.MethodNode;
import org.raml.nodes.impl.ResourceNode;
import org.raml.nodes.impl.ResourceTypeRefNode;
import org.raml.nodes.impl.TraitNode;
import org.raml.nodes.impl.TraitRefNode;
import org.raml.utils.NodeSelector;

public class ResourceTypesTraitsTransformer implements Transformer
{

    private Set<ResourceNode> mergedResources = new HashSet<>();

    @Override
    public boolean matches(Node node)
    {
        return node instanceof TraitRefNode ||
               node instanceof ResourceTypeRefNode;
    }

    @Override
    public Node transform(Node node)
    {
        ResourceNode resourceNode = findResourceNode((ReferenceNode) node);
        List<MethodNode> methodNodes = findMethodNodes(resourceNode);

        // apply method traits
        for (MethodNode methodNode : methodNodes)
        {
            List<TraitRefNode> traitRefs = findTraitReferences(methodNode);
            for (TraitRefNode traitRef : traitRefs)
            {
                System.out.println("applying trait = " + traitRef.getRefName());
                applyTrait(methodNode, traitRef);
            }
        }

        // TODO:
        // apply resource traits
        // apply resource type

        return node;
    }

    private void applyTrait(MethodNode methodNode, TraitRefNode traitRef)
    {
        TraitNode refNode = traitRef.getRefNode();
        if (refNode == null)
        {
            throw new RuntimeException("validated before?");
        }

        // TODO resolve parameters
        TraitNode copy = (TraitNode) refNode.copy();

        merge(methodNode.getValue(), copy.getValue());
    }

    private List<TraitRefNode> findTraitReferences(KeyValueNode keyValueNode)
    {
        List<TraitRefNode> result = new ArrayList<>();
        Node isNode = NodeSelector.selectFrom("is", keyValueNode.getValue());
        if (isNode != null)
        {
            List<Node> children = isNode.getChildren();
            for (Node child : children)
            {
                result.add((TraitRefNode) child);
            }
        }
        return result;
    }

    private List<MethodNode> findMethodNodes(ResourceNode resourceNode)
    {
        List<MethodNode> methodNodes = new ArrayList<>();
        for (Node node : resourceNode.getValue().getChildren())
        {
            if (node instanceof MethodNode)
            {
                methodNodes.add((MethodNode) node);
            }
        }
        return methodNodes;
    }

    private ResourceNode findResourceNode(ReferenceNode referenceNode)
    {
        // "type" at resource level
        Node parent = NodeSelector.selectFrom("../../..", referenceNode);
        if (parent instanceof ResourceNode)
        {
            return (ResourceNode) parent;
        }

        // "is" at resource level
        parent = NodeSelector.selectFrom("../../../..", referenceNode);
        if (parent instanceof ResourceNode)
        {
            return (ResourceNode) parent;
        }

        // "is" at method level
        parent = NodeSelector.selectFrom("../../../../../..", referenceNode);
        if (parent instanceof ResourceNode)
        {
            return (ResourceNode) parent;
        }
        throw new RuntimeException("Unreachable code: invalid node hierarchy");
    }

}
