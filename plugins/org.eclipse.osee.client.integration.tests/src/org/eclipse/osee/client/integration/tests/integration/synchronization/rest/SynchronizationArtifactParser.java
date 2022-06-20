/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.synchronization.rest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.ws.rs.core.Response;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.RankMap;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.DatatypeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecRelation;
import org.eclipse.rmf.reqif10.SpecRelationType;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceFactoryImpl;
import org.junit.Assert;

/**
 * Test class for requesting a ReqIF Synchronization Artifact from the server and indexing the ReqIF DOM.
 *
 * @author Loren K. Ashley
 */

class SynchronizationArtifactParser {

   /**
    * Saves the {@link ReqIF} test document to be parsed.
    */

   ReqIF reqifTestDocument;

   /**
    * Saves a reference to the {@link SynchronizationEndpoint} used to make test call to the API.
    */

   SynchronizationEndpoint synchronizationEndpoint;

   /**
    * Creates a new Synchronization Artifact Parser for {@link ReqIF} documents.
    *
    * @param synchronizationEndpoint the REST API to obtain the test document from.
    */

   SynchronizationArtifactParser(SynchronizationEndpoint synchronizationEndpoint) {
      this.synchronizationEndpoint = Objects.requireNonNull(synchronizationEndpoint);

      this.reqifTestDocument = null;
   }

   /**
    * Reflective method to extract a {@link String} value from an {@link EObject} by specifying a method name of the
    * {@link EObject} instance. The named method may belong to the {@link EObject} class, any of its super classes, or
    * any of the derived classes implementing the {@link EObject} instance.
    *
    * @param eObject the ReqIF Attribute Value to obtain an identifier or long name for.
    * @param secondaryFunctionName use "getIdentifier" to obtain the identifier and "getLongName" to get the long name.
    * @return the identifier or long name to be used for the Attribute Value.
    * @throws AssertionError when any of the reflective methods fail.
    */

   private static String extractStringValueFromEObject(EObject eObject, String extractionMethodName) {
      try {
         var eObjectClass = eObject.getClass();

         var extractionMethod = eObjectClass.getMethod(extractionMethodName);

         var value = (String) extractionMethod.invoke(eObject);

         return value;
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
         //Should never get here.
         return null;
      }
   }

   /**
    * {@link AttributeValue} subclasses all define a <code>getDefinition</code> method to obtain a reference to the
    * {@link AttributeDefinition} subclass that defines the attribute. Since these methods are defined at the subclass
    * level and this is test code, reflection is used to obtain the {@link AttributeDefintion} associated with the
    * {@link AttributeValue} instead of writing class specific code.
    *
    * @param eObject the {@link AttributeValue} to obtain the associated {@link AttributeDefinition} from.
    * @return the {@link AttributeDefinition}.
    * @throws AssertionError when"
    * <ul>
    * <li>The object returned by the <code>getDefinition</code> method is not an instance of
    * {@link AttributeDefinition}, or</li>
    * <li>a failure occurs obtaining the {@link AttributeDefinition}.</li>
    * </ul>
    */

   private static AttributeDefinition getAttributeDefinitionFromEObject(EObject eObject) {
      try {
         var attributeDefinition = eObject.getClass().getDeclaredMethod("getDefinition").invoke(eObject);

         Assert.assertTrue(attributeDefinition instanceof AttributeDefinition);

         return (AttributeDefinition) attributeDefinition;
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
         //Should never get here.
         return null;
      }
   }

   /**
    * For each provided {@link Identifiable}:
    * <ul>
    * <li>extracts an identifier,</li>
    * <li>extracts a long name,</li>
    * <li>adds the {@link Identifiable} to the maps using the identifier or long name as the key.</li>
    * </ul>
    *
    * @param reqifIdentifiables the list of {@link Identifiable> objects to be added to the maps.
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when:
    * <ul>
    * <li>an identifier is not extracted from the {@link Identifiable},</li>
    * <li>a long name is not extracted from the {@link Identifiable},</li>
    * <li>an entry for the {@link Identifiable} already exits in one of the maps.</li>
    * </ul>
    */

