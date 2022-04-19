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

package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DoubleHashMap;
import org.eclipse.osee.framework.jdk.core.util.DoubleMap;
import org.eclipse.osee.framework.jdk.core.util.EnumFunctionMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactReferenceAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;
import org.eclipse.rmf.reqif10.AccessControlledElement;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeDefinitionBoolean;
import org.eclipse.rmf.reqif10.AttributeDefinitionDate;
import org.eclipse.rmf.reqif10.AttributeDefinitionInteger;
import org.eclipse.rmf.reqif10.AttributeDefinitionReal;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeDefinitionXHTML;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.DatatypeDefinition;
import org.eclipse.rmf.reqif10.DatatypeDefinitionBoolean;
import org.eclipse.rmf.reqif10.DatatypeDefinitionDate;
import org.eclipse.rmf.reqif10.DatatypeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.DatatypeDefinitionInteger;
import org.eclipse.rmf.reqif10.DatatypeDefinitionReal;
import org.eclipse.rmf.reqif10.DatatypeDefinitionString;
import org.eclipse.rmf.reqif10.DatatypeDefinitionXHTML;
import org.eclipse.rmf.reqif10.EnumValue;
import org.eclipse.rmf.reqif10.Identifiable;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;
import org.eclipse.rmf.reqif10.XhtmlContent;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceFactoryImpl;
import org.eclipse.rmf.reqif10.xhtml.XhtmlDivType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * Tests for the Synchronization REST API End point defined in the package
 * {@link org.eclipse.osee.synchronization.rest}.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationEndpointTest {

   /**
    * Set this flag to <code>false</code> to prevent the test setup code from altering attribute values in the database.
    * The default (normal for testing) value is <code>true</code>.
    */

   private static boolean setValues = true;

   /**
    * Testing rule used to prevent modification of a production database. This is a {@link ClassRule} which will prevent
    * the <code>BeforeClass</code> method from running on a production database. A {@link TestRule} is not applied to
    * <code>BeforeClass</code> class methods and will therefore not provide any protection.
    */

   @ClassRule
   public static NotProductionDataStoreRule rule = new NotProductionDataStoreRule();

   private interface NamedMap<K, V> extends Map<K, V> {
      String getName();
   }

   private interface NamedDoubleMap<Kp, Ks, V> extends DoubleMap<Kp, Ks, V> {
      String getName();
   }

   @SuppressWarnings("serial")
   private static class NamedHashMap<K, V> extends HashMap<K, V> implements NamedMap<K, V> {
      private final String name;

      NamedHashMap(String name) {
         super();
         this.name = name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }

   private static class NamedDoubleHashMap<Kp, Ks, V> extends DoubleHashMap<Kp, Ks, V> implements NamedDoubleMap<Kp, Ks, V> {
      private final String name;

      NamedDoubleHashMap(String name) {
         super();
         this.name = name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }

   /**
    * Enumeration of the expected ReqIF {@link Identifiable} subclasses.
    */

   private static enum IdentifiableType {

      /**
       * {@link IdentifiableType} for {@link AttributeDefinition} subclass.
       */

      ATTRIBUTE_DEFINITION,

      /**
       * {@link IdentifiableType} for {@link DatatypeDefinition} subclass.
       */

      DATATYPE_DEFINITION,

      /**
       * {@link IdentifiableType} for unknown or unexpected subclass.
       */

      ERROR,

      /**
       * {@link IdentifiableType} for {@link SpecificationType} subclass.
       */

      SPEC_TYPE;

      /**
       * Classifies a subclass of the type {@link Identifiable}.
       *
       * @param identifiableClass the class of the {@link Identifiable} subclass object to be classified.
       * @return an {@link IdentifiableType} member describing the subclass.
       */

      static IdentifiableType classify(Class<? extends Identifiable> identifiableClass) {
         //@formatter:off
         return
            AttributeDefinition.class.isAssignableFrom( identifiableClass )
               ? ATTRIBUTE_DEFINITION
               : DatatypeDefinition.class.isAssignableFrom( identifiableClass )
                    ? DATATYPE_DEFINITION
                    : SpecType.class.isAssignableFrom( identifiableClass )
                         ? SPEC_TYPE
                         : ERROR;
         //@formatter:off
      }
   };

   /**
    * Class used to define, and build test artifacts with a test attribute in the local database. Only one attribute per
    * test artifact is setup and used for testing.
    */

   private static class ArtifactInfoRecord {

      /**
       * A map of {@link Function} methods to get expected descriptions based upon the {@link IdentifiableType}.
       */

      //@formatter:off
      private final static EnumFunctionMap<IdentifiableType,ArtifactInfoRecord,String> descriptionFunctionMap =
         EnumFunctionMap.ofEntries
            (
              IdentifiableType.class,
              Map.entry( IdentifiableType.SPEC_TYPE,            ArtifactInfoRecord::getSpecTypeDescription ),
              Map.entry( IdentifiableType.ATTRIBUTE_DEFINITION, ArtifactInfoRecord::getAttributeDefinitionDescription ),
              Map.entry( IdentifiableType.DATATYPE_DEFINITION,  ArtifactInfoRecord::getDatatypeDefinitionDescription )
            );
      //@formatter:on

      /**
       * A map of {@link Function} methods to get expected identifier prefixes based upon the {@link IdentifiableType}.
       */

      //@formatter:off
      private final static EnumFunctionMap<IdentifiableType,ArtifactInfoRecord,String> identifierPrefixFunctionMap =
         EnumFunctionMap.ofEntries
            (
               IdentifiableType.class,
               Map.entry( IdentifiableType.SPEC_TYPE,            ArtifactInfoRecord::getSpecTypeIdentifierPrefix ),
               Map.entry( IdentifiableType.ATTRIBUTE_DEFINITION, ArtifactInfoRecord::getAttributeDefinitionIdentifierPrefix ),
               Map.entry( IdentifiableType.DATATYPE_DEFINITION,  ArtifactInfoRecord::getDatatypeDefinitionIdentifierPrefix )
            );
      //@formatter:on

      /**
       * A map of {@link Function} methods to get the expected long name based upon the {@link IdentifiableType}.
       */

      //@formatter:off
      private final static EnumFunctionMap<IdentifiableType,ArtifactInfoRecord,String> longNameFunctionMap =
         EnumFunctionMap.ofEntries
            (
               IdentifiableType.class,
               Map.entry( IdentifiableType.SPEC_TYPE,            ArtifactInfoRecord::getSpecTypeLongName ),
               Map.entry( IdentifiableType.ATTRIBUTE_DEFINITION, ArtifactInfoRecord::getAttributeDefinitionLongName ),
               Map.entry( IdentifiableType.DATATYPE_DEFINITION,  ArtifactInfoRecord::getDatatypeDefinitionLongName )
            );
      //@formatter:on

      /**
       * The test {@link Artifact} loaded from the database.
       */

      private Artifact artifact;

      /**
       * The {@link ArtifactToken} ({@link ArtifactId}) of the test artifact.
       */

      private ArtifactToken artifactToken;

      /**
       * The test {@link Attribute} loaded from the database.
       */

      private Attribute<?> attribute;

      /**
       * The expected subclass of the ReqIf {@link AttributeDefinition} object.
       */

      private final Class<? extends AttributeDefinition> attributeDefinitionClass;

      /**
       * The expected description for the test attribute's {@link AttributeDefinition}.
       */

      private final String attributeDefinitionDescription;

      /**
       * The expected identifier prefix for the test attribute's {@link AttributeDefinition}.
       */

      private final String attributeDefinitionIdentifierPrefix;

      /**
       * The expected long name for the test attribute's {@link AttributeDefinition}.
       */

      private final String attributeDefinitionLongName;

      /**
       * A {@link BiConsumer} implementation used to assign the attribute value to the test attribute.
       */

      private final BiConsumer<Attribute<?>, Object> attributeSetter;

      /**
       * A {@link Function} implementation used to convert the value from the ReqIf test document back into the OSEE
       * attribute value type.
       */

      private final Function<Object, Object> backToOseeTypeFunction;

      /**
       * The expected subclass of the ReqIF {@link DatatypeDefinition} object.
       */

      private final Class<? extends DatatypeDefinition> datatypeDefinitionClass;

      /**
       * The expected description for the test attribute's {@link DatatypeDefinition}.
       */

      private final String datatypeDefinitionDescription;

      /**
       * The expected identifier prefix for the test attribute's {@link DatatypeDefinition}.
       */

      private final String datatypeDefinitionIdentifierPrefix;

      /**
       * The expected long name for the test attribute's {@link DatatypeDefinition}.
       */

      private final String datatypeDefinitionLongName;

      /**
       * Method to check {@link DatatypeDefinition} subclass specific parameters for correctness.
       */

      private final Consumer<? super DatatypeDefinition> datatypeDefinitionVerifier;

      /**
       * The {@link ArtifactInfoRecord#identifier} for the test artifact that is the hierarchical parent of the test
       * artifact defined by this record. Use 0 for top level artifacts.
       */

      private final Integer hierarchicalParentIdentifier;

      /**
       * A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforce bad test setup will result if
       * not unique. The identifier 0 is reserved for the Default Hierarchy Root artifact.
       */

      private final Integer identifier;

      /**
       * A name for the test artifact.
       */

      private final String name;

      /**
       * The expected description for the enclosing {@link SpecType} of the test attribute's value.
       */

      private final String specTypeDescription;

      /**
       * The expected identifier prefix for the enclosing {@link SpecType} of the test attribute's value.
       */

      private final String specTypeIdentifierPrefix;

      /**
       * The expected long name for the enclosing {@link SpecType} of the test attribute's value.
       */

      private final String specTypeLongName;

      /**
       * The expected default value for the test attribute.
       */

      private final Object testAttributeDefaultValue;

      /**
       * The {@link AttributeTypeGeneric} of the test attribute.
       */

      private final AttributeTypeGeneric<?> testAttributeType;

      /**
       * The value to be assigned to the test attribute.
       */

      private final Object testAttributeValue;

      /**
       * The {@link ArtifactTypeToken} of the test artifact.
       */

      private final ArtifactTypeToken typeToken;

      /**
       * Constructs a new empty {@link ArtifactInfoRecord} with the specified parameters.
       *
       * @param identifier A unique identifier for the {@link ArtifactInfoRecord}. Uniqueness is not enforced. The value
       * 0 is reserved.
       * @param hierarchicalParentIdentifier The {@link ArtifactInfoRecord#identifier} of the test artifact that is the
       * hierarchical parent of the test artifact. Use 0 for top level artifacts.
       * @param name A name for the test artifact.
       * @param typeToken The {@link ArtifactTypeToken} of the test artifact.
       * @param testAttributeType The {@link AttributeTypeGeneric} of the test attribute.
       * @param testAttributeValue The test attribute value.
       * @param testAttributeDefaultValue the expected default value for the test attribute.
       * @param specTypeDescription The expected description for the enclosing {@link SpecType} of the test attribute's
       * value.
       * @param specTypeIdentifierPrefix The expected identifier prefix for the enclosing {@link SpecType} of the test
       * attribute's value.
       * @param specTypeLongName The expected long name for the enclosing {@link SpecType} of the test attribute's
       * value.
       * @param attributeDefinitionDescription The expected description for the test attribute's
       * {@link AttributeDefinition}.
       * @param attributeDefinitionIdentifierPrefix The expected identifier prefix for the test attribute's
       * {@link AttributeDefinition}.
       * @param attributeDefinitionLongName The expected long name for the test attribute's {@link AttributeDefinition}.
       * @param attributeDefinitionClass The expected subclass of the ReqIf {@link AttributeDefinition} object.
       * @param datatypeDefinitionDescription The expected description for the test attribute's
       * {@link DatatypeDefinition}.
       * @param datatypeDefinitionIdentifier The expected identifier prefix for the test attribute's
       * {@link DatatypeDefinition}.
       * @param datatypeDefinitionLongName The expected long name for the test attribute's {@link DatatypeDefinition}.
       * @param datatypeDefinitionClass The expected subclass of the ReqIF {@link DatatypeDefinition} object.
       * @param attributeSetter A {@link BiConsumer} used to assign the attribute value to the test attribute. The first
       * parameter is the attribute as an {@link Attribute} and the second parameter is the value as an {@link Object}.
       * @param backToOseeTypeFunction A {@link Function} implementation used to convert the value from the ReqIf test
       * document back into the OSEE attribute value type.
       */

      //@formatter:off
      ArtifactInfoRecord
         (
            Integer                                identifier,
            Integer                                hierarchicalParentIdentifier,
            String                                 name,
            ArtifactTypeToken                      typeToken,
            AttributeTypeGeneric<?>                testAttributeType,
            Object                                 testAttributeValue,
            Object                                 testAttributeDefaultValue,

            String                                 specTypeDescription,
            String                                 specTypeIdentifierPrefix,
            String                                 specTypeLongName,

            String                                 attributeDefinitionDescription,
            String                                 attributeDefinitionIdentifierPrefix,
            String                                 attributeDefinitionLongName,
            Class<? extends AttributeDefinition>   attributeDefinitionClass,

            String                                 datatypeDefinitionDescription,
            String                                 datatypeDefinitionIdentifierPrefix,
            String                                 datatypeDefinitionLongName,
            Class<? extends DatatypeDefinition>    datatypeDefinitionClass,
            Consumer<? super DatatypeDefinition>   datatypeDefinitionVerifier,

            BiConsumer<Attribute<?>,Object>     attributeSetter,
            Function<Object,Object>             backToOseeTypeFunction
         )
      {
         this.identifier = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.name = Objects.requireNonNull(name);
         this.typeToken = Objects.requireNonNull(typeToken);
         this.testAttributeType = Objects.requireNonNull(testAttributeType);
         this.testAttributeValue = Objects.requireNonNull(testAttributeValue);
         this.testAttributeDefaultValue = testAttributeDefaultValue;

         this.specTypeDescription = Objects.requireNonNull( specTypeDescription );
         this.specTypeIdentifierPrefix = Objects.requireNonNull( specTypeIdentifierPrefix );
         this.specTypeLongName = Objects.requireNonNull(specTypeLongName);

         this.attributeDefinitionDescription = Objects.requireNonNull( attributeDefinitionDescription );
         this.attributeDefinitionIdentifierPrefix = Objects.requireNonNull( attributeDefinitionIdentifierPrefix );
         this.attributeDefinitionLongName = Objects.requireNonNull(attributeDefinitionLongName);
         this.attributeDefinitionClass = Objects.requireNonNull(attributeDefinitionClass);

         this.datatypeDefinitionDescription = Objects.requireNonNull( datatypeDefinitionDescription );
         this.datatypeDefinitionIdentifierPrefix = Objects.requireNonNull( datatypeDefinitionIdentifierPrefix );
         this.datatypeDefinitionLongName = Objects.requireNonNull(datatypeDefinitionLongName);
         this.datatypeDefinitionClass = Objects.requireNonNull(datatypeDefinitionClass);
         this.datatypeDefinitionVerifier = datatypeDefinitionVerifier;

         this.attributeSetter = Objects.requireNonNull(attributeSetter);
         this.backToOseeTypeFunction = Objects.requireNonNull(backToOseeTypeFunction);

         this.artifactToken = null;
         this.artifact = null;
         this.attribute = null;
      }
      //@formatter:on

      /**
       * Creates a new artifact under the specified parent on the specified branch.
       *
       * @param artifactEndpoint The REST API end point for artifact functions.
       * @param parentBranchId The branch to create the new artifact upon.
       * @param parentArtifactId The hierarchical parent of the artifact to be created.
       * @return The {@link ArtifactToken} (identifier) for the newly created artifact.
       */

      private ArtifactToken createChild(ArtifactEndpoint artifactEndpoint, BranchId parentBranchId, ArtifactId parentArtifactId) {
         var newArtifactToken =
            artifactEndpoint.createArtifact(parentBranchId, this.typeToken, parentArtifactId, this.name);
         return artifactEndpoint.getArtifactToken(newArtifactToken);
      }

      /**
       * Creates the test attribute for the test artifact.
       *
       * @return the created attribute.
       * @throws NoSuchElementException when unable to obtain the created attribute.
       */

      private Attribute<?> createTestAttribute() {
         var artifact = this.getArtifact();
         artifact.addAttribute(this.testAttributeType);

         var attributes = artifact.getAttributes(this.testAttributeType);

         if (attributes.size() == 1) {
            return attributes.get(0);
         }

         throw new NoSuchElementException();
      }

      /**
       * Gets the test artifact.
       *
       * @return the test artifact.
       */

      Artifact getArtifact() {
         assert Objects.nonNull(this.artifact);
         return this.artifact;
      }

      /**
       * Gets the identifier for the test artifact.
       *
       * @return the test artifact identifier.
       */

      ArtifactToken getArtifactToken() {
         assert Objects.nonNull(this.artifactToken);
         return this.artifactToken;
      }

      /**
       * Gets the expected subclass of the ReqIF {@link AttributeDefinition} object.
       *
       * @return the expected subclass of the ReqIF {@link AttributeDefinition} object.
       */

      Class<? extends AttributeDefinition> getAttributeDefinitionClass() {
         return this.attributeDefinitionClass;
      }

      /**
       * Gets the expected description for the ReqIF {@link AttributeDefinition} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected description.
       */

      String getAttributeDefinitionDescription() {
         return this.attributeDefinitionDescription;
      }

      /**
       * Gets the expected identifier prefix for the ReqIF {@link AttributeDefinition} that should be generated in the
       * test document for the test artifact.
       *
       * @return the expected identifier prefix.
       */

      String getAttributeDefinitionIdentifierPrefix() {
         return this.attributeDefinitionIdentifierPrefix;
      }

      /**
       * Gets the expected long name for the ReqIF {@link AttributeDefinition} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected long name.
       */

      String getAttributeDefinitionLongName() {
         return this.attributeDefinitionLongName;
      }

      /**
       * Gets a the hierarchical child {@link Artifact} by artifact name that is represented by this
       * {@link ArtifactInfoRecord}.
       *
       * @param relationEndpoint REST API end point for relationships.
       * @param parentArtifactId the {@link ArtifactId} of the artifact to find a child of.
       * @return If found, an {@link Optional} containing the loaded {@link Artifact}; otherwise, an empty
       * {@link Optional}.
       */

      private Optional<ArtifactToken> getChildByName(RelationEndpoint relationEndpoint, ArtifactId parentArtifactId) {
         //@formatter:off
         return
            relationEndpoint
               .getRelatedHierarchy( parentArtifactId )
               .stream()
               .filter( artifact -> artifact.getName().equals( this.name ) )
               .findFirst()
               ;
         //@formatter:on
      }

      /**
       * Gets the expected subclass of the ReqIF {@link DatatypeDefinition} object.
       *
       * @return the expected subclass of the ReqIF {@link DatatypeDefinition} object.
       */

      Class<? extends DatatypeDefinition> getDatatypeDefinitionClass() {
         return this.datatypeDefinitionClass;
      }

      /**
       * Gets the expected description for the ReqIF {@link DatatypeDefinition} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected description.
       */

      String getDatatypeDefinitionDescription() {
         return this.datatypeDefinitionDescription;
      }

      /**
       * Gets the expected identifier prefix for the ReqIF {@link DatatypeDefinition} that should be generated in the
       * test document for the test artifact.
       *
       * @return the expected identifier prefix.
       */

      String getDatatypeDefinitionIdentifierPrefix() {
         return this.datatypeDefinitionIdentifierPrefix;
      }

      /**
       * Gets the expected long name for the ReqIF {@link DatatypeDefinition} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected long name.
       */

      String getDatatypeDefinitionLongName() {
         return this.datatypeDefinitionLongName;
      }

      /**
       * Get the expected description for the ReqIF {@link Identifiable} subclass.
       *
       * @param identifiableClass the class of {@link Identifiable} to get the expected description for.
       * @return the expected description.
       */

      String getIdentifiableDescription(Class<? extends Identifiable> identifiableClass) {
         return ArtifactInfoRecord.descriptionFunctionMap.apply(IdentifiableType.classify(identifiableClass), this);
      }

      /**
       * Get the expected identifier prefix for the ReqIF {@link Identifiable} subclass.
       *
       * @param identifiableClass the class of {@link Identifiable} to get the expected description for.
       * @return the expected description.
       */

      String getIdentifiableIdentifierPrefix(Class<? extends Identifiable> identifiableClass) {
         return ArtifactInfoRecord.identifierPrefixFunctionMap.apply(IdentifiableType.classify(identifiableClass),
            this);
      }

      /**
       * Get the expected long name for the ReqIF {@link Identifiable} subclass.
       *
       * @param identifiableClass the class of {@link Identifiable} to get the expected description for.
       * @return the expected description.
       */

      String getIdentifiableLongName(Class<? extends Identifiable> identifiableClass) {
         return ArtifactInfoRecord.longNameFunctionMap.apply(IdentifiableType.classify(identifiableClass), this);
      }

      /**
       * Gets the {@link ArtifactInfoRecord} identifier.
       *
       * @return the assigned unique {@link Integer} identifier.
       */

      Integer getIdentifier() {
         return this.identifier;
      }

      /**
       * Gets or creates the test attribute for the test artifact. The test artifact must be obtained before calling
       * this method.
       */

      void getOrCreateAttribute() {
         assert Objects.isNull(this.attribute);
         this.attribute = this.getTestAttribute().orElseGet(this::createTestAttribute);
      }

      /**
       * Gets or creates the artifact identifier for the test artifact.
       *
       * @param relationEndpoint The REST API end point for obtaining related artifacts.
       * @param artifactEndpoint The REST API end point for creating artifacts.
       * @param parentBranchId The identifier of the branch to get or create the test artifact on.
       * @param hierarchicalParentArtifactIdMap A map of the {@link ArtifactToken} (identifiers) of the hierarchical
       * parent artifacts by {@link ArtifactInfoRecord} identifiers. The {@link ArtifactToken} for the obtained or
       * created artifact will be added to the map with the identifier of this {@link ArtifactInfoRecord} as the key.
       */

      void getOrCreateArtifactToken(RelationEndpoint relationEndpoint, ArtifactEndpoint artifactEndpoint, BranchId parentBranchId, Map<Integer, ArtifactId> hierarchicalParentArtifactIdMap) {
         assert Objects.isNull(this.artifactToken);
         var hierarchicalParentArtifactId = hierarchicalParentArtifactIdMap.get(this.hierarchicalParentIdentifier);

         this.artifactToken = this.getChildByName(relationEndpoint, hierarchicalParentArtifactId).orElseGet(
            () -> this.createChild(artifactEndpoint, parentBranchId, hierarchicalParentArtifactId));

         hierarchicalParentArtifactIdMap.put(this.identifier, artifactToken);
      }

      /**
       * The long name of the test artifact's Specification, Spec Object, or Spec Relation.
       *
       * @return the expected long name.
       */

      String getSpecElementWithAttributesLongName() {
         return this.name;
      }

      /**
       * Gets the expected description for the ReqIF {@link SpecType} that should be generated in the test document for
       * the test artifact.
       *
       * @return the expected description.
       */

      String getSpecTypeDescription() {
         return this.specTypeDescription;
      }

      /**
       * Gets the expected identifier prefix for the ReqIF {@link SpecType} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected identifier prefix.
       */

      String getSpecTypeIdentifierPrefix() {
         return this.specTypeIdentifierPrefix;
      }

      /**
       * Gets the expected long name for the ReqIF {@link SpecType} that should be generated in the test document for
       * the test artifact.
       *
       * @return the expected long name.
       */

      String getSpecTypeLongName() {
         return this.specTypeLongName;
      }

      /**
       * Gets the test attribute from the test artifact.
       *
       * @return An {@link Optional} with the test attribute if it exists; otherwise, an empty {@link Optional}.
       */

      private Optional<Attribute<?>> getTestAttribute() {
         assert Objects.nonNull(this.artifact);
         var attributes = this.artifact.getAttributes(this.testAttributeType); //this.getTestAttributeTypeId());

         return (attributes.size() == 1) ? Optional.of(attributes.get(0)) : Optional.empty();
      }

      /**
       * Predicate to determine if the test attribute has a default value.
       *
       * @return <code>true</code> when a default value is defined; otherwise, <code>false</code>.
       */

      boolean hasDefaultValue() {
         return Objects.nonNull(this.testAttributeDefaultValue);
      }

      /**
       * Save the artifact back to the database if it has been modified.
       */

      void persistIfDirty() {
         if (this.artifact.isDirty()) {
            this.artifact.persist("Three Blind Mice");
         }
      }

      /**
       * If the test attribute is not the expected value, set the test attribute value.
       */

      void setAttributeValue() {

         if ((SynchronizationEndpointTest.setValues) && (!this.attribute.getValue().equals(this.testAttributeValue))) {
            this.attributeSetter.accept(this.attribute, this.testAttributeValue);
         }
      }

      /**
       * Saves the test artifact that was retrieved from the database.
       *
       * @param artifact the {@link Artifact} read from the database.
       * @return this {@link ArtifactInfoRecord}.
       */

      ArtifactInfoRecord setArtifact(Artifact artifact) {
         assert Objects.isNull(this.artifact);
         this.artifact = Objects.requireNonNull(artifact);
         return this;
      }

      /**
       * Checks {@link DatatypeDefinition} subclass specific parameters for correctness.
       *
       * @param datatypeDefinition the {@link DatatypeDefinition} to check.
       */
      void verifyDatatypeDefinition(DatatypeDefinition datatypeDefinition) {
         if (this.datatypeDefinitionVerifier != null) {
            this.datatypeDefinitionVerifier.accept(datatypeDefinition);
         }
      }

      /**
       * Verifies a value read from the ReqIF DOM matches the expected value saved in this record.
       *
       * @param reqifValue the value to be tested.
       * @param checkDefaultValue <code>true</code> to check against the default value and <code>false</code> to check
       * against the regular value.
       * @throws AssertionError when the provided value does not match the expected value.
       */

      private void verifyExpectedValue(Object reqifValue, Boolean checkDefaultValue) {

         var oseeValue = this.backToOseeTypeFunction.apply(reqifValue);

         var expectedValue = checkDefaultValue ? this.testAttributeDefaultValue : this.testAttributeValue;

         if (oseeValue instanceof Date) {
            Assert.assertTrue(((Date) oseeValue).compareTo((Date) expectedValue) == 0);
         } else {
            Assert.assertEquals(oseeValue, expectedValue);
         }

      }

   }

   /**
    * Time {@link ZoneId} constant for "Zulu".
    */

   private static final ZoneId zoneIdZ = ZoneId.of("Z");

   /**
    * {@link GregorianCalendar} constant for the UNIX epoch January 1, 1970 UTC.
    */

   private static final GregorianCalendar lastChangeEpoch =
      GregorianCalendar.from(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, SynchronizationEndpointTest.zoneIdZ));

   /**
    * List of {@link ArtifactInfoRecords} describing the test artifacts.
    * <p>
    * Artifacts are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * <li>Top level test artifact have a hierarchical parent identifier of 0.</li>
    * <li>Ensure children artifact's of a hierarchical parent artifact have unique names.</li>
    * <li>Ensure the Long Names for each class of ReqIF object are unique.</li>
    * </ul>
    */

   //@formatter:off
   private static List<ArtifactInfoRecord> artifactInfoRecords =
      List.of
         (
           new ArtifactInfoRecord
                  (
                    1,                                        /* Identifier                             (Integer)                               */
                    0,                                        /* Hierarchical Parent Identifier         (Integer)                               */
                    "ReqIF Test Specifications",              /* Artifact Name                          (String)                                */
                    CoreArtifactTypes.Folder,                 /* Artifact Type                          (ArtifactTypeToken)                     */
                    CoreAttributeTypes.Description,           /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                    "Motley folder of test artifacts",        /* Test Attribute Value                   (Object)                                */
                    null,                                     /* Test Attribute Default Value           {Object}                                */
                    "",                                       /* Spec Type Description                  (String)                                */
                    "ST-",                                    /* Spec Type Identifier Prefix            (String)                                */
                    "",                                       /* Spec Type Long Name                    (String)                                */
                    "",                                       /* Attribute Definition Description       (String)                                */
                    "AD-",                                    /* Attribute Definition Identifier Prefix (String)                                */
                    "",                                       /* Attribute Definition Long Name         (String)                                */
                    AttributeDefinitionInteger.class,         /* Attribute Definition Class             (Class<? extends AttributeDefinition)   */
                    "",                                       /* Datatype Definition Description        (String)                                */
                    "DD-",                                    /* Datatype Definition Identifier Prefix  (String)                                */
                    "",                                       /* Datatype Definition Long Name          (String)                                */
                    DatatypeDefinitionInteger.class,          /* Datatype Definition Class              (Class<? extends DatatypeDefinition)    */
                    null,                                     /* Datatype Definition Verifier           (Consumer<? super DatatypeDefinition)   */
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ),
                    ( reqifValue ) -> reqifValue
                  ),

           new ArtifactInfoRecord
                  (
                    2,
                    1,
                    "ARTIFACT_IDENTIFIER_TESTER",
                    CoreArtifactTypes.GitCommit,
                    CoreAttributeTypes.UserArtifactId,
                    ArtifactId.valueOf( 1938 ),
                    ArtifactId.valueOf( -1 ),
                    "OSEE Git Commit Spec Object Type",
                    "SOT-",
                    "Git Commit",
                    "Artifact id of an artifact of type User",
                    "AD-",
                    "User Artifact Id",
                    AttributeDefinitionInteger.class,
                    "OSEE Artifact Identifier Datatype",
                    "DD-",
                    "ARTIFACT_IDENTIFIER",
                    DatatypeDefinitionInteger.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionInteger = (DatatypeDefinitionInteger) datatypeDefinition;

                       Assert.assertEquals( SynchronizationEndpointTest.maxLong, reqifDatatypeDefinitionInteger.getMax() );
                       Assert.assertEquals( SynchronizationEndpointTest.minLong, reqifDatatypeDefinitionInteger.getMin() );
                    },
                    ( attribute, value ) -> ((ArtifactReferenceAttribute) attribute).setValue( (ArtifactId) value ),
                    ( reqifValue ) -> ArtifactId.valueOf( ((BigInteger) reqifValue).longValueExact() )
                  ),

           new ArtifactInfoRecord
                  (
                    3,
                    1,
                    "BOOLEAN_TESTER",
                    CoreArtifactTypes.User,
                    CoreAttributeTypes.Active,
                    true,
                    false,
                    "OSEE User Spec Object Type",
                    "SOT-",
                    "User",
                    "OSEE Active Attribute Definition",
                    "AD-",
                    "Active",
                    AttributeDefinitionBoolean.class,
                    "OSEE Boolean Datatype",
                    "DD-",
                    "BOOLEAN",
                    DatatypeDefinitionBoolean.class,
                    null, /* Nothing else to verify for Boolean types */
                    ( attribute, value ) -> ((BooleanAttribute) attribute).setValue( (Boolean) value ),
                    ( reqifValue ) -> reqifValue
                  ),

           new ArtifactInfoRecord
                  (
                    4,
                    1,
                    "DATE_TESTER",
                    CoreArtifactTypes.CertificationBaselineEvent,
                    CoreAttributeTypes.BaselinedTimestamp,
                    SynchronizationEndpointTest.getTestDate(),
                    SynchronizationEndpointTest.lastChangeEpoch.getTime(),
                    "OSEE Certification Baseline Event Spec Object Type",
                    "SOT-",
                    "Certification Baseline Event",
                    "OSEE Baselined Timestamp Attribute Definition",
                    "AD-",
                    "Baselined Timestamp",
                    AttributeDefinitionDate.class,
                    "OSEE Date Datatype",
                    "DD-",
                    "DATE",
                    DatatypeDefinitionDate.class,
                    null, /* Nothing else to verify for Boolean types */
                    ( attribute, value ) -> ((DateAttribute) attribute).setValue( (Date) value ),
                    ( reqifValue ) -> ((GregorianCalendar) reqifValue).getTime()
                  ),

           new ArtifactInfoRecord
                  (
                    5,
                    1,
                    "DOUBLE_TESTER",
                    SynchronizationEndpointTest.getArtifactType( "Work Package" ),
                    SynchronizationEndpointTest.getAttributeType( "ats.Estimated Hours" ),
                    8.75,
                    0.0,
                    "OSEE Work Package Spec Object Type",
                    "SOT-",
                    "Work Package",
                    "Hours estimated to implement the changes associated with this Action.\\nIncludes estimated hours for workflows, tasks and reviews.",
                    "AD-",
                    "ats.Estimated Hours",
                    AttributeDefinitionReal.class,
                    "OSEE Double Datatype",
                    "DD-",
                    "DOUBLE",
                    DatatypeDefinitionReal.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionReal = (DatatypeDefinitionReal) datatypeDefinition;

                       Assert.assertEquals( SynchronizationEndpointTest.accuracyReal, reqifDatatypeDefinitionReal.getAccuracy() );
                       Assert.assertEquals( SynchronizationEndpointTest.maxReal, (Double) reqifDatatypeDefinitionReal.getMax() );
                       Assert.assertEquals( SynchronizationEndpointTest.minReal, (Double) reqifDatatypeDefinitionReal.getMin() );
                    },
                    ( attribute, value ) -> ((FloatingPointAttribute) attribute).setValue( (Double) value ),
                    ( reqifValue ) -> reqifValue
                  ),

           new ArtifactInfoRecord
                  (
                    6,
                    1,
                    "INTEGER_TESTER",
                    CoreArtifactTypes.CertificationBaselineEvent,
                    CoreAttributeTypes.ReviewId,
                    42,
                    0,
                    "OSEE Certification Baseline Event Spec Object Type",
                    "SOT-",
                    "Certification Baseline Event",
                    "OSEE Review Id Attribute Definition",
                    "AD-",
                    "Review Id",
                    AttributeDefinitionInteger.class,
                    "OSEE Integer Datatype",
                    "DD-",
                    "INTEGER",
                    DatatypeDefinitionInteger.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionInteger = (DatatypeDefinitionInteger) datatypeDefinition;

                       Assert.assertEquals( SynchronizationEndpointTest.maxInteger, reqifDatatypeDefinitionInteger.getMax() );
                       Assert.assertEquals( SynchronizationEndpointTest.minInteger, reqifDatatypeDefinitionInteger.getMin() );
                    },
                    ( attribute, value ) -> ((IntegerAttribute) attribute).setValue( (Integer) value ),
                    ( reqifValue ) -> ((BigInteger) reqifValue).intValueExact()
                  ),

           new ArtifactInfoRecord
                  (
                    7,
                    1,
                    "LONG_TESTER",
                    SynchronizationEndpointTest.getArtifactType( "Team Definition" ),
                    SynchronizationEndpointTest.getAttributeType( "ats.Task Set Id" ),
                    420L,
                    0L,
                    "OSEE Team Definition Spec Object Type",
                    "SOT-",
                    "Team Definition",
                    "OSEE ats.Task Set Id Attribute Definition",
                    "AD-",
                    "ats.Task Set Id",
                    AttributeDefinitionInteger.class,
                    "OSEE Long Datatype",
                    "DD-",
                    "LONG",
                    DatatypeDefinitionInteger.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionInteger = (DatatypeDefinitionInteger) datatypeDefinition;

                       Assert.assertEquals( SynchronizationEndpointTest.maxLong, reqifDatatypeDefinitionInteger.getMax() );
                       Assert.assertEquals( SynchronizationEndpointTest.minLong, reqifDatatypeDefinitionInteger.getMin() );
                    },
                    ( attribute, value ) -> ((LongAttribute) attribute).setValue( (Long) value ),
                    ( reqifValue ) -> ((BigInteger) reqifValue).longValueExact()
                  ),

           new ArtifactInfoRecord
                  (
                    8,
                    1,
                    "STRING_TESTER",
                    CoreArtifactTypes.Artifact,
                    CoreAttributeTypes.Description,
                    "Three cats are required for all great software developments.",
                    null,
                    "OSEE Artifact Spec Object Type",
                    "SOT-",
                    "Artifact",
                    "OSEE Description Attribute Definition",
                    "AD-",
                    "Description",
                    AttributeDefinitionString.class,
                    "OSEE String Datatype",
                    "DD-",
                    "STRING",
                    DatatypeDefinitionString.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionString = (DatatypeDefinitionString) datatypeDefinition;

                       Assert.assertEquals( SynchronizationEndpointTest.maxLengthString, reqifDatatypeDefinitionString.getMaxLength() );
                    },
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ),
                    ( reqifValue ) -> reqifValue
                  ),

           new ArtifactInfoRecord
                  (
                    9,
                    1,
                    "STRING_WORD_ML_TESTER",
                    CoreArtifactTypes.MsWordWholeDocument,
                    CoreAttributeTypes.WholeWordContent,
                    "Three cats are required for all great software developments.",
                    null,
                    "OSEE MS Word Whole Document Spec Object Type",
                    "SOT-",
                    "MS Word Whole Document",
                    "value must comply with WordML xml schema",
                    "AD-",
                    "Whole Word Content",
                    AttributeDefinitionXHTML.class,
                    "OSEE String Word ML Datatype",
                    "DD-",
                    "STRING_WORD_ML",
                    DatatypeDefinitionXHTML.class,
                    null,
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ),
                    ( reqifValue ) ->
                    {
                       var xhtmlContent = (XhtmlContent) reqifValue;
                       var xhtmlDivType = (XhtmlDivType) xhtmlContent.getXhtml();
                       var featureMap = xhtmlDivType.getMixed();
                       var stringValue = featureMap.getValue(0);
                       return stringValue;
                    }
                  ),

           new ArtifactInfoRecord
                  (
                    10,
                    1,
                    "URI_TESTER",
                    CoreArtifactTypes.OseeTypeDefinition,
                    CoreAttributeTypes.UriGeneralStringData,
                    "http://org.eclipse.org",
                    null,
                    "OSEE Osee Type Definition Spec Object Type",
                    "SOT-",
                    "Osee Type Definition",
                    "OSEE Uri General String Data Attribute Definition",
                    "AD-",
                    "Uri General String Data",
                    AttributeDefinitionString.class,
                    "OSEE URI Datatype",
                    "DD-",
                    "URI",
                    DatatypeDefinitionString.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionString = (DatatypeDefinitionString) datatypeDefinition;

                       Assert.assertEquals( SynchronizationEndpointTest.maxLengthStringUri, reqifDatatypeDefinitionString.getMaxLength() );
                    },
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ),
                    ( reqifValue ) -> reqifValue
                  ),

           new ArtifactInfoRecord
                  (
                    11,
                    1,
                    "ENUM_TESTER",
                    CoreArtifactTypes.Breaker,
                    CoreAttributeTypes.FunctionalGrouping,
                    "VMS/Flight Control",
                    null,
                    "OSEE Breaker Spec Object Type",
                    "SOT-",
                    "Breaker",
                    "OSEE Functional Grouping Attribute Definition",
                    "AD-",
                    "Functional Grouping",
                    AttributeDefinition.class,
                    "OSEE Enumerated Datatype",
                    "DD-",
                    "ENUMERATED-1741310787702764470",
                    DatatypeDefinition.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionEnumeration = (DatatypeDefinitionEnumeration) datatypeDefinition;

                       var enumMemberMap = reqifDatatypeDefinitionEnumeration.getSpecifiedValues().stream().collect( Collectors.toMap( EnumValue::getLongName, Function.identity() ) );

                       var ordinalAtomicInteger = new AtomicInteger( 0 );

                       // Enumeration members must be listed in ordinal order

                       Arrays.stream( new String[] { "Avionics", "VMS/Flight Control", "Engine/Fuel/Hydraulics", "Electrical" } ).forEach
                          (
                             ( enumMemberName ) ->
                             {
                                var ordinal = ordinalAtomicInteger.getAndIncrement();
                                var enumMember = enumMemberMap.get( enumMemberName );

                                Assert.assertNotNull( enumMember );

                                Assert.assertEquals( enumMemberName, enumMember.getProperties().getOtherContent() );
                                Assert.assertEquals( BigInteger.valueOf( ordinal ), enumMember.getProperties().getKey() );
                             }
                          );
                    },
                    ( attribute, value ) -> ((StringAttribute) attribute).setValue( (String) value ),
                    ( reqifValue ) -> reqifValue
                  )

         );
   //@formatter:on

   /**
    * A {@link Map} of the {@link ArtifactInfoRecord} objects by identifier.
    */

   private static Map<Integer, ArtifactInfoRecord> artifactInfoRecordsByIdentifierMap =
      SynchronizationEndpointTest.artifactInfoRecords.stream().collect(
         Collectors.toMap(ArtifactInfoRecord::getIdentifier, (artifactInfoRecord) -> artifactInfoRecord));

   /**
    * The number of bits used to represent the Real value.
    */

   private static BigInteger accuracyReal = BigInteger.valueOf(Double.SIZE);

   /**
    * The maximum value of an OSEE integer attribute as a {@link BigInteger}.
    */

   private static BigInteger maxInteger = BigInteger.valueOf(Integer.MAX_VALUE);

   /**
    * The maximum length of an OSEE string attribute as a {@link BigInteger}.
    */

   private static BigInteger maxLengthString = BigInteger.valueOf(8192);

   /**
    * The maximum length of an OSEE string attribute as a {@link BigInteger}.
    */

   private static BigInteger maxLengthStringUri = BigInteger.valueOf(2048);

   /**
    * The maximum value of an OSEE long attribute as a {@link BigInteger}.
    */

   private static BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);

   /**
    * The maximum value of an OSEE real attribute as a {@link Double}.
    */

   private static Double maxReal = Double.MAX_VALUE;

   /**
    * The minimum value of an OSEE integer attribute as a {@link BigInteger}.
    */

   private static BigInteger minInteger = BigInteger.valueOf(Integer.MIN_VALUE);

   /**
    * The minimum value of an OSEE long attribute as a {@link BigInteger}.
    */

   private static BigInteger minLong = BigInteger.valueOf(Long.MIN_VALUE);

   /**
    * The minimum value of an OSEE real attribute as a {@link Double}.
    */

   private static Double minReal = Double.MIN_VALUE;

   /**
    * ReqIF Attribute Definitions are specific to ReqIF Specification Types and Spec Object Types. This is a map of the
    * ReqIF Attribute Definitions in the test document keyed by the ReqIF Specification Type or ReqIF Spec Object Type
    * identifier and then by the ReqIF Attribute Definition identifier.
    */

   private static NamedDoubleMap<String, String, AttributeDefinition> reqifAttributeDefinitionByIdentifiersMap;

   /**
    * ReqIF Attribute Definitions are specific to ReqIF Specification Types and Spec Object Types. This is a map of the
    * ReqIF Attribute Definitions in the test document keyed by the ReqIF Specification Type or ReqIF Spec Object Type
    * long name and then by the ReqIF Attribute Definition long name.
    */

   private static NamedDoubleMap<String, String, AttributeDefinition> reqifAttributeDefinitionByLongNamesMap;

   /**
    * ReqIF Attribute Values are specific to ReqIF Specification and Spec Objects. This is a map of the ReqIF Attribute
    * Values in the test document keyed by the ReqIF Specification or ReqIF Spec Object identifier and then by the ReqIF
    * Attribute Value's Attribute Definition reference Identifier.
    */

   private static NamedDoubleMap<String, String, AttributeValue> reqifAttributeValueByIdentifiersMap;

   /**
    * ReqIF Attribute Values are specific to ReqIF Specification and Spec Objects. This is a map of the ReqIF Attribute
    * Values in the test document keyed by the ReqIF Specification or ReqIF Spec Object long name and then by the ReqIF
    * Attribute Value's Attribute Definition reference long name.
    */

   private static NamedDoubleMap<String, String, AttributeValue> reqifAttributeValueByLongNamesMap;

   /**
    * Map of ReqIF Data Type Definitions from the test document keyed by their identifiers.
    */

   private static NamedMap<String, DatatypeDefinition> reqifDatatypeDefinitionByIdentifierMap;

   /**
    * Map of ReqIF Data Type Definitions from the test document keyed by their long names.
    */

   private static NamedMap<String, DatatypeDefinition> reqifDatatypeDefinitionByLongNameMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their identifiers. This map does not include the ReqIF
    * Specifications.
    */

   private static NamedMap<String, SpecObject> reqifSpecObjectByIdentifierMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their long names. This map does not include the ReqIF
    * Specifications.
    */

   private static NamedMap<String, SpecObject> reqifSpecObjectByLongNameMap;

   /**
    * Map of ReqIF Specification Type and Spec Object Type definitions from the test document keyed by their
    * identifiers.
    */

   private static NamedMap<String, SpecType> reqifSpecTypeByIdentifierMap;

   /**
    * Map of ReqIF Specification Type and Spec Object Type definitions from the test document keyed by their long names.
    */

   private static NamedMap<String, SpecType> reqifSpecTypeByLongNameMap;

   /**
    * Saves the {@link ReqIF} DOM of the test document read back from the server.
    */

   private static ReqIF reqifTestDocument;

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId rootBranchId;

   /**
    * Saves a reference to the {@link SynchronizationEndpoint} used to make test call to the API.
    */

   private static SynchronizationEndpoint synchronizationEndpoint;

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "ReqIF Test Branch";

   /**
    * Creates a new branch in the database.
    *
    * @param branchEndpoint REST API end point for branches.
    * @param branchName The name to assign to the new branch.
    * @return The newly created {@link Branch}.
    */

   private static Branch createTestBranch(BranchEndpoint branchEndpoint, String branchName) {
      var newBranch = new NewBranch();
      newBranch.setAssociatedArtifact(ArtifactId.SENTINEL);
      newBranch.setBranchName(branchName);
      newBranch.setBranchType(BranchType.WORKING);
      newBranch.setCreationComment("For ReqIF Synchronization Artifact Testing");
      newBranch.setMergeAddressingQueryId(0L);
      newBranch.setMergeDestinationBranchId(null);
      newBranch.setParentBranchId(CoreBranches.SYSTEM_ROOT);
      newBranch.setSourceTransactionId(TransactionManager.getHeadTransaction(CoreBranches.SYSTEM_ROOT));
      newBranch.setTxCopyBranchType(false);

      var newBranchId = branchEndpoint.createBranch(newBranch);

      return branchEndpoint.getBranchById(newBranchId);
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
    * Gets the {@link ArtifactTypeToken} for the named artifact type. Not all of the artifact type classes are available
    * to the test suite at compile time. This method provides a means to specify test artifacts where the class
    * instantiation of the artifact type definition is not available to compile.
    *
    * @param name the artifact type name.
    * @return the {@link ArtifactTypeToken} for the named artifact type.
    */

   private static ArtifactTypeToken getArtifactType(String name) {
      return ServiceUtil.getTokenService().getArtifactType(name);
   }

   /**
    * Gets the {@link AttributeTypeGeneric} for the named attribute type. Not all of the attribute type classes are
    * available to the test suite at compile time. This method provides a means to specify test attributes where the
    * class instantiation of the attribute definition is not available to compile.
    *
    * @param name the attribute type name.
    * @return the {@link AttributeTypeGeneric} for the named attribute type.
    */

   private static AttributeTypeGeneric<?> getAttributeType(String name) {
      return ServiceUtil.getTokenService().getAttributeType(name);
   }

   /**
    * Gets a {@link Branch} by name.
    *
    * @param branchEndpoint REST API end point for branches.
    * @param branchName the name of the branch to get.
    * @return If found, an {@link Optional} containing the loaded {@link Branch}; otherwise, an empty {@link Optional}.
    */

   private static Optional<Branch> getBranchByName(BranchEndpoint branchEndpoint, String branchName) {
      //@formatter:off
      return
         branchEndpoint
            .getBranches("", "", "", false, false, SynchronizationEndpointTest.testBranchName, "", null, null, null)
            .stream()
            .filter( branch -> branch.getName().equals( branchName ) )
            .findFirst()
            ;
      //@formatter:on
   }

   /**
    * Gets the {@link DatatypeDefinition} referenced by the {@link AttributeDefinition}. The method to obtain the
    * {@link DatatypeDefinition} for an {@link AttributeDefinition} is defined separately in each subclass of
    * {@link AttributeDefinition} and returns the specific subclass of {@link DatatypeDefinition} that is referenced by
    * the {@link AttributeDefinition}. Since this is test code, reflection is used to obtain the
    * {@link DatatypeDefinition} object from the {@link AttributeDefinition}.
    *
    * @param reqifAttributeDefinition the {@link AttributeDefinition}.
    * @return the {@link DatatypeDefinition}.
    * @throws AssertionError when unable to obtain the referenced {@link DatatypeDefinition}.
    */

   public static DatatypeDefinition getDatatypeDefinitionFromAttributeDefinition(AttributeDefinition reqifAttributeDefinition) {
      try {
         var datatypeDefinition =
            (DatatypeDefinition) reqifAttributeDefinition.getClass().getDeclaredMethod("getType").invoke(
               reqifAttributeDefinition);
         Assert.assertNotNull(datatypeDefinition);
         return datatypeDefinition;
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
         //Will never reach here.
         return null;
      }
   }

   /**
    * Generates a known {@link Date} value for testing.
    *
    * @return the {@link Date} October 26, 1967.
    */

   private static Date getTestDate() {
      var calendarBuilder = new Calendar.Builder();
      calendarBuilder.setDate(1967, 10 - 1, 26);
      calendarBuilder.setTimeOfDay(0, 0, 0);
      var calendar = calendarBuilder.build();
      return calendar.getTime();
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
   private static void mapIdentifiables(EList<? extends Identifiable> reqifIdentifiables, NamedMap<String, ? extends Identifiable> byIdentifierMap, NamedMap<String, ? extends Identifiable> byLongNameMap) {

      for (var reqifIdentifiable : reqifIdentifiables) {

         var identifier = reqifIdentifiable.getIdentifier();

         Assert.assertNotNull(identifier);
         Assert.assertFalse(byIdentifierMap.containsKey(identifier));

         ((Map<String, Identifiable>) byIdentifierMap).put(identifier, reqifIdentifiable);

         var longName = reqifIdentifiable.getLongName();

         Assert.assertNotNull(longName);
         Assert.assertFalse(
            String.format("Map (%s) already contains entry with key (%s).", byLongNameMap.getName(), longName),
            byLongNameMap.containsKey(longName));

         ((Map<String, Identifiable>) byLongNameMap).put(longName, reqifIdentifiable);
      }
   }

   /**
    * For each provided secondary {@link Identifiable}:
    * <ul>
    * <li>extracts an identifier,</li>
    * <li>extracts a long name,</li>
    * <li>adds the secondary {@link Identifiable} to the maps using the primary and secondary identifiers or long names
    * as keys.</li>
    * </ul>
    *
    * @param primaryIdentifier the identifier to use as the primary map key for the <code>byIdentifierMap</code>. This
    * parameter may be <code>null</code>. When <code>null</code> the <code>byIdentifierMap</code> will not be populated.
    * @param primaryLongName the long name to use as the primary map key for the <code>byLongNameMap</code>. This
    * parameter may be <code>null</code>. When <code>null</code> the <code>byLongNameMap</code> will not be populated.
    * @param reqifSecondaryEObjects the list of secondary {@link Identifiable} objects that were extracted from the
    * primary {@link Identifiable}.
    * @param secondaryIdentifierFunction a {@link Function} used to extract the identifier from the secondary
    * {@link Identifiable}.
    * @param secondaryLongNameFunction a {@link Function } used to extract the long name from the secondary
    * {@link Identifiable}.
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when:
    * <ul>
    * <li>an identifier is needed and not extracted from the secondary {@link Identifiable},</li>
    * <li>a long name is needed and not extracted from the secondary {@link Identifiable},
    * <li>
    * <li>an entry for the secondary {@link Identifiable} already exists in a map being populated.</li>
    * </ul>
    */

   @SuppressWarnings("unchecked")
   private static void mapSecondaryEObjects(String primaryIdentifier, String primaryLongName, EList<? extends EObject> reqifSecondaryEObjects, Function<EObject, String> secondaryIdentifierFunction, Function<EObject, String> secondaryLongNameFunction, DoubleMap<String, String, ? extends EObject> byIdentifierMap, DoubleMap<String, String, ? extends EObject> byLongNameMap) {

      for (var reqifSecondaryEObject : reqifSecondaryEObjects) {

         if (Objects.nonNull(primaryIdentifier)) {
            var secondaryIdentifier = secondaryIdentifierFunction.apply(reqifSecondaryEObject);

            Assert.assertNotNull(secondaryIdentifier);

            var priorValueOptional = ((DoubleMap<String, String, EObject>) byIdentifierMap).put(primaryIdentifier,
               secondaryIdentifier, reqifSecondaryEObject);

            Assert.assertTrue(priorValueOptional.isEmpty());
         }

         if (Objects.nonNull(primaryLongName)) {
            var secondaryLongName = secondaryLongNameFunction.apply(reqifSecondaryEObject);

            Assert.assertNotNull(secondaryLongName);

            var priorValueOptional = ((DoubleMap<String, String, EObject>) byLongNameMap).put(primaryLongName,
               secondaryLongName, reqifSecondaryEObject);

            Assert.assertTrue(priorValueOptional.isEmpty());
         }
      }
   }

   /**
    * For each provided primary {@link Identifiable}:
    * <ul>
    * <li>extracts an identifier,</li>
    * <li>extracts a long name,</li>
    * <li>extracts a list of secondary {@link Identifiable} objects.</li>
    * </ul>
    * The secondary {@link Identifiable} objects are then added to the maps using the identifier and long name keys from
    * the associated primary {@link Identifiable} object.
    *
    * @param reqifPrimaryIdentifiables list of ReqIF {@link Identifiable} objects to be stored into the maps.
    * @param secondaryIdentifiablesFunction a {@link Function} used to extract the secondary {@link Identifiable} from
    * the primary {@link Identifiable}.
    * @param secondaryIdentifierFunction a {@link Function} used to extract the identifier from the secondary
    * {@link Identifiable}.
    * @param secondaryLongNameFunction a {@link Function} used to extract the long name from the secondary
    * {@link Identifiable}.
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when
    * <ul>
    * <li>an identifier cannot be extracted from a primary {@link Identifiable}, or</li>
    * <li>an long name cannot be extracted from a primary {@link Identifiable}.</li>
    * </ul>
    */

   private static void mapSecondaryEObjects(EList<? extends Identifiable> reqifPrimaryIdentifiables, Function<Identifiable, EList<? extends EObject>> secondaryIdentifiablesFunction, Function<EObject, String> secondaryIdentifierFunction, Function<EObject, String> secondaryLongNameFunction, DoubleMap<String, String, ? extends EObject> byIdentifierMap, DoubleMap<String, String, ? extends EObject> byLongNameMap) {

      for (var reqifPrimaryIdentifiable : reqifPrimaryIdentifiables) {

         var primaryIdentifier = reqifPrimaryIdentifiable.getIdentifier();

         Assert.assertNotNull(primaryIdentifier);

         var primaryLongName = reqifPrimaryIdentifiable.getLongName();

         Assert.assertNotNull(primaryLongName);

         var reqifSecondaryIdentifiables = secondaryIdentifiablesFunction.apply(reqifPrimaryIdentifiable);

         SynchronizationEndpointTest.mapSecondaryEObjects(primaryIdentifier, primaryLongName,
            reqifSecondaryIdentifiables, secondaryIdentifierFunction, secondaryLongNameFunction, byIdentifierMap,
            byLongNameMap);
      }
   }

   /**
    * Parses ReqIF AttributeDefinitions from the test document into {@link DoubleMap} keyed with the Specification Type
    * or Spec Object Type identifier or long name; and then keyed by the Attribute Definitions's identifier or long
    * name;
    *
    * @param reqif the test document to parse
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when
    * <ul>
    * <li>the test document core content is missing,</li>
    * <li>the test document spec types are missing.</li>
    * </ul>
    */

   private static void parseAttributeDefinitions(ReqIF reqif, DoubleMap<String, String, AttributeDefinition> byIdentifierMap, DoubleMap<String, String, AttributeDefinition> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecTypes = reqifCoreContent.getSpecTypes();

      Assert.assertNotNull(reqifSpecTypes);

      SynchronizationEndpointTest.mapSecondaryEObjects(reqifSpecTypes,
         (specType) -> ((SpecType) specType).getSpecAttributes(), (eObject) -> ((Identifiable) eObject).getIdentifier(),
         (eObject) -> ((Identifiable) eObject).getLongName(), byIdentifierMap, byLongNameMap);
   }

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

   private static String parseAttributeValueIdentifiers(EObject eObject, String secondaryFunctionName) {
      try {
         var attributeDefinition = SynchronizationEndpointTest.getAttributeDefinitionFromEObject(eObject);

         var attributeDefinitionClass = attributeDefinition.getClass();

         var getIdentifierMethod = attributeDefinitionClass.getMethod(secondaryFunctionName);

         var value = (String) getIdentifierMethod.invoke(attributeDefinition);

         return value;
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
         //Should never get here.
         return null;
      }
   }

   /**
    * Parses ReqIF AttributeValues from the test document into {@link DoubleMap} keyed with the Spec Object identifier
    * or long name; and then keyed by the Attribute Value's Attribute Definition reference identifier or long name;
    *
    * @param reqif the test document to parse
    * @param byIdentifierMap the map to store values by identifier.
    * @param byLongNameMap the map to store values by long name.
    * @throws AssertionError when
    * <ul>
    * <li>the test document core content is missing,</li>
    * <li>the test document spec objects are missing.</li>
    * </ul>
    */

   private static void parseAttributeValues(ReqIF reqif, DoubleMap<String, String, AttributeValue> byIdentifierMap, DoubleMap<String, String, AttributeValue> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecObjects = reqifCoreContent.getSpecObjects();

      Assert.assertNotNull(reqifSpecObjects);

      SynchronizationEndpointTest.mapSecondaryEObjects(reqifSpecObjects,
         (specObject) -> ((SpecObject) specObject).getValues(),
         (eObject) -> SynchronizationEndpointTest.parseAttributeValueIdentifiers(eObject, "getIdentifier"),
         (eObject) -> SynchronizationEndpointTest.parseAttributeValueIdentifiers(eObject, "getLongName"),
         byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Data Type Definitions from the test document into maps by identifier and by long names.
    *
    * @param reqif the test document to parse
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @throws AssertionError when
    * <ul>
    * <li>the ReqIF core content is not available,</li>
    * <li>the ReqIF data type definitions are not available,</li>
    * </ul>
    */

   private static void parseDatatypeDefinitions(ReqIF reqif, NamedMap<String, DatatypeDefinition> byIdentifierMap, NamedMap<String, DatatypeDefinition> byLongNameMap) {

      var reqifCoreContent = reqif.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifDatatypes = reqifCoreContent.getDatatypes();

      Assert.assertNotNull(reqifDatatypes);

      SynchronizationEndpointTest.mapIdentifiables(reqifDatatypes, byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Objects from the test document into maps by identifier and by long names.
    *
    * @param reqif the test document to parse
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @throws AssertionError when
    * <ul>
    * <li>the ReqIF core content is not available,</li>
    * <li>the ReqIF spec objects are not available,</li>
    * </ul>
    */

   private static void parseSpecObjects(ReqIF reqif, NamedMap<String, SpecObject> byIdentifierMap, NamedMap<String, SpecObject> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecObjects = reqifCoreContent.getSpecObjects();

      Assert.assertNotNull(reqifSpecObjects);

      SynchronizationEndpointTest.mapIdentifiables(reqifSpecObjects, byIdentifierMap, byLongNameMap);
   }

   /**
    * Parses the ReqIF Spec Types from the test document into maps by identifier and by long names.
    *
    * @param reqif the test document to parse
    * @param byIdentifier The {@link Map} to add the {@link DatatypeDefinition} objects keyed by identifier to.
    * @param byLongName The {@link Map} to add the {@link DatatypeDefinition} objects keyed by long name to.
    * @throws AssertionError when
    * <ul>
    * <li>the ReqIF core content is not available,</li>
    * <li>the ReqIF spec types are not available,</li>
    * </ul>
    */

   private static void parseSpecTypes(ReqIF reqif, NamedMap<String, SpecType> byIdentifierMap, NamedMap<String, SpecType> byLongNameMap) {

      var reqifCoreContent = SynchronizationEndpointTest.reqifTestDocument.getCoreContent();

      Assert.assertNotNull(reqifCoreContent);

      var reqifSpecTypes = reqifCoreContent.getSpecTypes();

      Assert.assertNotNull(reqifSpecTypes);

      SynchronizationEndpointTest.mapIdentifiables(reqifSpecTypes, byIdentifierMap, byLongNameMap);
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

   private static ReqIF parseTestDocument() {
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(
         SynchronizationEndpointTest.rootBranchId, SynchronizationEndpointTest.rootArtifactId,
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

      return (ReqIF) rootEObject;
   }

   /**
    * Verifies that a ReqIf {@link AccessControlledElement} has the "is editable" flag set and the flag is set.
    *
    * @param accessControlledElement the ReqIF {@link AccessControlledElement} to check.
    */

   public static void verifyAccessControlledElement(AccessControlledElement accessControlledElement) {
      //@formatter:off
      Assert.assertTrue( accessControlledElement.isSetEditable() && accessControlledElement.isEditable());
      //@formatter:on
   }

   /**
    * Verifies the value specified in an {@link AttributeValue} subclass is the expected value.
    *
    * @param reqifAttributeValue the {@link AttributeValue} to be checked.
    * @param artifactInfoRecord object containing the expected values.
    * @param checkDefaultValue when <code>true</code> the comparison is with the expected default value; otherwise, the
    * comparison is with the expected value.
    * @throws AssertionError when:
    * <ul>
    * <li>unable to obtain the value from the {@link AttributeValue} object, or</li>
    * <li>the {@link AttributeValue} object does not have a value.</li>
    * </ul>
    */

   private static void verifyAttributeValue(AttributeValue reqifAttributeValue, ArtifactInfoRecord artifactInfoRecord, Boolean checkDefaultValue) {
      try {

         var attributeValueIsSet =
            (Boolean) reqifAttributeValue.getClass().getDeclaredMethod("isSetTheValue").invoke(reqifAttributeValue);

         Assert.assertTrue(attributeValueIsSet);

         var declaredMethodsMap = Arrays.stream(reqifAttributeValue.getClass().getDeclaredMethods()).collect(
            Collectors.toMap(Method::getName, Function.identity()));

         //@formatter:off
         var value = declaredMethodsMap.containsKey("getTheValue")
                        ? declaredMethodsMap.get("getTheValue").invoke(reqifAttributeValue)
                        : declaredMethodsMap.containsKey("isTheValue")
                             ? declaredMethodsMap.get("isTheValue").invoke(reqifAttributeValue)
                             : null;
         //@formatter:on

         artifactInfoRecord.verifyExpectedValue(value, checkDefaultValue);

      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
      }
   }

   /**
    * Verifies the default value from an {@link AttributeDefinition}. The method to obtain the default value is defined
    * separately in each subclass of {@link AttributeDefinition} and returns the default value with its specific type.
    * Since this is test code, reflection is used to obtain the default value as an {@link Object} from the
    * {@link AttributeDefinition} instead of implementing class specific code.
    *
    * @param reqifAttributeDefinition the {@link AttributeDefinition} to get the default value from.
    * @param artifactInfoRecord object containing the expected values.
    * @throws AssertionError when:
    * <ul>
    * <li>an error occurs obtaining the default value status or the default value,</li>
    * <li>a default value is expected and the {@link AttributeDefintion} does not have a default value,</li>
    * <li>a default value is not expected and the {@link AttributeDefinition} does have a default value, or</li>
    * </ul>
    */

   private static void verifyDefaultValueFromAttributeDefinition(AttributeDefinition reqifAttributeDefinition, ArtifactInfoRecord artifactInfoRecord) {
      try {
         var defaultValueSet =
            reqifAttributeDefinition.getClass().getDeclaredMethod("isSetDefaultValue").invoke(reqifAttributeDefinition);

         Assert.assertEquals(artifactInfoRecord.hasDefaultValue(), defaultValueSet);

         if (!artifactInfoRecord.hasDefaultValue()) {
            return;
         }

         var attributeValue =
            reqifAttributeDefinition.getClass().getDeclaredMethod("getDefaultValue").invoke(reqifAttributeDefinition);

         SynchronizationEndpointTest.verifyAttributeValue((AttributeValue) attributeValue, artifactInfoRecord,
            true /* defaultValue */ );

      } catch (Exception e) {
         Assert.assertTrue(e.getMessage(), false);
      }
   }

   /**
    * The class of the <code>reqifIdentifiable</code> being tested is used to obtain the expected values from the
    * {@link ArtifactInfoRecord}. The description, identifier, last change time, and long name of the
    * {@link Identifiable} are compared with expected values.
    *
    * @param artifactInfoRecord object containing the expected values.
    * @param reqifIdentifiable the ReqIF {@link Identifiable} to be tested.
    * @throws AssertionError when:
    * <ul>
    * <li>the {@link Identifiable} object's description does not match the expected description,</li>
    * <li>the {@link Identifiable} object's identifier does not start with the expected prefix,</li>
    * <li>the {@link Identifiable} object's last change time is not the UNIX epoch, or</li>
    * <li>the {@link Identifiable} object's long name does not match the expected long name.</li>
    * </ul>
    */

   private static void verifyIdentifiable(ArtifactInfoRecord artifactInfoRecord, Identifiable reqifIdentifiable) {
      //@formatter:off
      Assert.assertEquals( artifactInfoRecord.getIdentifiableDescription( reqifIdentifiable.getClass() ), reqifIdentifiable.getDesc());
      Assert.assertTrue(reqifIdentifiable.getIdentifier().startsWith( artifactInfoRecord.getIdentifiableIdentifierPrefix( reqifIdentifiable.getClass() )));
      Assert.assertTrue(SynchronizationEndpointTest.lastChangeEpoch.compareTo(reqifIdentifiable.getLastChange()) == 0);
      Assert.assertEquals(reqifIdentifiable.getLongName(), artifactInfoRecord.getIdentifiableLongName(reqifIdentifiable.getClass()));
      //@formatter:off
   }

   /**
    * Verifies the expected ReqIF {@link AttributeDefinition} is present in the test document under the expected {@link SpecType} for the
    * specified {@link ArtifactInfoRecord}.
    *
    * @param artifactInfoRecordIdentifier The identifier of the {@link ArtifactInfoRecord} containing the expected
    * values.
    *
    * @throws AssertionError when:
    * <ul>
    * <li>the expected {@link AttributeDefinition} is not present in the test document,</li>
    * <li>the {@link AttributeDefinition} is not of the expected type,</li>
    * <li>the editable flag of the {@link AttributeDefinition} is not set or is false,</li>
    * <li>the expected {@link DatatypeDefinition} is not present in the test document,</li>
    * <li>the expected {@link DatatypeDefinition} is not of the expected type,</li>
    * <li>the {@link DatatypeDefinition} does not have an identifier,</li>
    * <li>the expected {@link DatatypeDefinition} is not found by long name,</li>
    * <li>the expected {@link DatatypeDefinition} found by long name does not have an identifier,</li>
    * <li>the identifiers of the {@link DatatypeDefinition} found by reference and the one found by long name do not match,</li>
    * <li>the {@link AttributeDefinition} does not have a default value, or</li>
    * <li>the {@link AttributeDefinition} default value does not match the expected default value.</li>
    * </ul>
    */

   public static void verifyAttributeDefinition(Integer artifactInfoRecordIdentifier) {

      var artifactInfoRecord =
         SynchronizationEndpointTest.artifactInfoRecordsByIdentifierMap.get(artifactInfoRecordIdentifier);

      var reqifAttributeDefinitionOptional = SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap.get(
         artifactInfoRecord.getSpecTypeLongName(), artifactInfoRecord.getAttributeDefinitionLongName());

      Assert.assertTrue(reqifAttributeDefinitionOptional.isPresent());

      var reqifAttributeDefinition = reqifAttributeDefinitionOptional.get();

      SynchronizationEndpointTest.verifyIdentifiable(artifactInfoRecord, reqifAttributeDefinition);

      SynchronizationEndpointTest.verifyAccessControlledElement(reqifAttributeDefinition);

      Assert.assertTrue(artifactInfoRecord.getAttributeDefinitionClass().isInstance(reqifAttributeDefinition));

      /*
       * Verify the expected default value
       */

      SynchronizationEndpointTest.verifyDefaultValueFromAttributeDefinition(reqifAttributeDefinition, artifactInfoRecord );

      /*
       * Verify the data type definition reference is correct
       */

      var reqifDatatypeDefinition =
         SynchronizationEndpointTest.getDatatypeDefinitionFromAttributeDefinition(reqifAttributeDefinition);

      var reqifDatatypeDefinitionIdentifier = reqifDatatypeDefinition.getIdentifier();

      Assert.assertNotNull(reqifDatatypeDefinitionIdentifier);

      var expectedReqifDatatypeDefinition = SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap.get(
         artifactInfoRecord.getDatatypeDefinitionLongName());

      Assert.assertNotNull(expectedReqifDatatypeDefinition);

      var expectedReqifDatatypeDefinitionIdentifier = expectedReqifDatatypeDefinition.getIdentifier();

      Assert.assertNotNull(expectedReqifDatatypeDefinitionIdentifier);

      Assert.assertEquals(expectedReqifDatatypeDefinitionIdentifier, reqifDatatypeDefinitionIdentifier);

      /*
       * Verify the Attribute Definition is in the expected Spec Element
       */

      var eContainer = reqifAttributeDefinition.eContainer();

      Assert.assertTrue( eContainer instanceof SpecType );

      var reqifSpecType = (SpecType) eContainer;

      SynchronizationEndpointTest.verifyIdentifiable( artifactInfoRecord, reqifSpecType );
   }

   /**
    * Verifies the expected ReqIF {@link AttributeValue} is present in the test document and the referenced
    * {@link AttributeDefinition} is the expected one for the specified {@link ArtifactInfoRecord}. The
    * {@link AttributeValue} is found under a ReqIF {@link SpecElementWithAttributes}. This indirectly verifies the
    * presence of the ReqIF {@link SpecObject} or ReqIF {@link Specification}.
    *
    * @param artifactInfoRecordIdentifier The identifier of the {@link ArtifactInfoRecord} containing the expected
    * values.
    * @throws AssertionError when:
    * <ul>
    * <li>the expected {@link AttributeValue} is not present in the test document, or</li>
    * <li>the {@link AttributeValue} is not of the expected type.</li>
    * </ul>
    */

   public static void verifyAttributeValue(Integer artifactInfoRecordIdentifier) {
      var artifactInfoRecord =
         SynchronizationEndpointTest.artifactInfoRecordsByIdentifierMap.get(artifactInfoRecordIdentifier);

      //@formatter:off
      var reqifAttributeValueOptional =
         SynchronizationEndpointTest.reqifAttributeValueByLongNamesMap.get
            (
              artifactInfoRecord.getSpecElementWithAttributesLongName(),
              artifactInfoRecord.getAttributeDefinitionLongName()
            );

      Assert.assertTrue
         (
            "Artifact Not Found, Spec Element Long Name(" + artifactInfoRecord.getSpecElementWithAttributesLongName() + ") Attribute Definition Long Name(" + artifactInfoRecord.getAttributeDefinitionLongName() + ")",
            reqifAttributeValueOptional.isPresent()
         );
      //@formatter:on

      var reqifAttributeValue = reqifAttributeValueOptional.get();

      SynchronizationEndpointTest.verifyAttributeValue(reqifAttributeValue, artifactInfoRecord,
         false /* not default value */ );

      /*
       * Verify the attribute definition reference
       */

      var reqifAttributeDefinition = SynchronizationEndpointTest.getAttributeDefinitionFromEObject(reqifAttributeValue);

      SynchronizationEndpointTest.verifyIdentifiable(artifactInfoRecord, reqifAttributeDefinition);
   }

   /**
    * Verifies the expected ReqIF Data Type Definition is in the test document for the specified
    * {@link ArtifactInfoRecord}. The expected ReqIF {@link DatatypeDefinition} is looked up by its long name field
    * value.
    *
    * @param artifactInfoRecordIdentifier the identifier of the {@link ArtifactInfoRecord} containing the expected
    * values.
    * @param max the expected value for the integer maximum value.
    * @param min the expected value for the integer minimum value.
    * @throws AssertionError when:
    * <ul>
    * <li>a {@link DatatypeDefinition} with the expected long name field value is not in the test document,</li>
    * <li>the found {@link DatatypeDefinition} object does not implement the {@link DatatypeDefinitionInteger}
    * interface,</li>
    * <li>the value of the max field is not as expected, or</li>
    * <li>the value of the min field is not as expected.</li>
    * </ul>
    */

   private static void verifyDatatypeDefinition(Integer artifactInfoRecordIdentifier) {
      var artifactInfoRecord =
         SynchronizationEndpointTest.artifactInfoRecordsByIdentifierMap.get(artifactInfoRecordIdentifier);

      var reqifDatatypeDefinition = SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap.get(
         artifactInfoRecord.getDatatypeDefinitionLongName());

      Assert.assertNotNull(reqifDatatypeDefinition);

      SynchronizationEndpointTest.verifyIdentifiable(artifactInfoRecord, reqifDatatypeDefinition);

      Assert.assertTrue(artifactInfoRecord.getDatatypeDefinitionClass().isInstance(reqifDatatypeDefinition));

      artifactInfoRecord.verifyDatatypeDefinition(reqifDatatypeDefinition);
   }

   @BeforeClass
   public static void testSetup() {
      //@formatter:off

      /*
       * Get OSEE Client
       */

      var oseeClient = OsgiUtil.getService( DemoChoice.class, OseeClient.class );

      /*
       * Get Synchronization Endpoint
       */

      SynchronizationEndpointTest.synchronizationEndpoint = oseeClient.getSynchronizationEndpoint();

      Assert.assertNotNull( SynchronizationEndpointTest.synchronizationEndpoint );

      /*
       * Get Branch end point for test data setup
       */

      var branchEndpoint = oseeClient.getBranchEndpoint();

      /*
       * Get or Create ReqIF test branch
       */

      var testBranch = SynchronizationEndpointTest.getBranchByName( branchEndpoint, SynchronizationEndpointTest.testBranchName )
                          .orElseGet( () -> SynchronizationEndpointTest.createTestBranch( branchEndpoint, SynchronizationEndpointTest.testBranchName ) );

      var testBranchId = BranchId.valueOf( testBranch.getId() );

      /*
       * Get ArtifactEndpoint and RelationEndpoint for the branch
       */

      var artifactEndpoint = oseeClient.getArtifactEndpoint( testBranchId );

      Assert.assertNotNull( artifactEndpoint );

      var relationEndpoint = oseeClient.getRelationEndpoint( testBranchId );

      Assert.assertNotNull( relationEndpoint );

      /*
       * Get Branch Root
       */

      var rootArtifactToken = CoreArtifactTokens.DefaultHierarchyRoot;

      /*
       * Load Artifacts
       */

      /*
       * Map of ArtifactToken objects by ArtifactInfoRecord identifiers. The branch default hierarchical root is saved
       * with the key of 0. This map is used to lookup the hierarchical parent ArtifactToken objects.
       */

      var hierarchicalParentArtifactIdMap = new HashMap<Integer, ArtifactId>();

      hierarchicalParentArtifactIdMap.put( 0, rootArtifactToken );

      /*
       * Map of ArtifactInfoRecord objects by ArtifactIds. This map is used to associate the loaded Artifact objects
       * back to the ArtifactInfoRecord objects.
       */

      var artifactInfoRecordByArtifactIdMap = new HashMap<ArtifactId, ArtifactInfoRecord>();

      /*
       * Load the artifacts and set the test attribute values
       */

      ArtifactLoader.loadArtifacts
         (
           SynchronizationEndpointTest.artifactInfoRecords.stream()
              .peek( artifactInfoRecord -> artifactInfoRecord.getOrCreateArtifactToken( relationEndpoint, artifactEndpoint, testBranchId, hierarchicalParentArtifactIdMap ) )
              .peek( artifactInfoRecord -> artifactInfoRecordByArtifactIdMap.put( artifactInfoRecord.getArtifactToken(), artifactInfoRecord ) )
              .map( ArtifactInfoRecord::getArtifactToken )
              .collect( Collectors.toList() ),
           testBranchId,
           LoadLevel.ALL,
           LoadType.RELOAD_CACHE,
           DeletionFlag.EXCLUDE_DELETED
         ).stream()
             .map( ( artifact ) -> artifactInfoRecordByArtifactIdMap.get( ArtifactId.valueOf( artifact.getId() ) ).setArtifact( artifact ) )
             .peek( ArtifactInfoRecord::getOrCreateAttribute )
             .peek( ArtifactInfoRecord::setAttributeValue )
             .forEach( ArtifactInfoRecord::persistIfDirty );

      /*
       * Save identifiers of test document root
       */

      SynchronizationEndpointTest.rootBranchId = testBranchId;
      SynchronizationEndpointTest.rootArtifactId = ArtifactId.valueOf( SynchronizationEndpointTest.artifactInfoRecords.get( 0 ).getArtifact().getId() );

      /*
       * Create tracking maps
       */

      SynchronizationEndpointTest.reqifAttributeDefinitionByIdentifiersMap = new NamedDoubleHashMap<>( "reqifAttributeDefinitionByIdentifiersMap");
      SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap = new NamedDoubleHashMap<>( "reqifAttributeDefinitionByLongNamesMap");
      SynchronizationEndpointTest.reqifAttributeValueByIdentifiersMap = new NamedDoubleHashMap<>("reqifAttributeValueByIdentifiersMap");
      SynchronizationEndpointTest.reqifAttributeValueByLongNamesMap = new NamedDoubleHashMap<>("reqifAttributeValueByLongNamesMap");
      SynchronizationEndpointTest.reqifDatatypeDefinitionByIdentifierMap = new NamedHashMap<>("reqifDatatypeDefinitionByIdentifierMap");
      SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap = new NamedHashMap<>("reqifDatatypeDefinitionByIdentifierMap");
      SynchronizationEndpointTest.reqifSpecObjectByIdentifierMap = new NamedHashMap<>("reqifSpecObjectByIdentifierMap");
      SynchronizationEndpointTest.reqifSpecObjectByLongNameMap = new NamedHashMap<>("reqifSpecObjectByLongNameMap");
      SynchronizationEndpointTest.reqifSpecTypeByIdentifierMap = new NamedHashMap<>("reqifSpecTypeByIdentifierMap");
      SynchronizationEndpointTest.reqifSpecTypeByLongNameMap = new NamedHashMap<>("reqifSpecTypeByLongNameMap");

      /*
       * Get and save the ReqIF test document from the server
       */

      SynchronizationEndpointTest.reqifTestDocument = SynchronizationEndpointTest.parseTestDocument();

      /*
       * Index the members of the ReqIF by identifier and long name
       */

      SynchronizationEndpointTest.parseSpecTypes
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifSpecTypeByIdentifierMap,
            SynchronizationEndpointTest.reqifSpecTypeByLongNameMap
         );

      SynchronizationEndpointTest.parseDatatypeDefinitions
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifDatatypeDefinitionByIdentifierMap,
            SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap
         );

      SynchronizationEndpointTest.parseAttributeDefinitions
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifAttributeDefinitionByIdentifiersMap,
            SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap
         );

      SynchronizationEndpointTest.parseSpecObjects
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifSpecObjectByIdentifierMap,
            SynchronizationEndpointTest.reqifSpecObjectByLongNameMap
         );

      SynchronizationEndpointTest.parseAttributeValues
         (
            SynchronizationEndpointTest.reqifTestDocument,
            SynchronizationEndpointTest.reqifAttributeValueByIdentifiersMap,
            SynchronizationEndpointTest.reqifAttributeValueByLongNamesMap
         );

      //@formatter:on
   }

   @Test
   public void testGetByBranchIdArtifactIdOk() {
      BranchId branchId = DemoBranches.SAW_PL_Working_Branch;
      ArtifactId artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(branchId,
         artifactId, synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
   }

   @Test
   public void testGetByBranchIdArtifactIdKoBadArtifactType() {
      BranchId branchId = DemoBranches.SAW_PL_Working_Branch;
      ArtifactId artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      String synchronizationArtifactType = "ZooCreatures";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(branchId, artifactId,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("Request for a Synchronization Artifact with an unknown artifact type."));
      }

      Assert.assertTrue(exceptionCought);
   }

   @Test
   public void testGetByRootsOk() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String artifactId = CoreArtifactTokens.DefaultHierarchyRoot.getIdString();
      String roots = branchId + ":" + artifactId;
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
         synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
   }

   @Test
   public void testGetByRootsKoBadRootsNonDigitFirstBranch() {
      String roots = "10a:1,2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void testGetByRootsKoBadRootsNonDigitSecondBranch() {
      String roots = "10:1,2,3;11z:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void testGetByRootsKoBadRootsNonDigitFirstArtifact() {
      String roots = "10:1a,2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void testGetByRootsKoBadRootsNonDigitLastArtifact() {
      String roots = "10:1,2,3;11:4,5,6a";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void testGetByRootsKoBadRootsBranchDelimiterOutOfPlace() {
      String roots = "10:1:2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void testGetByRootsKoBadRootsArtifactDelimiterOutOfPlace() {
      String roots = "10:1,2,3;11,4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void testGetByRootsKoBadRootsSpecificationDelimiterOutOfPlace() {
      String roots = "10:1,2,3;11;4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   /*
    * Artifact Identifier Tests
    */

   @Test
   public void testDataDefinitionArtifactIdentifier() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(2);
   }

   @Test
   public void testAttributeDefinitionArtifactIdentifier() {

      SynchronizationEndpointTest.verifyAttributeDefinition(2);
   }

   @Test
   public void testAttributeValueArtifactIdentifier() {

      SynchronizationEndpointTest.verifyAttributeValue(2);
   }

   /*
    * Boolean Tests
    */

   @Test
   public void testDataDefinitionBoolean() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(3);
   }

   @Test
   public void testAttributeDefinitionBoolean() {

      SynchronizationEndpointTest.verifyAttributeDefinition(3);
   }

   @Test
   public void testAttributeValueBoolean() {

      SynchronizationEndpointTest.verifyAttributeValue(3);
   }

   /*
    * Date Tests
    */

   @Test
   public void testDataDefinitionDate() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(4);
   }

   @Test
   public void testAttributeDefinitionDate() {

      SynchronizationEndpointTest.verifyAttributeDefinition(4);
   }

   @Test
   public void testAttributeValueDate() {

      SynchronizationEndpointTest.verifyAttributeValue(4);
   }

   /*
    * Double Tests
    */

   @Test
   public void testDataDefinitionDouble() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(5);
   }

   @Test
   public void testAttributeDefinitionDouble() {

      SynchronizationEndpointTest.verifyAttributeDefinition(5);
   }

   @Test
   public void testAttributeValueDouble() {

      SynchronizationEndpointTest.verifyAttributeValue(5);
   }

   /*
    * Integer Tests
    */

   @Test
   public void testDataDefinitionInteger() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(6);
   }

   @Test
   public void testAttributeDefinitionInteger() {

      SynchronizationEndpointTest.verifyAttributeDefinition(6);
   }

   @Test
   public void testAttributeValueInteger() {

      SynchronizationEndpointTest.verifyAttributeValue(6);
   }

   /*
    * Long Tests
    */

   @Test
   public void testDataDefinitionLong() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(7);
   }

   @Test
   public void testAttributeDefinitionLong() {

      SynchronizationEndpointTest.verifyAttributeDefinition(7);
   }

   @Test
   public void testAttributeValueLong() {

      SynchronizationEndpointTest.verifyAttributeValue(7);
   }

   /*
    * String Tests
    */

   @Test
   public void testDataDefinitionString() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(8);
   }

   @Test
   public void testAttributeDefinitionString() {

      SynchronizationEndpointTest.verifyAttributeDefinition(8);
   }

   @Test
   public void testAttributeValueString() {

      SynchronizationEndpointTest.verifyAttributeValue(8);
   }

   /*
    * String Word ML Tests
    */

   @Test
   public void testDataDefinitionStringWordML() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(9);
   }

   @Test
   public void testAttributeDefinitionStringWordML() {

      SynchronizationEndpointTest.verifyAttributeDefinition(9);
   }

   @Test
   public void testAttributeValueStringWordML() {

      SynchronizationEndpointTest.verifyAttributeValue(9);
   }

   /*
    * String URI Tests
    */

   @Test
   public void testDataDefinitionStringUri() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(10);
   }

   @Test
   public void testAttributeDefinitionStringUri() {

      SynchronizationEndpointTest.verifyAttributeDefinition(10);
   }

   @Test
   public void testAttributeValueStringUri() {

      SynchronizationEndpointTest.verifyAttributeValue(10);
   }

   /*
    * Enumeration Tests
    */

   @Test
   public void testDataDefinitionEnumeration() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(11);
   }

   @Test
   public void testAttributeDefinitionEnumeration() {

      SynchronizationEndpointTest.verifyAttributeDefinition(11);
   }

}

/* EOF */
