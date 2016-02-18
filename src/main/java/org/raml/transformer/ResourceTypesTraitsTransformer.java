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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.grammar.GrammarPhase;
import org.raml.grammar.Raml10Grammar;
import org.raml.nodes.ExecutionContext;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.ParametrizedReferenceNode;
import org.raml.nodes.ReferenceNode;
import org.raml.nodes.impl.MethodNode;
import org.raml.nodes.impl.ResourceNode;
import org.raml.nodes.impl.ResourceTypeNode;
import org.raml.nodes.impl.ResourceTypeRefNode;
import org.raml.nodes.impl.StringTemplateNode;
import org.raml.nodes.impl.TraitNode;
import org.raml.nodes.impl.TraitRefNode;
import org.raml.nodes.snakeyaml.SYBaseRamlNode;
import org.raml.nodes.snakeyaml.SYNullNode;
import org.raml.nodes.snakeyaml.SYObjectNode;
import org.raml.utils.NodeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypesTraitsTransformer implements Transformer
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
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
        if (mergedResources.contains(resourceNode))
        {
            return node;
        }

        // apply method and resource traits if defined
        checkTraits(resourceNode);

        // apply resource type if defined
        ResourceTypeRefNode resourceTypeReference = findResourceTypeReference(resourceNode);
        if (resourceTypeReference != null)
        {
            applyResourceType(resourceNode, resourceTypeReference);
        }

        mergedResources.add(resourceNode);
        return node;
    }

    private void checkTraits(KeyValueNode resourceNode)
    {
        List<MethodNode> methodNodes = findMethodNodes(resourceNode);
        List<TraitRefNode> resourceTraitRefs = findTraitReferences(resourceNode);

        for (MethodNode methodNode : methodNodes)
        {
            List<TraitRefNode> traitRefs = findTraitReferences(methodNode);
            traitRefs.addAll(resourceTraitRefs);
            for (TraitRefNode traitRef : traitRefs)
            {
                String traitLevel = resourceTraitRefs.contains(traitRef) ? "resource" : "method";
                logger.info("applying {} level trait '{}' to '{}.{}'", traitLevel, traitRef.getRefName(), resourceNode.getKey(), methodNode.getName());
                applyTrait(methodNode, traitRef);
            }
        }
    }

    private void applyResourceType(KeyValueNode resourceNode, ResourceTypeRefNode resourceTypeReference)
    {
        ResourceTypeNode refNode = resourceTypeReference.getRefNode();
        ResourceTypeNode templateNode = (ResourceTypeNode) refNode.copy();
        templateNode.setParent(refNode.getParent());

        // resolve parameters
        if (resourceTypeReference instanceof ParametrizedReferenceNode)
        {
            resolveParameters(templateNode, ((ParametrizedReferenceNode) resourceTypeReference).getParameters());
        }

        // apply grammar phase to generate method nodes
        GrammarPhase parseMethodsPhase = new GrammarPhase(new Raml10Grammar().resourceType());
        parseMethodsPhase.apply(templateNode.getValue());

        // apply traits
        checkTraits(templateNode);

        // resolve inheritance
        ResourceTypeRefNode parentTypeReference = findResourceTypeReference(templateNode);
        if (parentTypeReference != null)
        {
            applyResourceType(templateNode, parentTypeReference);
        }

        merge(resourceNode.getValue(), templateNode.getValue());
    }

    private void applyTrait(MethodNode methodNode, TraitRefNode traitReference)
    {
        TraitNode refNode = traitReference.getRefNode();
        if (refNode == null)
        {
            throw new RuntimeException("validated before?");
        }

        TraitNode copy = (TraitNode) refNode.copy();

        // resolve parameters
        if (traitReference instanceof ParametrizedReferenceNode)
        {
            resolveParameters(copy, ((ParametrizedReferenceNode) traitReference).getParameters());
        }

        replaceNullValueWithObject(methodNode);
        merge(methodNode.getValue(), copy.getValue());
    }

    private void resolveParameters(Node parameterizedNode, Map<String, String> parameters)
    {
        ExecutionContext context = new ExecutionContext(parameters);
        List<StringTemplateNode> templateNodes = parameterizedNode.findDescendantsWith(StringTemplateNode.class);
        for (StringTemplateNode templateNode : templateNodes)
        {
            Node resolvedNode = templateNode.execute(context);
            templateNode.replaceWith(resolvedNode);
        }
    }

    private void replaceNullValueWithObject(KeyValueNode keyValueNode)
    {
        Node valueNode = keyValueNode.getValue();
        if (valueNode instanceof SYNullNode)
        {
            valueNode = new SYObjectNode((SYBaseRamlNode) valueNode);
            keyValueNode.setValue(valueNode);
        }
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

    private ResourceTypeRefNode findResourceTypeReference(KeyValueNode resourceNode)
    {
        return (ResourceTypeRefNode) NodeSelector.selectFrom("type", resourceNode.getValue());
    }

    private List<MethodNode> findMethodNodes(KeyValueNode resourceNode)
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