   @SuppressWarnings("unchecked")
   private static void mapIdentifiables(Class<? extends Identifiable> identifiableClass, EList<? extends Identifiable> reqifIdentifiables, RankMap<? extends Identifiable> byIdentifierMap, RankMap<? extends Identifiable> byLongNameMap) {

      for (var reqifIdentifiable : reqifIdentifiables) {

         if (identifiableClass.isInstance(reqifIdentifiable)) {

            var identifier = reqifIdentifiable.getIdentifier();

            Assert.assertNotNull(identifier);
            Assert.assertFalse(byIdentifierMap.containsKeys(identifier));

            ((RankMap<Identifiable>) byIdentifierMap).associate(reqifIdentifiable, identifier);

            var longName = reqifIdentifiable.getLongName();

            Assert.assertNotNull(longName);
            Assert.assertFalse(
               String.format("Map (%s) already contains entry with key (%s).", byLongNameMap.identifier(), longName),
               byLongNameMap.containsKeys(longName));

            ((RankMap<Identifiable>) byLongNameMap).associate(reqifIdentifiable, longName);
         }
      }
   }

   /**
    * For each provided primary {@link Identifiable}
    * <ul>
    * <li>extracts a primary key,</li>
    * <li>extracts a list of secondary {@link Identifiable} objects,</li>
    * <li>extracts a secondary key from each secondary {@link Identifiable}, and</li>
    * <li>saves the secondary identifiable into a map using the primary and secondary keys.</li>
    * </ul>
    *
    * @param reqifPrimaryIdentifiables list of ReqIF {@link Identifiable} objects to be stored into the maps.
    * @param primaryKeyExtractionFunction a {@link Function} used to extract the primary key from the primary
    * {@link Identifiable}.
    * @param secondaryIdentifiablersFunction a {@link Function} used to extract the secondary {@link Identifiable} from
    * the primary {@link Identifiable}.
    * @param secondaryKeyExtractionFunction a {@link Function} used to extract the secondary key from the secondary
    * {@link Identifiable}.
    * @param map the secondary {@link Identifiable} objects are stored into this map with the primary and secondary
    * keys.
    */

   //@formatter:off
   private static void
      mapSecondaryEObjects
         (
            EList<? extends Identifiable>                    reqifPrimaryIdentifiables,
            Function<Identifiable, String>                   primaryKeyExtractionFunction,
            Function<Identifiable, EList<? extends EObject>> secondaryIdentifiablesFunction,
            Function<EObject, String>                        secondaryKeyExtractionFunction,
            RankMap<EObject>                                 map
         )
   {
      reqifPrimaryIdentifiables
         .forEach
            (
              ( reqifPrimaryIdentifiable ) ->
                 secondaryIdentifiablesFunction.apply( reqifPrimaryIdentifiable ).stream()
                    .map
                       (
                         ( reqifSecondaryIdentifiable ) ->
                            map.associate
                               (
                                 reqifSecondaryIdentifiable,
                                 primaryKeyExtractionFunction.apply( reqifPrimaryIdentifiable ),
                                 secondaryKeyExtractionFunction.apply( reqifSecondaryIdentifiable )
                               )
                       )
                    .map( Optional::isEmpty )
                    .forEach ( Assert::assertTrue )
            );
   }
   //@formatter:on

   /**
    * Parses ReqIF AttributeDefinitions from the test document into a rank 2 {@link RankMap} keyed with the
    * Specification Type or Spec Object Type identifier or long name; and then keyed by the Attribute Definitions's
    * identifier or long name.
    *
    * @param reqifSpecElementWithAttributesExtractors an array of {@link Function}s used to extract lists of
    * {@link SpecElementWithAttributes} from the Core Content of the Test Document to parse the Attribute Values from.
    * @param primaryKeyExtractionFunction a {@link Function} that is used to extract the primary map key from the
    * {@link SpecElementWithAttributes} (Specification, Spec Object, or Spec Relation) that contains the Attribute
    * Value.
    * @param secondaryKeyExtractionMethodName the name of a method belonging to the {@link AttributeDefinition} subclass
    * referenced by the Attribute Value that returns a {@link String}. This method is used to obtain the secondary map
    * key.
    * @param map a rank 2 {@link RankMap} to store the {@link AttributeValue} objects in.
    */

