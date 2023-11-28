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

package org.eclipse.osee.ats.ide.integration.tests.framework.core;

import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.osee;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeMapEntry;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * Performs write read test on various attribute types.
 *
 * @author Loren K. Ashley
 */

public class FrameworkCoreAttributeTest {

   /**
    * Set this flag to <code>false</code> to prevent the test setup code from altering attribute values in the database.
    * The default (normal for testing) value is <code>true</code>.
    *
    * @implNote This test purges the test branch upon completion. When <code>setValue</code> is set to
    * <code>false</code> the {@link BranchManager#purgeBranch} call in the method {@link #testCleanup} should be
    * commented out.
    */

   private static boolean setValues = true;

   /**
    * The base value used to create the identifiers for the test artifacts and test attributes. Hopefully the test
    * identifiers don't ever conflict with system Artifact or Attribute identifiers.
    */

   private static long magic = 5497559238391627776L;

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>ExitDatabaseInitializationRule</dt>
    * <dd>This rule will exit database initialization mode and re-authenticate as the test user when necessary.</dd>
    * </dl>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         ;
   //@formatter:on

   /**
    * Wrap the test methods with a check to prevent execution on a production database.
    */

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   /**
    * A rule to get the method name of the currently running test.
    */

   @Rule
   public TestName testName = new TestName();

   /**
    * Saves a {@link Map} of the artifacts associated with each artifact {@link ArtifactSpecificationRecord}.
    */

   private static Map<Integer, Optional<Artifact>> builderRecordMap;

   /**
    * The {@link ArtifactTypeToken} implementation for testing the Date attribute type.
    */

   //@formatter:off
   private static AttributeTypeDate DateTestAttribute =
      osee.createDate
         (
            FrameworkCoreAttributeTest.magic + 101L,
            "Date",
            MediaType.TEXT_PLAIN,
            "Description"
         );
   //@formatter:on

   /**
    * The {@link ArtifactTypeToken} for the test artifact artifact for the {@link DateTestAttribute}.
    */

   //@formatter:off
   private static ArtifactTypeToken DateTestArtifact =
      osee.add
         (
            osee
               .artifactType( FrameworkCoreAttributeTest.magic + 1L, "DateTestArtifact", false, CoreArtifactTypes.Artifact )
               .zeroOrOne(DateTestAttribute)
         );
   //@formatter:on

   /**
    * The {@link ArtifactTypeToken} implementation for testing the Map Entry attribute type.
    */

   //@formatter:off
   private static AttributeTypeMapEntry MapEntryTestAttribute =
      osee.createMapEntry
         (
            FrameworkCoreAttributeTest.magic + 100L,
            "Map Entry",
            "Description",
            "defaultKey",
            "defaultValue"
         );

   /**
    * The {@link ArtifactTypeToken} for the test artifact for the {@link MapEntryTestAttribute}.
    */

   //@formatter:off
   private static ArtifactTypeToken MapEntryTestArtifact =
      osee.add
         (
            osee
               .artifactType( FrameworkCoreAttributeTest.magic + 0L, "MapEntryTestArtifact", false, CoreArtifactTypes.Artifact )
               .any(MapEntryTestAttribute)
         );
   //@formatter:on

   /**
    * Saves the {@link ArtifactId} of the root artifact of the test document.
    */

   @SuppressWarnings("unused")
   private static ArtifactId rootArtifactId;

   /**
    * Saves the {@link BranchId} of the root artifact of the test document.
    */

   private static BranchId rootBranchId;

   /**
    * Creation comment used for the OSEE test branch
    */

   private static String testBranchCreationComment = "Branch for Framework Core Attribute Type Testing";

   /**
    * Name used for the OSEE branch holding the test document.
    */

   private static String testBranchName = "Framework Core Attribute Type Test Branch";

   /**
    * The {@link BranchSpecificationRecord} identifier for the test branch.
    */

   private static int testBranchSpecificationRecordIdentifier = 1;

   /**
    * List of {@link BranchSpecificationRecord} implementations describing the branches for the test.
    * <p>
    * Branches are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * </ul>
    */

   //@formatter:off
   private static final List<BranchSpecificationRecord> branchSpecifications =
      List.of
         (
            new BasicBranchSpecificationRecord
                   (
                      FrameworkCoreAttributeTest.testBranchSpecificationRecordIdentifier,
                      FrameworkCoreAttributeTest.testBranchName,
                      FrameworkCoreAttributeTest.testBranchCreationComment
                   )
         );
   //@formatter:on

   /**
    * {@link BuilderRecord} objects to define the artifacts to be created on the test branch for the tests.
    */

