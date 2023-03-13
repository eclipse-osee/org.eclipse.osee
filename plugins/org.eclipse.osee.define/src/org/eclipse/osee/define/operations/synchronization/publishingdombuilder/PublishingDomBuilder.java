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

package org.eclipse.osee.define.operations.synchronization.publishingdombuilder;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.naming.OperationNotSupportedException;
import org.eclipse.osee.define.operations.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.RelationshipTerminal;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifact;
import org.eclipse.osee.define.operations.synchronization.SynchronizationArtifactBuilder;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.StreamEntry;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.publishingdom.AttributeDefinition;
import org.eclipse.osee.define.operations.synchronization.publishingdom.AttributeValue;
import org.eclipse.osee.define.operations.synchronization.publishingdom.Document;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentMap;
import org.eclipse.osee.define.operations.synchronization.publishingdom.DocumentObject;
import org.eclipse.osee.define.operations.synchronization.publishingdom.Factory;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.framework.jdk.core.util.EnumConsumerMap;

/**
 * An implementation of the {@link SynchronizationArtifactBuilder} interface for building the general Publishing DOM.
 * This implementation does not support import operations. All importing methods will throw an
 * {@link OperationNotSupportedException}.
 *
 * @author Loren K. Ashley
 */

public class PublishingDomBuilder implements SynchronizationArtifactBuilder {

   /**
    * Map of the {@link Consumer} implementations to be returned by the {@link #getConverter} method.
    */

   private EnumConsumerMap<IdentifierType, GroveThing> converterMap;

   /**
    * Saves the publishing DOM.
    */

   private DocumentMap documentMap;

   /**
    * Save the serializer to be used for serializing the Publishing DOM.
    */

   private Function<DocumentMap, InputStream> serializer;

   /**
    * Saves the {@link SynchronizationArtifact} the Publishing DOM is to be built from.
    */

   private SynchronizationArtifact synchronizationArtifact;

   /**
    * Creates a new implementation of the {@link SynchronizationArtifactBuilder} interface for building the general
    * purpose publishing DOM.
    *
    * @param serializer the serializer to be used for serializing the publishing DOM.
    * @throw NullPointerException when the parameter <code>serializer</code> is <code>null</code>.
    */