   //@formatter:off
   @SuppressWarnings("unchecked")
   void
      parseAttributeDefinitions
      (
         Function<ReqIFContent,EList<? extends Identifiable>>[] reqifSpecElementWithAttributesExtractors,
         Function<Identifiable, String>                         primaryKeyExtractionFunction,
         String                                                 secondaryKeyExtractionMethodName,
         RankMap<AttributeDefinition>                           map
      )
   {
      var reqifCoreContent = this.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(
         "SynchronizationArtifactParser::parseAttributeDefinitions, Error ReqIF document has not yet been parsed.",
         reqifCoreContent);

      Arrays.stream( reqifSpecElementWithAttributesExtractors )
      .map( ( extractor ) -> extractor.apply( reqifCoreContent ) )
      .peek( Assert::assertNotNull )
      .forEach
         (
            ( elist ) ->
               SynchronizationArtifactParser.mapSecondaryEObjects
                  (
                     elist,
                     primaryKeyExtractionFunction,
                     (specType) -> ((SpecType) specType).getSpecAttributes(),
                     ( eObject ) -> SynchronizationArtifactParser.extractStringValueFromEObject(eObject, secondaryKeyExtractionMethodName ),
                     (RankMap<EObject>) (Object) map
                  )
         )
      ;
   }
   //@formatter:on

   /**
    * ReqIF Attribute Value objects don't have an identifier or long name. The Attribute Value objects reference an
    * Attribute Definition which does have an identifier and long name. The referenced Attribute Definition's identifier
    * and long name are used for the Attribute Value. The ReqIF Attribute Value classes do not share a common base or
    * interface which allows for access to the Attribute Definition or the Attribute Definition's values. Since this is
    * test code, reflection is used to obtain the Attribute Definition object and then either it's identifier or long
    * name instead of implementing class specific code.
    *
    * @param eObject the ReqIF Attribute Value to obtain an identifier or long name for.
    * @param secondaryFunctionName use "getIdentifier" to obtain the identifier and "getLongName" to get the long name.
    * @return the identifier or long name to be used for the Attribute Value.
    * @throws AssertionError when any of the reflective methods fail.
    */

   private static String parseAttributeValueKey(EObject eObject, String secondaryFunctionName) {

      var attributeDefinition = SynchronizationArtifactParser.getAttributeDefinitionFromEObject(eObject);

      return SynchronizationArtifactParser.extractStringValueFromEObject(attributeDefinition, secondaryFunctionName);
   }

   /**
    * Parses ReqIF AttributeValues from the test document into a rank 2 {@link RankMap}.
    *
    * @param reqifSpecElementWithAttributesExtractors an array of {@link Function}s used to extract lists of
    * {@link SpecElementWithAttributes} from the Core Content of the Test Document to parse the Attribute Values from.
    * @param primaryKeyExtractionFunction a {@link Function} that is used to extract the primary map key from the
    * {@link SpecElementWithAttributes} (Specification, Spec Object, or Spec Relation) that contains the Attribute
    * Value.
    * @param secondaryKeyExtractionMethodName the name of a method belonging to the {@link AttributeDefinition} subclass
    * referenced by the Attribute Value that returns a {@link String}. This method is used to obtain the secondary map
    * key.
    * @param map a rank 2 {@link RankMap} to store the {@link AttributeValue} in.
    */

