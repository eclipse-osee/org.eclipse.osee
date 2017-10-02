/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static java.lang.Thread.sleep;
import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
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
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime.Units;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public final class ViewWordChangeAndDiffTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private IFolder renderFolder;
   private BranchId branch;
   private Map<RendererOption, Object> rendererOptions;

   @Before
   public void setUp() throws Exception {
      renderFolder = RenderingUtil.ensureRenderFolderExists(PresentationType.DIFF);
      branch = SAW_Bld_2;
      rendererOptions = new HashMap<>();
      rendererOptions.put(RendererOption.NO_DISPLAY, true);
   }

   @Test
   public void testCompareTwoArtifacts() throws Exception {
      ElapsedTime time = new ElapsedTime("testPurgeTransaction", true);
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

      RendererManager.diff(collector, new ArtifactDelta(txDelta, baseArtifact, newerArtifact), "", rendererOptions);

      sleep(2000);

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
      time.end(Units.MIN);
      time.end(Units.SEC);
   }

   @Test
   public void testViewWordChangeReport() throws Exception {
      ElapsedTime time = new ElapsedTime("testPurgeTransaction", true);
      Collection<Change> changes = getChanges(branch);
      checkPermissions(asArtifacts(changes));

      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
      RendererManager.diff(artifactDeltas, "testDiff", rendererOptions);
      verifyRenderFolderExists();
      time.end(Units.MIN);
      time.end(Units.SEC);
   }

   @Test
   public void testSingleNativeDiff() throws Exception {
      ElapsedTime time = new ElapsedTime("testPurgeTransaction", true);
      Collection<Change> changes = getChanges(branch);
      Artifact artifact = changes.iterator().next().getChangeArtifact();

      checkPermissions(Collections.singletonList(artifact));

      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
      RendererManager.diff(artifactDeltas, "", rendererOptions);

      verifyRenderFolderExists();

      assertTrue("Single Native Diff test passed", true);
      time.end(Units.MIN);
      time.end(Units.SEC);
   }

   private static Collection<Change> getChanges(BranchId testBranch)  {
      Collection<Change> changes = new ArrayList<>();
      IOperation operation = ChangeManager.comparedToParent(testBranch, changes);
      Operations.executeWorkAndCheckStatus(operation);
      return changes;
   }

   private void verifyRenderFolderExists() {
      Assert.assertNotNull(renderFolder);
      Assert.assertTrue(renderFolder.exists());
   }

   private static Artifact loadHistorical(Artifact artifact)  {
      return ArtifactQuery.getHistoricalArtifactFromId(artifact, artifact.getTransaction(), INCLUDE_DELETED);
   }

   private static void checkPermissions(List<Artifact> artifacts)  {
      boolean isReadable = AccessControlManager.hasPermission(artifacts, PermissionEnum.READ);
      assertTrue("Valid object permissions", isReadable);
   }

   private static ArrayList<Artifact> asArtifacts(Collection<Change> changes) {
      ArrayList<Artifact> arts = new ArrayList<>();
      for (Change artifactChange : changes) {
         arts.add(artifactChange.getChangeArtifact());
      }
      return arts;
   }
}
