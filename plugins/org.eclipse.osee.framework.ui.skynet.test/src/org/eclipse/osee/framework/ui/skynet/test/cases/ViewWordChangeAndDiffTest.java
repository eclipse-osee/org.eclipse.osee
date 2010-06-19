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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Before;

/**
 * @author Megumi Telles
 */
public class ViewWordChangeAndDiffTest {

   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production database.", TestUtil.isProductionDb());
      RenderingUtil.setPopupsAllowed(false);
   }

   private Branch getTestBranch() throws OseeCoreException {
      // get the changes on the specified branch
      if (BranchManager.branchExists(DemoSawBuilds.SAW_Bld_2)) {
         return BranchManager.getBranch(DemoSawBuilds.SAW_Bld_2);
      }
      return BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
   }

   private static Collection<Change> getChanges(Branch testBranch) throws OseeCoreException {
      Collection<Change> changes = new ArrayList<Change>();
      IOperation operation = ChangeManager.comparedToParent(testBranch, changes);
      Operations.executeWorkAndCheckStatus(operation, new NullProgressMonitor(), -1.0);
      return changes;
   }

   @org.junit.Test
   public void testViewWordChangeReport() throws Exception {
      Collection<Change> changes = getChanges(getTestBranch());
      checkPermissions(asArtifacts(changes));

      Collection<ArtifactDelta> itemsToCompare = ChangeManager.getCompareArtifacts(changes);
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      try {
         VariableMap variableMap = new VariableMap();
         variableMap.setValue("suppressWord", true);
         renderer.setOptions(variableMap);
         renderer.getComparator().compareArtifacts(new NullProgressMonitor(), PresentationType.DIFF, itemsToCompare);
      } catch (OseeCoreException e) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, e);
         fail("View Word Change Report test failed");
      }

      // if we get here there were no exceptions on the compare considered successful
      assertTrue("View Word Change Report test passed", true);
   }

   @org.junit.Test
   public void testSingleNativeDiff() throws Exception {
      Collection<Change> changes = getChanges(getTestBranch());
      Artifact artifact = changes.iterator().next().getChangeArtifact();

      checkPermissions(Collections.singletonList(artifact));

      Collection<ArtifactDelta> itemsToCompare = ChangeManager.getCompareArtifacts(changes);
      ArtifactDelta delta = itemsToCompare.iterator().next();
      try {
         RendererManager.diff(delta, false);
         assertTrue("Single Native Diff test passed", true);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         fail("Single Native Diff test failed.");
      }
   }

   @org.junit.Test
   public void testCompareTwoArtifacts() throws Exception {
      Collection<Change> changes = getChanges(getTestBranch());
      ArrayList<Artifact> artifacts = asArtifacts(changes);

      checkPermissions(artifacts);
      try {
         TransactionDelta txDelta = changes.iterator().next().getTxDelta();
         Artifact newerArtifact = loadHistorical(artifacts.get(0));
         Artifact baseArtifact = loadHistorical(artifacts.get(1));

         RendererManager.diff(new ArtifactDelta(txDelta, baseArtifact, newerArtifact), false);

         assertTrue("Compare Two Artifacts test passed", true);
      } catch (Exception ex) {
         fail("Compare Two Artifacts test failed");
         throw ex;
      }
   }

   private static Artifact loadHistorical(Artifact artifact) throws OseeCoreException {
      return ArtifactQuery.getHistoricalArtifactFromId(artifact.getArtId(), artifact.getTransactionRecord(), true);
   }

   private static void checkPermissions(List<Artifact> artifacts) throws OseeCoreException {
      boolean isReadable = AccessControlManager.checkObjectListPermission(artifacts, PermissionEnum.READ);
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