   //@formatter:off
   @SuppressWarnings("unchecked")
   void
      parseAttributeValues
         (
            Function<ReqIFContent,EList<? extends Identifiable>>[] reqifSpecElementWithAttributesExtractors,
            Function<Identifiable, String>                         primaryKeyExtractionFunction,
            String                                                 secondaryKeyExtractionMethodName,
            RankMap<AttributeValue>                                map
         )
   {
      var reqifCoreContent = this.reqifTestDocument.getCoreContent();

      Assert.assertNotNull
         (
            "SynchronizationArtifactParser::parseAttributeValues, Error ReqIF document has not yet been parsed.",
            reqifCoreContent
         );

      Arrays.stream( reqifSpecElementWithAttributesExtractors )
         .map( ( extractor ) -> extractor.apply( reqifCoreContent ) )
         .peek( Assert::assertNotNull )
         .forEach
            (
               ( elist ) ->
                  SynchronizationArtifactParser.mapSecondaryEObjects
                     (
                        elist,
                        primaryKeyExtractionFunction,
                        ( specElementWithAttributes ) -> ((SpecElementWithAttributes) specElementWithAttributes).getValues(),
                        ( eObject ) -> SynchronizationArtifactParser.parseAttributeValueKey(eObject, secondaryKeyExtractionMethodName ),
                        (RankMap<EObject>) (Object) map
                     )
            )
         ;
   }
   //@formatter:on

   /**
    * Parses the ReqIF Data Type Definitions from the test document into maps by identifier and by long names.
    *
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @param enumValueByIdentifierMap The {@link Map} to add the {@link EnumValue} objects from
    * {@link DatatypeDefinition} objects for enumerated data types to.
    */

   void parseDatatypeDefinitions(RankMap<DatatypeDefinition> byIdentifierMap, RankMap<DatatypeDefinition> byLongNameMap, RankMap<String> enumValueLongNameByIdentifierMap) {

      Assert.assertNotNull(
         "SynchronizationArtifactParser::parseDatatypeDefinitions, Error ReqIF document has not yet been parsed.",
         this.reqifTestDocument);

      var reqifCoreContent = this.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifDatatypes = reqifCoreContent.getDatatypes();

      Assert.assertNotNull(reqifDatatypes);

      SynchronizationArtifactParser.mapIdentifiables(Identifiable.class, reqifDatatypes, byIdentifierMap,
         byLongNameMap);

      //@formatter:off
      reqifDatatypes.stream()
         .filter( reqifDatatype -> reqifDatatype instanceof DatatypeDefinitionEnumeration )
         .flatMap( reqifDatatype -> ((DatatypeDefinitionEnumeration) reqifDatatype).getSpecifiedValues().stream() )
         .forEach( enumValue -> enumValueLongNameByIdentifierMap.associateThrowOnDuplicate( enumValue.getLongName(), enumValue.getIdentifier() ) );
      //@formatter:on
   }

   /**
    * Parses the ReqIF Spec Objects from the test document into maps by identifier and by long names.
    *
    * @param byIdentifier The {@link Map} to add the {@link SpecObject} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link SpecObject} objects keyed by long name to.
    */

