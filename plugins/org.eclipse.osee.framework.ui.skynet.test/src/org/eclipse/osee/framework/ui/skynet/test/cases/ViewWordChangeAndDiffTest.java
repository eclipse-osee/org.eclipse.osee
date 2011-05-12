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
package org.eclipse.osee.framework.ui.skynet.test.cases;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFolder;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareData;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Megumi Telles
 */
public final class ViewWordChangeAndDiffTest {

   private IFolder renderFolder;

   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production database.", TestUtil.isProductionDb());
      RenderingUtil.setPopupsAllowed(false);
      renderFolder = RenderingUtil.ensureRenderFolderExists(PresentationType.DIFF);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testCompareTwoArtifacts() throws Exception {
      SevereLoggingMonitor severeLoggingMonitor = TestUtil.severeLoggingStart();
      Collection<Change> changes = getChanges(getTestBranch());
      ArrayList<Artifact> artifacts = asArtifacts(changes);

      checkPermissions(artifacts);
      try {
         TransactionDelta txDelta = changes.iterator().next().getTxDelta();
         Artifact newerArtifact = loadHistorical(artifacts.get(0));
         Artifact baseArtifact = loadHistorical(artifacts.get(1));

         final Collection<CompareData> testDatas = new ArrayList<CompareData>();

         CompareDataCollector collector = new CompareDataCollector() {

            @Override
            public void onCompare(CompareData data) {
               testDatas.add(data);
            }
         };

         RendererManager.diff(collector, new ArtifactDelta(txDelta, baseArtifact, newerArtifact), "",
            IRenderer.NO_DISPLAY, true);

         TestUtil.sleep(2000);

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
      } catch (Exception ex) {
         fail(String.format("Compare Two Artifacts test failed [%s]", Lib.exceptionToString(ex)));
         throw ex;
      }
      TestUtil.severeLoggingEnd(severeLoggingMonitor);
   }

   private IOseeBranch getTestBranch() throws OseeCoreException {
      // get the changes on the specified branch
      if (BranchManager.branchExists(DemoSawBuilds.SAW_Bld_2)) {
         return DemoSawBuilds.SAW_Bld_2;
      }
      return DemoSawBuilds.SAW_Bld_1;
   }

   private static Collection<Change> getChanges(IOseeBranch testBranch) throws OseeCoreException {
      Collection<Change> changes = new ArrayList<Change>();
      IOperation operation = ChangeManager.comparedToParent(testBranch, changes);
      Operations.executeWorkAndCheckStatus(operation);
      return changes;
   }

   @org.junit.Test
   public void testViewWordChangeReport() throws Exception {
      SevereLoggingMonitor severeLoggingMonitor = TestUtil.severeLoggingStart();
      Collection<Change> changes = getChanges(getTestBranch());
      checkPermissions(asArtifacts(changes));

      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
      RendererManager.diff(artifactDeltas, "testDiff", IRenderer.NO_DISPLAY, true);
      verifyRenderFolderExists();

      TestUtil.severeLoggingEnd(severeLoggingMonitor);
   }

   @org.junit.Test
   public void testSingleNativeDiff() throws Exception {
      SevereLoggingMonitor severeLoggingMonitor = TestUtil.severeLoggingStart();
      Collection<Change> changes = getChanges(getTestBranch());
      Artifact artifact = changes.iterator().next().getChangeArtifact();

      checkPermissions(Collections.singletonList(artifact));

      Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
      RendererManager.diff(artifactDeltas, "", IRenderer.NO_DISPLAY, true);

      verifyRenderFolderExists();

      assertTrue("Single Native Diff test passed", true);
      TestUtil.severeLoggingEnd(severeLoggingMonitor);
   }

   private void verifyRenderFolderExists() {
      Assert.assertNotNull(renderFolder);
      Assert.assertTrue(renderFolder.exists());
   }

   private static Artifact loadHistorical(Artifact artifact) throws OseeCoreException {
      return ArtifactQuery.getHistoricalArtifactFromId(artifact.getArtId(), artifact.getTransactionRecord(),
         INCLUDE_DELETED);
   }

   private static void checkPermissions(List<Artifact> artifacts) throws OseeCoreException {
      boolean isReadable = AccessControlManager.hasPermission(artifacts, PermissionEnum.READ);
      assertTrue("Valid object permissions", isReadable);
   }

   private static ArrayList<Artifact> asArtifacts(Collection<Change> changes) {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      for (Change artifactChange : changes) {
         arts.add(artifactChange.getChangeArtifact());
      }
      return arts;
   }
}
