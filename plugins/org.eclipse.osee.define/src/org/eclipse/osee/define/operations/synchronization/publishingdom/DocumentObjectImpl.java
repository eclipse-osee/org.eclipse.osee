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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierFactory;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierFactoryType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;

/**
 * An implementation of the {@link Node} interface for a {@link Node} that represents an object within a document
 * (Synchronization Artifact Spec Object).
 *
 * @author Loren K. Ashley
 */

class DocumentObjectImpl extends AbstractHierarchicalNode implements DocumentObject {

   /**
    * Map of the {@link DocumentObjectImpl}'s {@link AttributeDefinitionImpl}s by name.
    */

   private final Map<String, AttributeDefinitionImpl> attributeDefinitionByAttributeDefinitionNameMap;

   /**
    * The {@link DocumentImpl} name.
    */

   private final String name;

   /**
    * A description of the {@link Node} typeDescription.
    */

   private final String typeDescription;

   /**
    * Creates a new unattached {@link DocumentObjectImpl} {@link Node}.
    *
    * @param documentMap the {@link DocumentMap} this DOM node belongs to.
    * @param identifier the {@link Identifier} for the {@link Node}.
    * @param name the {@link DocumentObject} name.
    * @param typeDescription a description of the {@link DocumentObject} type.
    */

   DocumentObjectImpl(DocumentMap documentMap, Identifier identifier, String name, String typeDescription) {

      super(documentMap, identifier);

      //@formatter:off
      assert   identifier.isInGroup( IdentifierTypeGroup.SUBORDINATE_OBJECT )
             : "DocumentObjectImpl::new, parameter \"identifier\" is not of the type \"IdentifierType.SPEC_OBJECT\", \"Identifier.SPECTER_SPEC_OBJECT\", or \"Identifier.SPEC_RELATION\".";

      assert   Objects.nonNull( name )
             : "DocumentObjectImpl::new, parameter \"name\" cannot be null.";

      assert   Objects.nonNull( typeDescription )
             : "DocumentObjectImpl::new, parameter \"typeDescription\" cannot be null.";
      //@formatter:on

      this.name = name;
      this.typeDescription = typeDescription;

      this.attributeDefinitionByAttributeDefinitionNameMap = new HashMap<>();
   }

   /**
    * {@inheritDoc}
    * <p>
    * When the <code>childNode</code> is an {@link AttributeDefinitionImpl}, it is saved in a map of
    * {@link AttributeDefinitionImpl}s by name.
    *
    * @throws IllegalStateException {@inheritDoc}
    */

   @Override
   void append(Node childNode) {

      super.append(childNode);

      //@formatter:off
      assert   childNode.getIdentifier().isInGroup( IdentifierTypeGroup.SUBORDINATE_OBJECT )
             : "DocumentObjectImpl::append, parameter \"childNode\" is not of the type \"IdentifierType.SPEC_OBJECT\", \"Identifier.SPECTER_SPEC_OBJECT\", or \"Identifier.SPEC_RELATION\".";
      //@formatter:on

      if (!(childNode instanceof AttributeDefinitionImpl)) {
         return;
      }

      var attributeDefinition = (AttributeDefinitionImpl) childNode;

      //@formatter:off
      this.attributeDefinitionByAttributeDefinitionNameMap.put
         (
            attributeDefinition.getName(),
            attributeDefinition
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getTypeDescription() {
      return this.typeDescription;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getPrimaryAttributeValue() {

      var primaryAttributeDefinition = this.attributeDefinitionByAttributeDefinitionNameMap.get("Primary Attribute");

      if (Objects.isNull(primaryAttributeDefinition)) {

         return Optional.empty();
      }

      var primaryAttributeDefinitionIdentifier = primaryAttributeDefinition.getIdentifier();

      if (this.attributeValueMap.size(primaryAttributeDefinitionIdentifier) != 1) {

         return Optional.empty();
      }

      //@formatter:off
      var attributeValue =
         this.attributeValueMap.get
            (

              this.attributeValueMap
                 .streamKeySets( primaryAttributeDefinitionIdentifier )
                 .findFirst()
                 .get()

            ).get();
      //@formatter:on

      var identifierString = attributeValue.getValue();

      var identifierFactory = new IdentifierFactory(IdentifierFactoryType.PATTERN_MATCHING);

      var identifier = identifierFactory.createIdentifier(IdentifierType.ATTRIBUTE_DEFINITION, identifierString);

      var thePrimaryAttributeDefinition = this.attributeDefinitionMap.get(identifier);

      if (Objects.isNull(thePrimaryAttributeDefinition)) {

         return Optional.empty();
      }

      var thePrimaryAttributeDefinitionIdentifier = thePrimaryAttributeDefinition.getIdentifier();

      if (this.attributeValueMap.size(thePrimaryAttributeDefinitionIdentifier) != 1) {

         return Optional.empty();
      }

      //@formatter:off
      var thePrimaryAttributeValue =
         this.attributeValueMap.get
            (

               this.attributeValueMap
                  .streamKeySets( thePrimaryAttributeDefinitionIdentifier )
                  .findFirst()
                  .get()

            ).get();
      //@formatter:on

      var value = thePrimaryAttributeValue.getValue();

      return Optional.of(value);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<String> getNameAttributeValue() {

      var nameAttributeDefinition = this.attributeDefinitionByAttributeDefinitionNameMap.get("Name");

      if (Objects.isNull(nameAttributeDefinition)) {

         return Optional.empty();
      }

      var nameAttributeDefinitionIdentifier = nameAttributeDefinition.getIdentifier();

      if (this.attributeValueMap.size(nameAttributeDefinitionIdentifier) != 1) {

         return Optional.empty();
      }

      //@formatter:off
      var nameAttributeValue =
         this.attributeValueMap.get
            (

               this.attributeValueMap
                  .streamKeySets( nameAttributeDefinitionIdentifier )
                  .findFirst()
                  .get()

            ).get();
      //@formatter:on

      var value = nameAttributeValue.getValue();

      return Optional.of(value);
   }

}

/* EOF */
