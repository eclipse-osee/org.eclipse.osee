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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.framework.jdk.core.util.RankLinkedHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;

/**
 * This package private class implements the common parts of the mark down publishing model data types.
 *
 * @author Loren K. Ashley
 */

class AbstractNode implements Node {

   /**
    * An ordered map of the {@link Node}'s attribute values {@link Node}s.
    */

   protected final RankMap<AttributeValueImpl> attributeValueMap;

   /**
    * An ordered map of the {@link Node}'s attribute definition {@link Node}s.
    */

   protected final Map<Identifier, AttributeDefinitionImpl> attributeDefinitionMap;

   /**
    * The {@link DocumentMapImpl} the {@link Node} has been attached to. A value of <code>null</code> indicates the
    * {@link Node} has not yet been attached.
    */

   protected DocumentMap documentMap;

   /**
    * Saves the {@link AbstractNode}'s {@link Identifier}. Each {@link AbstractNode} in the document model is required
    * to have a unique {@link Identifier}.
    */

   protected final Identifier nodeIdentifier;

   /**
    * The {@link Identifier} of this {@link Node}'s parent.
    */

   protected Identifier parentIdentifier;

   //@formatter:off
   @SuppressWarnings("unchecked")
   private static Predicate<Object> keysAreIdentifiers[] = new Predicate[]
      {
         ( key ) -> Objects.nonNull( key ) && ( key instanceof Identifier ) && ( (Identifier) key).isType( IdentifierType.ATTRIBUTE_DEFINITION ),
         ( key ) -> Objects.nonNull( key ) && ( key instanceof Identifier ) && ( (Identifier) key).isType( IdentifierType.ATTRIBUTE_VALUE )
      };
   //@formatter:on

   /**
    * Creates a new unattached {@link AbstractNode} with the specified {@link Identifier}.
    *
    * @param documentMap the {@link DocumentMap} this DOM node belongs to.
    * @param nodeIdentifier the {@link Identifier} for the {@link AbstractNode}.
    * @throws NullPointerException when the parameter <code>identifier</code> is <code>null</code>.
    */

   AbstractNode(DocumentMap documentMap, Identifier nodeIdentifier) {

      //@formatter:off
      assert   Objects.nonNull( documentMap )
             : "AbstractNode::new, parameter \"documentMap\" cannot be null.";

      assert   Objects.nonNull( nodeIdentifier )
             : "AbstractNode::new, parameter \"nodeIdentifier\" cannot be null.";
      //@formatter:on

      this.documentMap = documentMap;
      this.parentIdentifier = null;
      this.nodeIdentifier = nodeIdentifier;
      this.attributeDefinitionMap = new LinkedHashMap<>(32, 0.75f);
      this.attributeValueMap = new RankLinkedHashMap<>("attributeMap", 2, 32, 0.75f, AbstractNode.keysAreIdentifiers);
   }

   /**
    * When the <code>childNode</code> is of the same class as this {@link Node}, it is added as the last hierarchical
    * child; otherwise, it is added the last direct child.
    *
    * @param childNode the {@link Node} to be added as the last child.
    * @throws IllegalStateException when the {@link Node} is unattached or the <code>childNode</code> is unattached.
    */

   void append(Node childNode) {
      //@formatter:off
      assert   Objects.nonNull( childNode )
             : "AbstractNode:append, parameter \"childNode\" is null.";
      //@formatter:off

      if( !this.isAttached() ) {
         throw new IllegalStateException();
      }

      if( !childNode.isAttached() ) {
         throw new IllegalStateException();
      }

      if( childNode.isType( IdentifierType.ATTRIBUTE_DEFINITION ) ) {
         //@formatter:off
         assert   ( childNode instanceof AttributeDefinitionImpl )
                : "AbstractNode:apend, parameter \"childNode\" has an identifier type of \"ATTRIBUTE_DEFINITION\" and is not of class \"AttributeDefinitionImpl\".";
         //@formatter:on

         var attributeDefinition = (AttributeDefinitionImpl) childNode;

         this.attributeDefinitionMap.put(attributeDefinition.getIdentifier(), attributeDefinition);

         return;
      }

      if (childNode.isType(IdentifierType.ATTRIBUTE_VALUE)) {
         //@formatter:off
         assert   ( childNode instanceof AttributeValueImpl )
                : "AbstractNode:apend, parameter \"childNode\" has an identifier type of \"ATTRIBUTE_VALUE\" and is not of class \"AttributeValueImpl\".";
         //@formatter:on

         var attributeValue = (AttributeValueImpl) childNode;

         this.attributeValueMap.associate(attributeValue, attributeValue.getParentIdentifier(),
            attributeValue.getIdentifier());

         return;
      }

   }

   /**
    * Attaches this {@link Node} to a parent {@link Node}.
    *
    * @param parentIdentifier the {@link Identifier} of the {@link Node} that is to be the parent of this {@link Node}.
    * @throws IllegalStateException when the {@link Node} is already attached.
    */

   void attach(Identifier parentIdentifier) {

      //@formatter:off
      assert   Objects.nonNull( parentIdentifier )
             : "AbstractNode::new, parameter \"parentIdentifier\" cannot be null.";
      //@formatter:on

      if (!(this instanceof AttributeDefinitionImpl) && this.isAttached()) {
         throw new IllegalStateException();
      }

      this.parentIdentifier = parentIdentifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int attributeDefinitionSize() {
      return this.attributeDefinitionMap.size();
   }

   /**
    * {@inheritDoc
    */

   @Override
   public Identifier getIdentifier() {
      return this.nodeIdentifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Identifier getParentIdentifier() {
      return this.parentIdentifier;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IdentifierType getType() {
      return this.nodeIdentifier.getType();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isAttached() {
      return Objects.nonNull(this.parentIdentifier);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isType(IdentifierType identifierType) {
      return this.nodeIdentifier.isType(identifierType);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<AttributeDefinitionImpl> streamAttributeDefinitionChildren() {
      return this.attributeDefinitionMap.values().stream();
   }

   @Override
   public boolean isHierarchical() {
      return false;
   }

}

/* EOF */