   //@formatter:off
   @SuppressWarnings("deprecation")
   private static MapList<Integer,ArtifactSpecificationRecord> artifactSpecifications =
      MapList.ofEntries
         (
            /*
             * Artifacts for the test branch.
             */

            Map.entry
               (
                  FrameworkCoreAttributeTest.testBranchSpecificationRecordIdentifier,                           /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                           (
                              1,                                                                                /* Identifier                             (Integer)                               */
                              0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Map Entry Test Artifact",                                                        /* Artifact Name                          (String)                                */
                              MapEntryTestArtifact,                                                             /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             MapEntryTestAttribute,                                             /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   Map.entry( "text",   "text statement"   ),
                                                   Map.entry( "wordml", "wordml statement" )
                                                ),
                                             AttributeSetters.mapEntryAttributeSetter                           /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           ),

                        new BasicArtifactSpecificationRecord
                           (
                              2,                                                                                /* Identifier                             (Integer)                               */
                              0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                              "Date Test Artifact",                                                             /* Artifact Name                          (String)                                */
                              DateTestArtifact,                                                                 /* Artifact Type                          (ArtifactTypeToken)                     */
                              List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                 (
                                    new BasicAttributeSpecificationRecord
                                           (
                                             DateTestAttribute,                                                 /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                             List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                (
                                                   new Date(-178,1,22)
                                                ),
                                             AttributeSetters.dateAttributeSetter                               /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                           )
                                 ),
                              List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                           )

                     )
               )
         );
   //@formatter:on

   /**
    * Purges the test branch at the completion of the tests.
    *
    * @implNote The Artifacts created for the test will be unknown to any client. The test branch is purged to prevent
    * errors in the client if an attempt is made to access the test branch.
    */

   @AfterClass
   public static void testCleanup() {

      BranchManager.purgeBranch(FrameworkCoreAttributeTest.rootBranchId);
   }

   @BeforeClass
   public static void testSetup() {

      var orcsTokenService = OsgiUtil.getService(DemoChoice.class, OrcsTokenService.class);

      orcsTokenService.registerArtifactType(MapEntryTestArtifact);
      orcsTokenService.registerAttributeType(MapEntryTestAttribute);

      orcsTokenService.registerArtifactType(DateTestArtifact);
      orcsTokenService.registerAttributeType(DateTestAttribute);

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(FrameworkCoreAttributeTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            FrameworkCoreAttributeTest.branchSpecifications,
            FrameworkCoreAttributeTest.artifactSpecifications
         );

      FrameworkCoreAttributeTest.rootBranchId =
         testDocumentBuilder
            .getBranchIdentifier
               (
                  FrameworkCoreAttributeTest.testBranchSpecificationRecordIdentifier
               )
            .get();

      FrameworkCoreAttributeTest.builderRecordMap =
         FrameworkCoreAttributeTest
            .artifactSpecifications
            .stream( FrameworkCoreAttributeTest.testBranchSpecificationRecordIdentifier )
            .map( ArtifactSpecificationRecord::getIdentifier )
            .collect
               (
                  Collectors.toMap
                     (
                        Function.identity(),
                        ( builderRecordIdentifier ) -> testDocumentBuilder.getArtifact
                                                     (
                                                        FrameworkCoreAttributeTest.testBranchSpecificationRecordIdentifier,
                                                        builderRecordIdentifier
                                                     )
                     )
               );
      //@formatter:on
   }

   @Test
   public void testDateAttribute() {
      var testArtifactIdentifier = FrameworkCoreAttributeTest.builderRecordMap.get(2).get();

      //@formatter:off
      var artifactList =
         ArtifactLoader.loadArtifacts
            (
               List.of( ArtifactId.valueOf( testArtifactIdentifier ) ),
               FrameworkCoreAttributeTest.rootBranchId,
               LoadLevel.ALL,
               LoadType.RELOAD_CACHE,
               DeletionFlag.EXCLUDE_DELETED
            );
      //@formatter:on

      Assert.assertNotNull("Failed to load test artifact.", artifactList);
      Assert.assertEquals("Failed to load test artifact.", 1, artifactList.size());

      var artifact = artifactList.get(0);

      Assert.assertNotNull("Failed to load test artifact.", artifact);

      List<Date> list = artifact.getAttributeValues(DateTestAttribute);

      Assert.assertNotNull("Failed to get attribute values.", list);
      Assert.assertEquals("Failed to get attribute values.", 1, list.size());

      var date = list.get(0);

      Assert.assertNotNull("Failed to get attribute values.", date);
      Assert.assertEquals(date.toString(), "1722-02-22");
   }

   @Test
   public void testMapEntryAttribute() {
      var testArtifactIdentifier = FrameworkCoreAttributeTest.builderRecordMap.get(1).get();

      //@formatter:off
      var artifactList =
         ArtifactLoader.loadArtifacts
            (
               List.of( ArtifactId.valueOf( testArtifactIdentifier ) ),
               FrameworkCoreAttributeTest.rootBranchId,
               LoadLevel.ALL,
               LoadType.RELOAD_CACHE,
               DeletionFlag.EXCLUDE_DELETED
            );
      //@formatter:on

      Assert.assertNotNull("Failed to load test artifact.", artifactList);
      Assert.assertEquals("Failed to load test artifact.", artifactList.size(), 1);

      var artifact = artifactList.get(0);

      Assert.assertNotNull("Failed to load test artifact.", artifact);

      List<Map.Entry<String, String>> list = artifact.getAttributeValues(MapEntryTestAttribute);

      Assert.assertNotNull("Failed to get attribute values.", list);
      Assert.assertEquals("Failed to get attribute values.", 2, list.size());

      var map = list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      Assert.assertNotNull("Failed to get attribute values.", map);
      Assert.assertEquals("Failed to get attribute values.", 2, map.size());

      Assert.assertEquals("text statement", map.get("text"));
      Assert.assertEquals("wordml statement", map.get("wordml"));
   }

}

/* EOF */
