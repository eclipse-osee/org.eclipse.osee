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

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.BuilderRecord;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.BuilderRelationshipRecord;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.EnumFunctionMap;
import org.eclipse.osee.framework.jdk.core.util.RankHashMap;
import org.eclipse.osee.framework.jdk.core.util.RankMap;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactReferenceAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
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
import org.eclipse.rmf.reqif10.SpecElementWithAttributes;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;
import org.eclipse.rmf.reqif10.XhtmlContent;
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

   private static class ArtifactInfoRecord implements BuilderRecord {

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
       * The values to be assigned to the test attributes.
       */

      private final List<Object> testAttributeValues;

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
            List<Object>                           testAttributeValues,
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

            BiConsumer<Attribute<?>,Object>        attributeSetter,
            Function<Object,Object>                backToOseeTypeFunction
         )
      {
         this.identifier = Objects.requireNonNull(identifier);
         this.hierarchicalParentIdentifier = Objects.requireNonNull(hierarchicalParentIdentifier);
         this.name = Objects.requireNonNull(name);
         this.typeToken = Objects.requireNonNull(typeToken);
         this.testAttributeType = Objects.requireNonNull(testAttributeType);
         this.testAttributeValues = Objects.requireNonNull(testAttributeValues);
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
      }
      //@formatter:on

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

      public String getAttributeDefinitionDescription() {
         return this.attributeDefinitionDescription;
      }

      /**
       * Gets the expected identifier prefix for the ReqIF {@link AttributeDefinition} that should be generated in the
       * test document for the test artifact.
       *
       * @return the expected identifier prefix.
       */

      public String getAttributeDefinitionIdentifierPrefix() {
         return this.attributeDefinitionIdentifierPrefix;
      }

      /**
       * Gets the expected long name for the ReqIF {@link AttributeDefinition} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected long name.
       */

      public String getAttributeDefinitionLongName() {
         return this.attributeDefinitionLongName;
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

      public String getDatatypeDefinitionDescription() {
         return this.datatypeDefinitionDescription;
      }

      /**
       * Gets the expected identifier prefix for the ReqIF {@link DatatypeDefinition} that should be generated in the
       * test document for the test artifact.
       *
       * @return the expected identifier prefix.
       */

      public String getDatatypeDefinitionIdentifierPrefix() {
         return this.datatypeDefinitionIdentifierPrefix;
      }

      /**
       * Gets the expected long name for the ReqIF {@link DatatypeDefinition} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected long name.
       */

      public String getDatatypeDefinitionLongName() {
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

      @Override
      public Integer getIdentifier() {
         return this.identifier;
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

      public String getSpecTypeDescription() {
         return this.specTypeDescription;
      }

      /**
       * Gets the expected identifier prefix for the ReqIF {@link SpecType} that should be generated in the test
       * document for the test artifact.
       *
       * @return the expected identifier prefix.
       */

      public String getSpecTypeIdentifierPrefix() {
         return this.specTypeIdentifierPrefix;
      }

      /**
       * Gets the expected long name for the ReqIF {@link SpecType} that should be generated in the test document for
       * the test artifact.
       *
       * @return the expected long name.
       */

      public String getSpecTypeLongName() {
         return this.specTypeLongName;
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
       * Removes the first value on the <code>objectList</code> that compares with the provided object using the method
       * {@link TestUtil#compareAttributeValue}.
       *
       * @param objectList the list of objects.
       * @param object the value to search for.
       * @return <code>true</code>, when a value match was found and an object was removed from the list; otherwise,
       * <code>false</code>.
       */

      private boolean removeFirstMatch(List<Object> objectList, Object object) {
         for (int i = 0, l = objectList.size(); i < l; i++) {
            if (TestUtil.compareAttributeValue(objectList.get(i), object)) {
               objectList.remove(i);
               return true;
            }
         }

         return false;
      }

      /**
       * Verifies the specified value or values match the expected attribute values of the test artifact.
       *
       * @param reqifValue the value to be checked or an {@link EList} containing the value or values to be checked.
       * @param checkDefaultValue when <code>true</code>, the provided values are compared to the expected default
       * value; otherwise, the provided values are compared with the test artifact's expected attribute values.
       * @return <code>true</code> when the provided value or values match the expected value; otherwise,
       * <code>false</code>.
       */

      private boolean verifyExpectedValue(Object reqifValue, Boolean checkDefaultValue) {

         if (checkDefaultValue || this.testAttributeValues.size() == 1) {
            /*
             * Expected value is a scalar
             */

            //@formatter:off
            var oseeValue = ( reqifValue instanceof EList )
                               ? this.backToOseeTypeFunction.apply( ((EList<?>) reqifValue).get( 0 ) )
                               : this.backToOseeTypeFunction.apply( reqifValue );
            //@formatter:on

            var expectedValue = checkDefaultValue ? this.testAttributeDefaultValue : this.testAttributeValues.get(0);

            return TestUtil.compareAttributeValue(oseeValue, expectedValue);

         } else {
            /*
             * Expected value is a vector
             */

            @SuppressWarnings("unchecked")
            var oseeValues =
               ((List<Object>) reqifValue).stream().map(this.backToOseeTypeFunction).collect(Collectors.toList());

            if (this.testAttributeValues.size() != oseeValues.size()) {
               return false;
            }

            for (var expectedValue : this.testAttributeValues) {
               if (!this.removeFirstMatch(oseeValues, expectedValue)) {
                  return false;
               }
            }

            return (oseeValues.size() == 0);
         }

      }

      /**
       * A wrapper on the {@link #verifyExpectedValue} method with a JUnit assertion and an error message in case the
       * values do not compare.
       *
       * @param reqifValue the value to be checked or an {@link EList} containing the value or values to be checked.
       * @param checkDefaultValue checkDefaultValue when <code>true</code>, the provided values are compared to the
       * expected default value; otherwise, the provided values are compared with the test artifact's expected attribute
       * values.
       */

      private void assertExpectedValue(Object reqifValue, Boolean checkDefaultValue) {
         Assert.assertTrue("ReqIfValue is not expected: " + reqifValue.toString(),
            this.verifyExpectedValue(reqifValue, checkDefaultValue));
      }

      @Override
      public ArtifactTypeToken getArtifactTypeToken() {
         return this.typeToken;
      }

      @Override
      public BiConsumer<Attribute<?>, Object> getAttributeSetter() {
         return this.attributeSetter;
      }

      @Override
      public List<BuilderRelationshipRecord> getBuilderRelationshipRecords() {
         return null;
      }

      @Override
      public Integer getHierarchicalParentIdentifier() {
         return this.hierarchicalParentIdentifier;
      }

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public AttributeTypeGeneric<?> getTestAttributeType() {
         return this.testAttributeType;
      }

      @Override
      public List<Object> getTestAttributeValues() {
         return this.testAttributeValues;
      }

   } /* End of ArtifactInfoRecord */

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
                    1,                                            /* Identifier                             (Integer)                               */
                    0,                                            /* Hierarchical Parent Identifier         (Integer)                               */
                    "ReqIF Test Specifications",                  /* Artifact Name                          (String)                                */
                    CoreArtifactTypes.Folder,                     /* Artifact Type                          (ArtifactTypeToken)                     */
                    CoreAttributeTypes.Description,               /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                    List.of( "Motley folder of test artifacts" ), /* Test Attribute Values                  (List<Object>)                          */
                    null,                                         /* Test Attribute Default Value           {Object}                                */
                    "",                                           /* Spec Type Description                  (String)                                */
                    "ST-",                                        /* Spec Type Identifier Prefix            (String)                                */
                    "",                                           /* Spec Type Long Name                    (String)                                */
                    "",                                           /* Attribute Definition Description       (String)                                */
                    "AD-",                                        /* Attribute Definition Identifier Prefix (String)                                */
                    "",                                           /* Attribute Definition Long Name         (String)                                */
                    AttributeDefinitionInteger.class,             /* Attribute Definition Class             (Class<? extends AttributeDefinition)   */
                    "",                                           /* Datatype Definition Description        (String)                                */
                    "DD-",                                        /* Datatype Definition Identifier Prefix  (String)                                */
                    "",                                           /* Datatype Definition Long Name          (String)                                */
                    DatatypeDefinitionInteger.class,              /* Datatype Definition Class              (Class<? extends DatatypeDefinition)    */
                    null,                                         /* Datatype Definition Verifier           (Consumer<? super DatatypeDefinition)   */
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
                    List.of( ArtifactId.valueOf( 1938 ) ),
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
                    List.of( true ),
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
                    List.of( SynchronizationEndpointTest.getTestDate() ),
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
                    List.of( 8.75 ),
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
                    List.of( 42 ),
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
                    List.of( 420L ),
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
                    List.of( "Three cats are required for all great software developments." ),
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
                    List.of( "Three cats are required for all great software developments." ),
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
                    List.of( "http://org.eclipse.org" ),
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
                    List.of( "VMS/Flight Control" ),
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
                    ( reqifValue ) -> SynchronizationEndpointTest.reqifEnumValueLongNameByIdentifierMap.get( ((EnumValue) reqifValue).getIdentifier() ).orElseThrow()
                  ),

                  new ArtifactInfoRecord
                  (
                    12,
                    1,
                    "MULTI_VALUE_ENUM_TESTER",
                    CoreArtifactTypes.SoftwareRequirementHtml,
                    CoreAttributeTypes.QualificationMethod,
                    List.of( "Demonstration", "Test", "Analysis", "Inspection", "Similarity", "Pass Thru", "Special Qualification", "Legacy" ),
                    null,
                    "OSEE Software Requirement - HTML Spec Object Type",
                    "SOT-",
                    "Software Requirement - HTML",
                    "Demonstration:  The operation of the CSCI, or a part of the CSCI, that relies on observable functional operation not requiring the use of instrumentation, special test equipment, or subsequent analysis.\012\012Test:  The operation of the CSCI, or a part of the CSCI, using instrumentation or other special test equipment to collect data for later analysis.\012\012Analysis:  The processing of accumulated data obtained from other qualification methods.  Examples are reduction, interpretation, or extrapolation of test results.\012\012Inspection:  The visual examination of CSCI code, documentation, etc.\012\012Special Qualification Methods:  Any special qualification methods for the CSCI, such as special tools, techniques, procedures, facilities, and acceptance limits.\012\012Legacy:  Requirement, design, or implementation has not changed since last qualification (use sparingly - Not to be used with functions implemented in internal software).\012\012Unspecified:  The qualification method has yet to be set.",
                    "AD-",
                    "Qualification Method",
                    AttributeDefinition.class,
                    "OSEE Enumerated Datatype",
                    "DD-",
                    "ENUMERATED-1152921504606847113",
                    DatatypeDefinition.class,
                    ( datatypeDefinition ) ->
                    {
                       var reqifDatatypeDefinitionEnumeration = (DatatypeDefinitionEnumeration) datatypeDefinition;

                       var enumMemberMap = reqifDatatypeDefinitionEnumeration.getSpecifiedValues().stream().collect( Collectors.toMap( EnumValue::getLongName, Function.identity() ) );

                       var ordinalAtomicInteger = new AtomicInteger( 0 );

                       // Enumeration members must be listed in ordinal order

                       Arrays.stream( new String[] { "Demonstration", "Test", "Analysis", "Inspection", "Similarity", "Pass Thru", "Special Qualification", "Legacy", "Unspecified" } ).forEach
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
                    ( reqifValue ) -> SynchronizationEndpointTest.reqifEnumValueLongNameByIdentifierMap.get( ((EnumValue) reqifValue).getIdentifier() ).orElseThrow()
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

   private static RankMap<AttributeDefinition> reqifAttributeDefinitionByIdentifiersMap;

   /**
    * ReqIF Attribute Definitions are specific to ReqIF Specification Types and Spec Object Types. This is a map of the
    * ReqIF Attribute Definitions in the test document keyed by the ReqIF Specification Type or ReqIF Spec Object Type
    * long name and then by the ReqIF Attribute Definition long name.
    */

   private static RankMap<AttributeDefinition> reqifAttributeDefinitionByLongNamesMap;

   /**
    * ReqIF Attribute Values are specific to ReqIF Specification and Spec Objects. This is a map of the ReqIF Attribute
    * Values in the test document keyed by the ReqIF Specification or ReqIF Spec Object identifier and then by the ReqIF
    * Attribute Value's Attribute Definition reference Identifier.
    */

   private static RankMap<AttributeValue> reqifAttributeValueByIdentifiersMap;

   /**
    * ReqIF Attribute Values are specific to ReqIF Specification and Spec Objects. This is a map of the ReqIF Attribute
    * Values in the test document keyed by the ReqIF Specification or ReqIF Spec Object long name and then by the ReqIF
    * Attribute Value's Attribute Definition reference long name.
    */

   private static RankMap<AttributeValue> reqifAttributeValueByLongNamesMap;

   /**
    * Map of ReqIF Data Type Definitions from the test document keyed by their identifiers.
    */

   private static RankMap<DatatypeDefinition> reqifDatatypeDefinitionByIdentifierMap;

   /**
    * Map of ReqIF Data Type Definitions from the test document keyed by their long names.
    */

   private static RankMap<DatatypeDefinition> reqifDatatypeDefinitionByLongNameMap;

   /**
    * Map of ReqIF EnumValues from the test document keyed by the identifiers.
    */

   private static RankMap<String> reqifEnumValueLongNameByIdentifierMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their identifiers. This map does not include the ReqIF
    * Specifications.
    */

   private static RankMap<SpecObject> reqifSpecObjectByIdentifierMap;

   /**
    * Map of ReqIF Spec Objects from the test document keyed by their long names. This map does not include the ReqIF
    * Specifications.
    */

   private static RankMap<SpecObject> reqifSpecObjectByLongNameMap;

   /**
    * Map of ReqIF Specification Type and Spec Object Type definitions from the test document keyed by their
    * identifiers.
    */

   private static RankMap<SpecType> reqifSpecTypeByIdentifierMap;

   /**
    * Map of ReqIF Specification Type and Spec Object Type definitions from the test document keyed by their long names.
    */

   private static RankMap<SpecType> reqifSpecTypeByLongNameMap;

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
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for ReqIF Synchronizaion Artifact Testing";

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
         //@formatter:off
         var declaredMethodsMap =
            Arrays
               .stream( reqifAttributeValue.getClass().getDeclaredMethods() )
               .collect( Collectors.toMap( Method::getName, Function.identity() ) );
         //@formatter:on

         //@formatter:off
         var attributeValueIsSet =
            declaredMethodsMap.containsKey("isSetTheValue")
               ? (Boolean) reqifAttributeValue.getClass().getDeclaredMethod("isSetTheValue").invoke(reqifAttributeValue)
               : declaredMethodsMap.containsKey("isSetValues")
                    ? (Boolean) reqifAttributeValue.getClass().getDeclaredMethod("isSetValues").invoke(reqifAttributeValue)
                    : null;
         //@formatter:on

         Assert.assertTrue((attributeValueIsSet != null) && attributeValueIsSet);

         //@formatter:off
         var value = declaredMethodsMap.containsKey("getTheValue")
                        ? declaredMethodsMap.get("getTheValue").invoke(reqifAttributeValue)
                        : declaredMethodsMap.containsKey("getValues")
                             ? declaredMethodsMap.get( "getValues" ).invoke( reqifAttributeValue )
                             : declaredMethodsMap.containsKey("isTheValue")
                                  ? declaredMethodsMap.get("isTheValue").invoke(reqifAttributeValue)
                                  : null;
         //@formatter:on

         artifactInfoRecord.assertExpectedValue(value, checkDefaultValue);

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
         artifactInfoRecord.getDatatypeDefinitionLongName()).orElse( null );

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
         artifactInfoRecord.getDatatypeDefinitionLongName()).orElse(null);

      Assert.assertNotNull(reqifDatatypeDefinition);

      SynchronizationEndpointTest.verifyIdentifiable(artifactInfoRecord, reqifDatatypeDefinition);

      Assert.assertTrue(artifactInfoRecord.getDatatypeDefinitionClass().isInstance(reqifDatatypeDefinition));

      artifactInfoRecord.verifyDatatypeDefinition(reqifDatatypeDefinition);
   }

   @SuppressWarnings("unchecked")
   @BeforeClass
   public static void testSetup() {
      //@formatter:off

      var testDocumentBuilder = new TestDocumentBuilder( SynchronizationEndpointTest.setValues );

      testDocumentBuilder.buildDocument
                             (
                                (List<BuilderRecord>) (Object) SynchronizationEndpointTest.artifactInfoRecords,
                                SynchronizationEndpointTest.testBranchName,
                                SynchronizationEndpointTest.testBranchCreationComment
                             );

      /*
       * Save identifiers of test document root
       */

      SynchronizationEndpointTest.rootBranchId = testDocumentBuilder.getRootBranchId();
      SynchronizationEndpointTest.rootArtifactId = testDocumentBuilder.getRootArtifactId();

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
       * Create tracking maps
       */

      SynchronizationEndpointTest.reqifAttributeDefinitionByIdentifiersMap = new RankHashMap<>( "reqifAttributeDefinitionByIdentifiersMap",2, 256, 0.75f, KeyPredicates.keysAreStringsRank2);
      SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap = new RankHashMap<>( "reqifAttributeDefinitionByLongNamesMap",2, 256, 0.75f, KeyPredicates.keysAreStringsRank2);
      SynchronizationEndpointTest.reqifAttributeValueByIdentifiersMap = new RankHashMap<>("reqifAttributeValueByIdentifiersMap",2, 256, 0.75f, KeyPredicates.keysAreStringsRank2);
      SynchronizationEndpointTest.reqifAttributeValueByLongNamesMap = new RankHashMap<>("reqifAttributeValueByLongNamesMap",2, 256, 0.75f, KeyPredicates.keysAreStringsRank2);
      SynchronizationEndpointTest.reqifDatatypeDefinitionByIdentifierMap = new RankHashMap<>("reqifDatatypeDefinitionByIdentifierMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);
      SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap = new RankHashMap<>("reqifDatatypeDefinitionByIdentifierMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);
      SynchronizationEndpointTest.reqifEnumValueLongNameByIdentifierMap = new RankHashMap<>("reqifEnumValueLongNameByIdentifierMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);
      SynchronizationEndpointTest.reqifSpecObjectByIdentifierMap = new RankHashMap<>("reqifSpecObjectByIdentifierMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);
      SynchronizationEndpointTest.reqifSpecObjectByLongNameMap = new RankHashMap<>("reqifSpecObjectByLongNameMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);
      SynchronizationEndpointTest.reqifSpecTypeByIdentifierMap = new RankHashMap<>("reqifSpecTypeByIdentifierMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);
      SynchronizationEndpointTest.reqifSpecTypeByLongNameMap = new RankHashMap<>("reqifSpecTypeByLongNameMap",1, 256, 0.75f, KeyPredicates.keysAreStringsRank1);

      /*
       * Get and save the ReqIF test document from the server
       */

      var synchronizationArtifactParser = new SynchronizationArtifactParser( SynchronizationEndpointTest.synchronizationEndpoint );

      synchronizationArtifactParser.parseTestDocument( SynchronizationEndpointTest.rootBranchId, SynchronizationEndpointTest.rootArtifactId, "reqif" );

      /*
       * Index the members of the ReqIF by identifier and long name
       */

      synchronizationArtifactParser.parseSpecTypes
         (
            SpecType.class,
            SynchronizationEndpointTest.reqifSpecTypeByIdentifierMap,
            SynchronizationEndpointTest.reqifSpecTypeByLongNameMap
         );

      synchronizationArtifactParser.parseDatatypeDefinitions
         (
            SynchronizationEndpointTest.reqifDatatypeDefinitionByIdentifierMap,
            SynchronizationEndpointTest.reqifDatatypeDefinitionByLongNameMap,
            SynchronizationEndpointTest.reqifEnumValueLongNameByIdentifierMap
         );

      synchronizationArtifactParser.parseAttributeDefinitions
         (
            SynchronizationEndpointTest.reqifAttributeDefinitionByIdentifiersMap,
            SynchronizationEndpointTest.reqifAttributeDefinitionByLongNamesMap
         );

      synchronizationArtifactParser.parseSpecObjects
         (
            SynchronizationEndpointTest.reqifSpecObjectByIdentifierMap,
            SynchronizationEndpointTest.reqifSpecObjectByLongNameMap
         );

      synchronizationArtifactParser.parseAttributeValues
         (
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

   @Test
   public void testAttributeValueEnumeration() {

      SynchronizationEndpointTest.verifyAttributeValue(11);
   }

   @Test
   public void testDataDefinitionMultiValueEnumeration() {

      SynchronizationEndpointTest.verifyDatatypeDefinition(12);
   }

   @Test
   public void testAttributeDefinitionMultiValueEnumeration() {

      SynchronizationEndpointTest.verifyAttributeDefinition(12);
   }

   @Test
   public void testAttributeValueMultiValueEnumeration() {

      SynchronizationEndpointTest.verifyAttributeValue(12);
   }

}

/* EOF */
