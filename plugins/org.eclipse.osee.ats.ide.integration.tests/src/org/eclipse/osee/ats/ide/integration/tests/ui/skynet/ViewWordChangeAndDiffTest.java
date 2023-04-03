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
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotForEclipseOrgRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.RendererUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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

   private IFolder renderFolder;
   private BranchId branch;
   private Map<RendererOption, Object> rendererOptions;

   @Before
   public void setUp() throws Exception {
      renderFolder = RendererUtil.ensureRenderFolderExists(PresentationType.DIFF).orElseThrow();
      branch = SAW_Bld_2;
      rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.NO_DISPLAY, true);
   }

   @Test
   public void testCompareTwoArtifacts() throws Exception {
      Collection<Change> changes = getChanges(branch);
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
      Collection<Change> changes = getChanges(branch);
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
      Collection<Change> changes = getChanges(branch);
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
