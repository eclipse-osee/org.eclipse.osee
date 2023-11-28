/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import static java.lang.Thread.sleep;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.ArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.AttributeSetters;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicArtifactSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicAttributeSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BasicBranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.BranchSpecificationRecord;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestDocumentBuilder;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotForEclipseOrgRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.MapList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

/**
 * @author Megumi Telles
 */
public final class ViewWordChangeAndDiffTest {

   /**
    * Class level testing rules are applied before the {@link #testSetup} method is invoked. These rules are used for
    * the following:
    * <dl>
    * <dt>Not Production Data Store Rule</dt>
    * <dd>This rule is used to prevent modification of a production database.</dd>
    * <dt>ExitDatabaseInitializationRule</dt>
    * <dd>This rule will exit database initialization mode and re-authenticate as the test user when necessary.</dd>
    * {@Link UserToken} cache has been flushed.</dd></dt>
    */

   //@formatter:off
   @ClassRule
   public static TestRule classRuleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( new NotForEclipseOrgRule() ) //<--ToDo: Remove with TW22315
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

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   /**
    * The {@link BranchSpecificationRecord} identifier for the parent test branch.
    */

   private static int parentBranchSpecificationRecordIdentifier = 1;

   /**
    * The {@link BranchSpecificationRecord} identifier for the child test branch.
    */