   public PublishingDomBuilder(Function<DocumentMap, InputStream> serializer) {

      this.serializer =
         Objects.requireNonNull(serializer, "PublishingDomBuilder::new, parameter \"serializer\" cannot be null.");

      this.documentMap = null;
      this.converterMap = null;
      this.synchronizationArtifact = null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean build() {

      //@formatter:off

      var forest = this.synchronizationArtifact.getForest();

      var specObjectGrove = forest.getGrove( IdentifierType.SPEC_OBJECT );

      /*
       * Synchronization Artifact Specifications
       *
       * Create a new Document from each Specification
       */

      forest.streamGroves
         (
            StreamEntry.create( IdentifierType.SPECIFICATION )
         )
      .forEach
         (
            ( specificationGroveThing ) ->
            {
               var document = (Document) specificationGroveThing.getForeignThing();

               this.documentMap.appendDocumentToDocumentMap( this.documentMap.getIdentifier(), document );
            }
         );

      /*
       * Synchronization Artifact Spec Objects
       *
       * Create a new DocumentObject from each Spec Object and attach them to their containing Documents.
       */

      forest.streamGroves
         (
            StreamEntry.create( IdentifierType.SPECIFICATION )
         )
      .forEach
         (
            ( specificationGroveThing ) ->
            {
               var specificationGroveThingIdentifier = specificationGroveThing.getIdentifier();

               specObjectGrove.streamIdentifiersDeep( specificationGroveThingIdentifier )
                  .filter( ( identifier ) -> identifier.getType().equals( IdentifierType.SPEC_OBJECT ) )
                  .forEach
                     (
                        ( specObjectIdentifier ) ->
                        {
                           var specObjectGroveThing = specObjectGrove.getByUniquePrimaryKey( specObjectIdentifier ).get();

                           var parentGroveThing = specObjectGroveThing.getParent(-1).get();

                           var parentGroveThingIdentifier = parentGroveThing.getIdentifier();

                           var documentObject = (DocumentObject) specObjectGroveThing.getForeignThing();

                           if( parentGroveThingIdentifier.isType( IdentifierType.SPECIFICATION ) ) {
                              this.documentMap.appendDocumentObjectToDocument( parentGroveThingIdentifier, documentObject );
                           } else {
                              this.documentMap.appendDocumentObjectToDocumentObject( parentGroveThingIdentifier, documentObject );
                           }
                        }
                     );
            }
         );

      /*
       * Synchronization Artifact Definition
       *
       * Some OSEE Artifacts contain a PrimaryAttribute which contains the OSEE native identifier for the attribute that contains
       * the content to be published. A Map of the Synchronization Artifact Attribute Definition GroveThing objects is built. The primary key
       * is the parent GroveThing's identifier. The parent of a Synchronization Artifact Attribute Definition is a Synchronization Artifact
       * Specification Type, Spec Object Type, or Spec Relation Type. The secondary key is the Native OSEE identifier for the attribute
       * definition.
       *
       * This map is used to resolve the reference by Native OSEE Attribute Identifier in the PrimaryAttribute.
       */

      DoubleMap<Identifier,Long,GroveThing> attributeDefinitionByParentIdentifierAndNativeIdentifierMap = new DoubleHashMap<>();

      forest.streamGroves
         (
            IdentifierType.ATTRIBUTE_DEFINITION
         )
      .forEach
         (
            ( attributeDefinitionGroveThing ) ->
            {
               var parentGroveThing = attributeDefinitionGroveThing.getParent(-1).get();
               var parentGroveThingIdentifier = parentGroveThing.getIdentifier();
               var nativeAttributeTypeGeneric = (AttributeTypeGeneric<?>) attributeDefinitionGroveThing.getNativeThing();
               var nativeIdentifier = nativeAttributeTypeGeneric.getId();
               attributeDefinitionByParentIdentifierAndNativeIdentifierMap.put(parentGroveThingIdentifier, nativeIdentifier, attributeDefinitionGroveThing );
            }
         );

      /*
       * Synchronization Artifact Attribute Values
       *
       * Stream only the Synchronization Artifact Spec Object Attribute Values and create new AttributeDefinition and AttributeValue object for
       * them. The AttributeDefinition and AttributeValue objects are attached to DocumentObject that corresponds to the Synchronization
       * Artifact Spec Object they were derived from.
       */

      forest.streamGroves
         (
            StreamEntry.create( IdentifierType.ATTRIBUTE_VALUE )
         )
      .filter
         (
            /*
             * Only keep Synchronization Artifact Attribute Values that belong to a Synchronization Artifact Spec Object.
             */

            ( attributeValueGroveThing ) -> attributeValueGroveThing.getParent( -1 ).get().getIdentifier().isType( IdentifierType.SPEC_OBJECT )
         )
      .forEach
         (
            ( attributeValueGroveThing ) ->
            {
               /*
                * Get the Publishing DOM AttributeValue
                */

               var attributeValue = (AttributeValue) attributeValueGroveThing.getForeignThing();

               /*
                * Get the Synchronization Artifact Identifier for the Synchronization Artifact Spec Object Type that is the parent
                * of the Synchronization Artifact Attribute Value.
                */

               var commonObjectGroveThing = attributeValueGroveThing.getParent( -1 ).get();
               var parentIdentifier = commonObjectGroveThing.getIdentifier();

               /*
                * Get the Synchronization Artifact Attribute Definition, the Native OSEE Attribute Definition, and the Publishing DOM
                * Attribute Definition the Synchronization Artifact Attribute Value belongs to.
                */

               var attributeDefinitionGroveThing = attributeValueGroveThing.getLinkScalar( IdentifierType.ATTRIBUTE_DEFINITION ).get();
               var nativeAttributeDefinition     = (AttributeTypeGeneric<?>) attributeDefinitionGroveThing.getNativeThing();
               var attributeDefinition = (AttributeDefinition) attributeDefinitionGroveThing.getForeignThing();

               /*
                * Is the Native OSEE Attribute Definition for the "PrimaryAttribute"?
                */

               if( CoreAttributeTypes.PrimaryAttribute.getId().equals( nativeAttributeDefinition.getId() ) )
               {
                  /*
                   * Get the value of the Native OSEE Primary Attribute. The value is the Native OSEE Attribute Type identifier
                   * of the Native OSEE Attribute Type that will contain the main publishing content.
                   */

                  var nativePrimaryAttributeTypeIdentifier = Long.parseLong( attributeValue.getValue(), 10 );

                  /*
                   * Get the Synchronization Artifact Identifier of the Synchronization Artifact Spec Object the Native OSEE
                   * Attribute Definition is encapsulated by.
                   */

                  var attributeDefinitionGroveThingParent = attributeDefinitionGroveThing.getParent(-1).get();
                  var attributeDefinitionGroveThingParentIdentifier = attributeDefinitionGroveThingParent.getIdentifier();

                  /*
                   * Get the Synchronization Artifact Identifier of the Synchronization Artifact Attribute Definition that
                   * encapsulates the Native OSEE Attribute Definition that was referenced by the value of the Native OSEE
                   * "PrimaryAttribute".
                   */

                  var referencedAttributeDefinition = attributeDefinitionByParentIdentifierAndNativeIdentifierMap.get( attributeDefinitionGroveThingParentIdentifier, nativePrimaryAttributeTypeIdentifier ).get();
                  var referencedAttributeDefinitionIdentifier = referencedAttributeDefinition.getIdentifier();

                  /*
                   * Change the Publishing DOM Attribute Value from the Native OSEE Attribute Type identifier to the
                   * Synchronization Artifact Identifier of the referenced Synchronization Artifact Attribute Definition.
                   */

                  attributeValue.setValue( referencedAttributeDefinitionIdentifier.toString() );
               }

               /*
                * Add the Publishing DOM Attribute Definition and Attribute Value to their parent in the Publishing DOM.
                */

               this.documentMap.appendAttributeDefinition(parentIdentifier,attributeDefinition);
               this.documentMap.appendAttributeValue(parentIdentifier,attributeDefinition.getIdentifier(),attributeValue);
            }
         );
      //@formatter:on

      /*
       * Once the Publishing DOM has been built, determine the hierarchy level of each node.
       */

      this.documentMap.setHierarchyLevels();

      return true;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void close() {
      this.converterMap = null;
      this.documentMap = null;
      this.serializer = null;
      this.synchronizationArtifact = null;
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public void deserialize(InputStream inputStream) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getAttributeDefinition(GroveThing attributeValueGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Stream<String> getAttributeDefinitions(GroveThing specificationTypeGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalStateException when the {@link #build} method
    */

   @Override
   public Optional<Consumer<GroveThing>> getConverter(IdentifierType identifierType) {
      return this.converterMap.getFunction(identifierType);
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getDatatypeDefinition(GroveThing attributeDefinitionGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Stream<String> getEnumValues(GroveThing datatypeDefinitionGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Stream<ForeignThingFamily> getForeignThings(IdentifierType identifierType) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getSpecificationType(GroveThing specificationGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<ForeignThingFamily> getSpecObject(GroveThing specRelationGroveThing, RelationshipTerminal relationshipTerminal) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getSpecObjectType(GroveThing specObjectGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public Optional<String> getSpecRelationType(GroveThing specRelationGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when the parameter <code>synchronizationArtifact</code> is <code>null</code>.
    */

   @Override
   public void initialize(SynchronizationArtifact synchronizationArtifact) {

      this.synchronizationArtifact = Objects.requireNonNull(synchronizationArtifact,
         "PublishingDomBuilder::initialize, the parameter \"synchronizationArtifact\" cannot be null.");

      //@formatter:off
      var forest = synchronizationArtifact.getForest();

      var documentMapIdentifier = forest.getIdentifier();

      this.documentMap = Factory.createDocumentMap( documentMapIdentifier );

      /*
       * Create the converters
       */

      var attributeDefinitionConverter       = new AttributeDefinitionConverter( this.documentMap );
      var attributeValueConverter            = new AttributeValueConverter( this.documentMap );
      var specElementWithAttributesConverter = new SpecElementWithAttributesConverter( this.documentMap );

      this.converterMap = new EnumConsumerMap<>(IdentifierType.class);

      this.converterMap.put( IdentifierType.ATTRIBUTE_DEFINITION, attributeDefinitionConverter::convert       );
      this.converterMap.put( IdentifierType.ATTRIBUTE_VALUE,      attributeValueConverter::convert            );
      this.converterMap.put( IdentifierType.DATA_TYPE_DEFINITION, NullConverter::convert                      );
      this.converterMap.put( IdentifierType.ENUM_VALUE,           NullConverter::convert                      );
      this.converterMap.put( IdentifierType.HEADER,               NullConverter::convert                      );
      this.converterMap.put( IdentifierType.SPECIFICATION,        specElementWithAttributesConverter::convert );
      this.converterMap.put( IdentifierType.SPECIFICATION_TYPE,   NullConverter::convert                      );
      this.converterMap.put( IdentifierType.SPECTER_SPEC_OBJECT,  specElementWithAttributesConverter::convert );
      this.converterMap.put( IdentifierType.SPEC_OBJECT,          specElementWithAttributesConverter::convert );
      this.converterMap.put( IdentifierType.SPEC_OBJECT_TYPE,     NullConverter::convert                      );
      this.converterMap.put( IdentifierType.SPEC_RELATION_TYPE,   NullConverter::convert                      );
      this.converterMap.put( IdentifierType.SPEC_RELATION,        specElementWithAttributesConverter::convert );
      //@formatter:on
   }

   /**
    * Import operations are not supported by this implementation.
    * <p>
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException Import operations are not supported.
    */

   @Override
   public boolean isEnumerated(GroveThing attributeValueGroveThing) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @implNode Uses the Serializer Functional Interface implementation passed to the constructor to serialize the
    * Publishing DOM.
    */

   @Override
   public InputStream serialize() {
      return this.serializer.apply(this.documentMap);
   }

}

/* EOF */
