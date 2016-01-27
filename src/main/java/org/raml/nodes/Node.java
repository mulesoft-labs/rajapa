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
package org.raml.nodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import org.raml.nodes.impl.KeyValueNodeImpl;

public interface Node
{

    Position getStartPosition();

    Position getEndPosition();

    Node getRootNode();

    @Nullable
    Node getParent();

    List<Node> getChildren();

    void addChild(Node node);

    void setParent(Node parent);

    void setSource(Node source);

    /**
     * Returns the list of descendants nodes that is instance of with the specified class
     * @param nodeType The class that the node should implement
     * @param <T> The type of the class
     * @return The matching types
     */
    @Nonnull
    <T extends Node> List<T> findDescendantsWith(Class<T> nodeType);


    @Nullable
    Node getSource();

    void replaceWith(Node newNode);

    void setChild(int idx, Node newNode);

    void insertChild(int idx, Node newNode);
}