   void parseSpecObjects(RankMap<SpecObject> byIdentifierMap, RankMap<SpecObject> byLongNameMap) {

      Assert.assertNotNull(
         "SynchronizationArtifactParser::parseSpecObjects, Error ReqIF document has not yet been parsed.",
         this.reqifTestDocument);

      var reqifCoreContent = this.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecObjects = reqifCoreContent.getSpecObjects();

      Assert.assertNotNull(reqifSpecObjects);

      SynchronizationArtifactParser.mapIdentifiables(Identifiable.class, reqifSpecObjects, byIdentifierMap,
         byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Types from the test document into maps by identifier and by long names.
    *
    * @param byIdentifier The {@link Map} to add the {@link SpecType} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link SpecType} objects keyed by long name to.
    */

   void parseSpecTypes(Class<? extends SpecType> specTypeClass, RankMap<SpecType> byIdentifierMap, RankMap<SpecType> byLongNameMap) {

      Assert.assertNotNull(
         "SynchronizationArtifactParser::parseSpecTypes, Error ReqIF document has not yet been parsed.",
         this.reqifTestDocument);

      var reqifCoreContent = this.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecTypes = reqifCoreContent.getSpecTypes();

      Assert.assertNotNull(reqifSpecTypes);

      SynchronizationArtifactParser.mapIdentifiables(specTypeClass, reqifSpecTypes, byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Relation Types from the test document into maps by identifier and by long names.
    *
    * @param byIdentifier The {@link Map} to add the {@link SpecRelationType} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link SpecRelaiontType} objects keyed by long name to.
    */

   void parseSpecRelationTypes(RankMap<SpecType> byIdentifierMap, RankMap<SpecType> byLongNameMap) {

      Assert.assertNotNull(
         "SynchronizationArtifactParser::parseSpecRelationTypes, Error ReqIF document has not yet been parsed.",
         this.reqifTestDocument);

      this.parseSpecTypes(SpecRelationType.class, byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Relation objects from the test document into a map by type, source, and target identifiers.
    *
    * @param byTypeSourceTargetIdentifierMap The {@link RankMap} to add the {@link SpecRelation} objects keyed by type,
    * source, and target identifiers to.
    */

   void parseSpecRelations(RankMap<SpecRelation> byTypeSourceTargetIdentifierMap) {

      Assert.assertNotNull(
         "SynchronizationArtifactParser::parseSpecRelations, Error ReqIF document has not yet been parsed.",
         this.reqifTestDocument);

      var reqifCoreContent = this.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecRelations = reqifCoreContent.getSpecRelations();

      Assert.assertNotNull(reqifSpecRelations);

      for (var reqifSpecRelation : reqifSpecRelations) {
         var reqifSourceSpecObject = reqifSpecRelation.getSource();

         Assert.assertNotNull(reqifSourceSpecObject);

         var reqifTargetSpecObject = reqifSpecRelation.getTarget();

         Assert.assertNotNull(reqifTargetSpecObject);

         var reqifSpecRelationType = reqifSpecRelation.getType();

         Assert.assertNotNull(reqifSpecRelationType);

         var reqifSourceSpecObjectIdentifier = reqifSourceSpecObject.getIdentifier();

         Assert.assertNotNull(reqifSourceSpecObjectIdentifier);

         var reqifTargetSpecObjectIdentifier = reqifTargetSpecObject.getIdentifier();

         Assert.assertNotNull(reqifTargetSpecObjectIdentifier);

         var reqifSpecRelationTypeIdentifier = reqifSpecRelationType.getIdentifier();

         Assert.assertNotNull(reqifSpecRelationTypeIdentifier);

         byTypeSourceTargetIdentifierMap.associate(reqifSpecRelation, reqifSpecRelationTypeIdentifier,
            reqifSourceSpecObjectIdentifier, reqifTargetSpecObjectIdentifier);
      }
   }

   /**
    * Get the test document from the server and parse it into a ReqIF DOM.
    *
    * @return the ReqIF DOM.
    * @throws AssertionError when
    * <ul>
    * <li>a response is not received from the server,</li>
    * <li>the server response is not OK,</li>
    * <li>the received resource does not have any contents,</li>
    * <li>the received resource does not contain a {@link ReqIF} object.</li>
    * </ul>
    * @throws RuntimeException when an error occurs loading the {@link InputStream} received from the server into a
    * resource.
    */

   ReqIF parseTestDocument(BranchId rootBranchId, ArtifactId rootArtifactId, String synchronizationArtifactType) {

      Response response = this.synchronizationEndpoint.getSynchronizationArtifact(rootBranchId, rootArtifactId,
         synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);

      var reqIfInputStream = response.readEntity(InputStream.class);

      var resourceSet = new ResourceSetImpl();

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("reqif",
         new ReqIF10ResourceFactoryImpl());

      var uri = URI.createFileURI("i.reqif");

      var resource = resourceSet.createResource(uri);

      try {
         resource.load(reqIfInputStream, null);
      } catch (Exception e) {
         throw new RuntimeException("Resource Load Failed", e);
      }

      var eObjectList = resource.getContents();

      Assert.assertNotNull(eObjectList);

      var rootEObject = eObjectList.get(0);

      Assert.assertTrue(rootEObject instanceof ReqIF);

      this.reqifTestDocument = (ReqIF) rootEObject;

      return this.reqifTestDocument;
   }

}

/* EOF */
