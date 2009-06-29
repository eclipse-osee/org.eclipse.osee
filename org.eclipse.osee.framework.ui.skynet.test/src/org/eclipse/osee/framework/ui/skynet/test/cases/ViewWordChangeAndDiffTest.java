/*
 * Created on Jun 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.cases;

import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Megumi Telles
 */
public class ViewWordChangeAndDiffTest {
   private boolean isWordRunning = false;
   private Collection<Change> artifactChanges = new ArrayList<Change>();
   private ArrayList<Artifact> baseArtifacts = new ArrayList<Artifact>();
   private ArrayList<Artifact> newerArtifacts = new ArrayList<Artifact>();
   private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
   private Artifact baseArtifact = null;
   private Artifact newerArtifact = null;
   private VariableMap variableMap = new VariableMap();
   private Artifact instanceOfArtifact = null;
   private String fileName = null;

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production database.", TestUtil.isProductionDb());
      isWordRunning = false;
      isWordRunning = FrameworkTestUtil.areWinWordsRunning();
      assertTrue(
            "This test kills all Word Documents. Cannot continue due to existing open Word Documents." + " Please save and close existing Word Documents before running this test.",
            isWordRunning == false);
   }

   @org.junit.Test
   public void testViewWordChangeReport() throws Exception {
      Branch theBranch;
      // get the changes on the specified branch
      if (BranchManager.branchExists(DemoSawBuilds.SAW_Bld_2.name())) {
         theBranch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name());
      } else {
         theBranch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name());
      }
      artifactChanges = ChangeManager.getChangesPerBranch(theBranch, new EmptyMonitor());
      // get the artifacts from the changed list
      artifacts = getArtifacts();
      // make sure permissions are right
      assertTrue("Valid object permissions", (AccessControlManager.getInstance().checkObjectListPermission(artifacts,
            PermissionEnum.READ)));
      // initialize the lists for the test
      initializeViewChangeReportBaseAndNewArtifacts();
      Artifact bArtifact = baseArtifacts.iterator().next();
      Artifact nArtifact = newerArtifacts.iterator().next();
      instanceOfArtifact = bArtifact != null ? bArtifact : nArtifact;
      // set up renderer and compare
      viewWordChangeCompareArtifacts();
      // if we get here there were no exceptions on the compare considered successful
      assertTrue("View Word Change Report test passed", true);
   }

   @org.junit.Test
   public void testSingleNativeDiff() throws Exception {
      artifactChanges =
            ChangeManager.getChangesPerBranch(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()),
                  new EmptyMonitor());
      // get the artifacts from the changed list
      artifacts = getArtifacts();
      // make sure permissions are right
      assertTrue("Valid object permissions", (AccessControlManager.checkObjectPermission(
            artifactChanges.iterator().next().getArtifact(), PermissionEnum.READ)));
      initializeBaseAndNewArtifact(artifactChanges.iterator().next());
      singleNativeDiff(baseArtifact, newerArtifact);
      // if we get here there were no exceptions on the diff considered successful
      assertTrue("Single Native Diff test passed", true);
   }

   @org.junit.Test
   public void testCompareTwoArtifacts() throws Exception {
      try {
         artifactChanges =
               ChangeManager.getChangesPerBranch(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()),
                     new EmptyMonitor());
         // get the artifacts from the changed list
         artifacts = getArtifacts();
         newerArtifact =
               ArtifactPersistenceManager.getInstance().getArtifactFromId(artifacts.get(0).getArtId(),
                     artifacts.get(0).getTransactionId());
         baseArtifact =
               ArtifactPersistenceManager.getInstance().getArtifactFromId(artifacts.get(1).getArtId(),
                     artifacts.get(1).getTransactionId());
         RendererManager.diffInJob(baseArtifact, newerArtifact);
         // if we get here there were no exceptions on the diff considered successful
         assertTrue("Compare Two Artifacts test passed", true);
      } catch (Exception ex) {
         fail("Compare Two Artifacts test failed");
         throw ex;
      }

   }

   /**
    * @throws java.lang.Exception
    */
   @After
   public void tearDown() throws Exception {
      if (!isWordRunning) {
         Thread.sleep(7000);
         FrameworkTestUtil.killAllOpenWinword();
      }
   }

   private ArrayList<Artifact> getArtifacts() throws ArtifactDoesNotExist {
      ArrayList<Artifact> arts = new ArrayList<Artifact>();
      for (Change artifactChange : artifactChanges) {
         arts.add(artifactChange.getArtifact());
      }
      return arts;
   }

   private void initializeViewChangeReportBaseAndNewArtifacts() {
      for (Change artifactChange : artifactChanges) {
         try {
            initializeBaseAndNewArtifact(artifactChange);
         } catch (OseeCoreException ex1) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex1);
            fail("Initialization of base and new artifacts failed");
         }
      }
   }

   private void initializeBaseAndNewArtifact(Change artifactChange) throws ArtifactDoesNotExist, OseeCoreException {
      baseArtifact =
            (artifactChange.getModificationType() == NEW || artifactChange.getModificationType() == ModificationType.INTRODUCED) ? null : ArtifactPersistenceManager.getInstance().getArtifactFromId(
                  artifactChange.getArtifact().getArtId(), artifactChange.getFromTransactionId());

      newerArtifact =
            artifactChange.getModificationType() == DELETED ? null : (artifactChange.isHistorical() ? ArtifactPersistenceManager.getInstance().getArtifactFromId(
                  artifactChange.getArtifact().getArtId(), artifactChange.getToTransactionId()) : artifactChange.getArtifact());

      baseArtifacts.add(baseArtifact);
      newerArtifacts.add(newerArtifact);

      if (fileName == null) {
         if (artifactChanges.size() == 1) {
            fileName = baseArtifact != null ? baseArtifact.getSafeName() : newerArtifact.getSafeName();
         } else {
            fileName =
                  baseArtifact != null ? baseArtifact.getBranch().getBranchShortName() : newerArtifact.getBranch().getBranchShortName();
         }
         variableMap.setValue("fileName", fileName + "_" + (new Date()).toString().replaceAll(":", ";") + ".xml");
      }
   }

   private void viewWordChangeCompareArtifacts() {
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      try {
         renderer.setOptions(variableMap);
         renderer.compareArtifacts(baseArtifacts, newerArtifacts, new NullProgressMonitor(),
               instanceOfArtifact.getBranch(), PresentationType.DIFF);
      } catch (OseeCoreException e) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, e);
         fail("View Word Change Report test failed");
      }
   }

   private void singleNativeDiff(Artifact baseArtifact, Artifact newerArtifact) {
      try {
         RendererManager.diff(baseArtifact, newerArtifact, true);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         fail("Single Native Diff test failed.");
      }
   }

}
