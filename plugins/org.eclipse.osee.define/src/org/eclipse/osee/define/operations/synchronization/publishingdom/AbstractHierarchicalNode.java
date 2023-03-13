/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.synchronization.publishingdom;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * This package private class implements the common parts for {@link Node} implementations that require hierarchical
 * information.
 *
 * @author Loren K. Ashley
 */

class AbstractHierarchicalNode extends AbstractNode implements HierarchicalNode, HierarchicalNodeSetter {

   /**
    * An array containing the numerical hierarchical position of the {@link DocumentImpl}. The number of entries in the
    * array indicates the hierarchical depth of the {@link Node}. The value in each index of the array indicates the
    * sequence position of the {@link Node} parent at the hierarchical level indicated by the array index.
    */

   private int[] hierarchyLevels;

   /**
    * A {@link String} representation of the {@link #hierarchyLevels} array.
    */

   private String hierarchyLevelsString;

   /**
    * An ordered map of the {@link Node}'s hierarchical children {@link Node}s.
    */

   protected final Map<Identifier, HierarchicalNode> hierarchicalNodeMap;

   /**
    * Creates a new unattached {@link AbstractHierarchicalNode} with the specified {@link Identifier}.
    *
    * @param documentMap the {@link DocumentMap} this DOM node belongs to.
    * @param nodeIdentifier the {@link Identifier} for the {@link AbstractNode}.
    * @throws NullPointerException when the parameter <code>identifier</code> is <code>null</code>.
    */

   AbstractHierarchicalNode(DocumentMap documentMap, Identifier nodeIdentifier) {

      super(documentMap, nodeIdentifier);

      this.hierarchyLevels = null;
      this.hierarchyLevelsString = null;
      this.hierarchicalNodeMap = new LinkedHashMap<>(64, 0.75f);
   }

   @Override
   void append(Node childNode) {

      if (!(childNode instanceof AbstractHierarchicalNode)) {
         super.append(childNode);
         return;
      }

      //@formatter:off
      assert   Objects.nonNull( childNode )
             : "AbstractHierarchicalNode:append, parameter \"childNode\" is null.";

      assert   !this.hierarchicalNodeMap.containsKey( childNode.getIdentifier() )
             : new Message()
                      .title( "AbstractHierarchicalNode::append, AbstractHierarchicalNode already has a child with the \"childNode\" parameter's identifier." )
                      .indentInc()
                      .segment( "AbstractNode Identifier", childNode.getIdentifier() )
                      .toString();
      //@formatter:off

      if( !this.isAttached() ) {
         throw new IllegalStateException();
      }

      if( !childNode.isAttached() ) {
         throw new IllegalStateException();
      }

      this.hierarchicalNodeMap.put(childNode.getIdentifier(), (HierarchicalNode) childNode);
   }




   /**
    * {@inheritDoc}
    */

   @Override
   public int getHierarchyLevel() {

      return Objects.nonNull(this.hierarchyLevels) ? this.hierarchyLevels.length : -1;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hierarchicalSize() {
      return this.hierarchicalNodeMap.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setHierarchyLevels(int[] hierarchyLevels) {

      Objects.requireNonNull(hierarchyLevels,
         "DocumentImpl:setHierarchyLevels, parameter \"hierarchyLevels\" cannot be null.");

      if (Objects.nonNull(this.hierarchyLevels)) {
         throw new IllegalStateException(
            "Object::setHierarchyLevels, member \"hierarchyLevels\" has already been set.");
      }

      this.hierarchyLevels = hierarchyLevels;
      this.hierarchyLevelsString =
         Arrays.stream(this.hierarchyLevels).map((level) -> level + 1).mapToObj(Integer::toString).collect(
            Collectors.joining("."));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<HierarchicalNode> streamHierarchicalChildren() {
      return this.hierarchicalNodeMap.values().stream();
   }

   /**
    * Gets a {@link String} representation of the {@link #hierarchyLevels} array.
    *
    * @return the hierarchy level string.
    */

   @Override
   public String getHierarchyLevelString() {
      return this.hierarchyLevelsString;
   }

   @Override
   public int[] getHierarchyLevels() {
      return Arrays.copyOf(this.hierarchyLevels, this.hierarchyLevels.length);
   }

   @Override
   public boolean isHierarchical() {
      return true;
   }

}

/* EOF */