   private static int childBranchSpecificationRecordIdentifier = 2;

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
                      ViewWordChangeAndDiffTest.parentBranchSpecificationRecordIdentifier,  /* BranchSpecificationRecord Identifier */
                      "ViewWordChangeAndDiffTestParentBranch",                              /* Branch Name                          */
                      "Parent Branch for ViewWordChangeAndDiffTest"                         /* Branch Creation Comment              */
                    ),

            new BasicBranchSpecificationRecord
                   (
                      ViewWordChangeAndDiffTest.childBranchSpecificationRecordIdentifier,   /* BranchSpecificationRecord Identifier */
                      "ViewWordChangeAndDiffTestChildBranch",                               /* Branch Name                          */
                      "Child Branch for ViewWordChangeAndDiffTest",                         /* Branch Creation Comment              */
                      ViewWordChangeAndDiffTest.parentBranchSpecificationRecordIdentifier   /* Parent Branch Identifier             */
                   )
         );
   //@formatter:on

   /**
    * MapList of {@ArtifactSpecificationRecord}s describing the test artifacts for each branch.
    * <p>
    * Artifacts are created in the list order. Follow the rules:
    * <ul>
    * <li>Ensure identifiers are unique.</li>
    * <li>The identifier 0 is reserved.</li>
    * <li>Ensure hierarchical parents are at lower list indices.</li>
    * <li>Top level test artifact have a hierarchical parent identifier of 0.</li>
    * <li>Ensure children artifact's of a hierarchical parent artifact have unique names.</li>
    * </ul>
    */

   //@formatter:off
   private static final MapList<Integer,ArtifactSpecificationRecord> artifactSpecifications =
      MapList.ofEntries
         (
            /*
             * Artifacts for the parent (baseline) test branch.
             */

            Map.entry
               (
                  ViewWordChangeAndDiffTest.parentBranchSpecificationRecordIdentifier,                              /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Preview Artifacts Folder",                                                       /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "This folder contains artifacts for publishing preview tests."
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                       new BasicArtifactSpecificationRecord
                              (
                                 2,                                                                                /* Identifier                             (Integer)                               */
                                 1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                 "Requirement A",                                                                  /* Artifact Name                          (String)                                */
                                 CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                 List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                    (
                                       new BasicAttributeSpecificationRecord
                                              (
                                                CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                   (
                                                      "This is Requirement A's Description."
                                                   ),
                                                AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                              ),
                                       new BasicAttributeSpecificationRecord
                                              (
                                                CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                   (
                                                      "<w:p><w:r><w:t>This is Requirement A's WordTemplateContent.</w:t></w:r></w:p>"
                                                   ),
                                                AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                              )
                                    ),
                                 List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                              ),

                       new BasicArtifactSpecificationRecord
                              (
                                 3,                                                                                /* Identifier                             (Integer)                               */
                                 1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                 "Requirement B",                                                                  /* Artifact Name                          (String)                                */
                                 CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                 List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                    (
                                       new BasicAttributeSpecificationRecord
                                              (
                                                CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                   (
                                                      "This is Requirement B's Description."
                                                   ),
                                                AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                              ),
                                       new BasicAttributeSpecificationRecord
                                              (
                                                CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                   (
                                                      "<w:p><w:r><w:t>This is Requirement B's WordTemplateContent.</w:t></w:r></w:p>"
                                                   ),
                                                AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                              )
                                    ),
                                 List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                              )
                     )
               ),

            /*
             * Artifacts for the child (working) test branch.
             */

            Map.entry
               (
                  ViewWordChangeAndDiffTest.childBranchSpecificationRecordIdentifier,                               /* Test Branch Identifier                 (Integer)                               */
                  List.of
                     (
                        new BasicArtifactSpecificationRecord
                               (
                                  1,                                                                                /* Identifier                             (Integer)                               */
                                  0,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Preview Artifacts Folder",                                                       /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.Folder,                                                         /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "This folder contains artifacts for publishing preview tests."
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  2,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement A",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "This is Requirement A's Description."
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "<w:p><w:r><w:t>This is changed Requirement A's WordTemplateContent.</w:t></w:r></w:p>"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               ),

                        new BasicArtifactSpecificationRecord
                               (
                                  3,                                                                                /* Identifier                             (Integer)                               */
                                  1,                                                                                /* Hierarchical Parent Identifier         (Integer)                               */
                                  "Requirement B",                                                                  /* Artifact Name                          (String)                                */
                                  CoreArtifactTypes.SoftwareRequirementMsWord,                                      /* Artifact Type                          (ArtifactTypeToken)                     */
                                  List.of                                                                           /* Attribute Specifications               (List<AttributeSpecificationRecord>)    */
                                     (
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.Description,                                    /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "This is Requirement B's Description."
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               ),
                                        new BasicAttributeSpecificationRecord
                                               (
                                                 CoreAttributeTypes.WordTemplateContent,                            /* Test Attribute Type                    (AttributeTypeGeneric<?>)               */
                                                 List.of                                                            /* Test Attribute Values                  (List<Object>)                          */
                                                    (
                                                       "<w:p><w:r><w:t>This Requirement B's WordTemplateContent.</w:t></w:r></w:p>"
                                                    ),
                                                 AttributeSetters.stringAttributeSetter                             /* AttributeSetter                        (BiConsumer<Attribute<?>,Object>)       */
                                               )
                                     ),
                                  List.of()                                                                         /* BuilderRelationshipRecords             (List<BuilderRelationshipRecords>)      */
                               )
                     )
               )
         );
   //@formatter:on

   private IFolder renderFolder;
   private RendererMap rendererOptions;

   private static boolean setValues = true;
   private static BranchId rootBranchId;
   private static ArtifactId rootArtifactId;

   @BeforeClass
   public static void testSetup() {

      /*
       * Create the Test Artifacts
       */

      var testDocumentBuilder = new TestDocumentBuilder(ViewWordChangeAndDiffTest.setValues);

      //@formatter:off
      testDocumentBuilder.buildDocument
         (
            ViewWordChangeAndDiffTest.branchSpecifications,
            ViewWordChangeAndDiffTest.artifactSpecifications
         );

      /*
       * Save identifiers of test document root
       */

      //@formatter:off
      ViewWordChangeAndDiffTest.rootBranchId =
         testDocumentBuilder
            .getBranchIdentifier
               (
                  ViewWordChangeAndDiffTest.childBranchSpecificationRecordIdentifier
               )
            .get();

      ViewWordChangeAndDiffTest.rootArtifactId =
         testDocumentBuilder
            .getArtifactIdentifier
               (
                  ViewWordChangeAndDiffTest.childBranchSpecificationRecordIdentifier,
                  1
               )
            .get();

   }

   @Before
   public void setUp() throws Exception {
      this.renderFolder = RendererUtil.ensureRenderFolderExists(PresentationType.DIFF).orElseThrow();
      this.rendererOptions = new EnumRendererMap(RendererOption.NO_DISPLAY, true);
   }

   @Test
   public void testCompareTwoArtifacts() throws Exception {
      Collection<Change> changes = getChanges(ViewWordChangeAndDiffTest.rootBranchId);
      ArrayList<Artifact> artifacts = asArtifacts(changes);

      checkPermissions(artifacts);
      TransactionDelta txDelta = changes.iterator().next().getTxDelta();
      Artifact newerArtifact = loadHistorical(artifacts.get(0));
      Artifact baseArtifact = loadHistorical(artifacts.get(1));

      final Collection<CompareData> testDatas = new ArrayList<>();

      CompareDataCollector collector = new CompareDataCollector() {

         @Override
         public void onCompare(CompareData data) {
            testDatas.add(data);
         }
      };

      var testFolder = this.testName.getMethodName();

      //@formatter:off
      RendererManager.diff
         (
            collector,
            new ArtifactDelta( txDelta, baseArtifact, newerArtifact ),
            testFolder,
            rendererOptions
         );
      //@formatter:on

      /*
       * Have to provide time for the Visual Basic Script and Word to complete the differences.
       */

      sleep(8000);

      Assert.assertEquals(1, testDatas.size());
      CompareData testData = testDatas.iterator().next();

      Assert.assertEquals(1, testData.size());
      Entry<String, String> fileSet = testData.entrySet().iterator().next();

      File vbScript = new File(testData.getGeneratorScriptPath());
      File outPut = new File(testData.getOutputPath());
      File file1 = new File(fileSet.getKey());
      File file2 = new File(fileSet.getValue());

      Assert.assertTrue(vbScript.exists());
      Assert.assertTrue(file1.exists());
      Assert.assertTrue(file2.exists());

      if (Lib.isWindows()) {
         Assert.assertTrue(outPut.exists());
      }
   }

   @Test
   public void testViewWordChangeReport() throws Exception {
      Collection<Change> changes = getChanges(ViewWordChangeAndDiffTest.rootBranchId);
      checkPermissions(asArtifacts(changes));

      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);

      var testFolder = this.testName.getMethodName();

      //@formatter:off
      RendererManager.diff
         (
            artifactDeltas,
            testFolder,
            rendererOptions
         );
      //@formatter:on

      verifyRenderFolderExists();
   }

   @Test
   public void testSingleNativeDiff() throws Exception {
      Collection<Change> changes = getChanges(ViewWordChangeAndDiffTest.rootBranchId);
      Artifact artifact = changes.iterator().next().getChangeArtifact();

      checkPermissions(Collections.singletonList(artifact));

      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);

      var testFolder = this.testName.getMethodName();

      //@formatter:off
      RendererManager.diff
         (
            artifactDeltas,
            testFolder,
            rendererOptions
         );
      //@formatter:on

      verifyRenderFolderExists();

      assertTrue("Single Native Diff test passed", true);
   }

   private static Collection<Change> getChanges(BranchId testBranch) {
      Collection<Change> changes = new ArrayList<>();
      IOperation operation =
         ChangeManager.comparedToPreviousTx(TransactionManager.getHeadTransaction(testBranch), changes);
      Operations.executeWorkAndCheckStatus(operation);
      return changes;
   }

   private void verifyRenderFolderExists() {
      Assert.assertNotNull(renderFolder);
      Assert.assertTrue(renderFolder.exists());
   }

   private static Artifact loadHistorical(Artifact artifact) {
      return ArtifactQuery.getHistoricalArtifactFromId(artifact, artifact.getTransaction(), INCLUDE_DELETED);
   }

   private static void checkPermissions(List<Artifact> artifacts) {
      boolean isReadable = ServiceUtil.getOseeClient().getAccessControlService().hasArtifactPermission(artifacts,
         PermissionEnum.READ, null).isSuccess();
      assertTrue("Valid object permissions", isReadable);
   }

   private static ArrayList<Artifact> asArtifacts(Collection<Change> changes) {
      ArrayList<Artifact> arts = new ArrayList<>();
      for (Change artifactChange : changes) {
         Artifact changeArtifact = artifactChange.getChangeArtifact();
         if (changeArtifact.isValid()) {
            arts.add(changeArtifact);
         }
      }
      return arts;
   }
}
