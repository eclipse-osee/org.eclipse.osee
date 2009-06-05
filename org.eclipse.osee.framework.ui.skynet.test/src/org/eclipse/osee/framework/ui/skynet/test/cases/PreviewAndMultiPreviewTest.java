/*
 * Created on Jun 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test2.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.PreviewWithChildWordHandler;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Megumi Telles
 */
public class PreviewAndMultiPreviewTest extends TestCase {
   private List<Artifact> artifacts = new ArrayList<Artifact>();
   private Artifact newArt;
   private SevereLoggingMonitor monitorLog = null;
   private Branch branch;
   private boolean isWordRunning = false;

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      isWordRunning = FrameworkTestUtil.areWinWordsRunning();
      assertTrue(
            "This test kills all Word Documents. Cannot continue due to existing open Word Documents." + " Please save and close existing Word Documents before running this test.",
            isWordRunning == false);
      init();
   }

   private void init() throws Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name()),
            getClass().getSimpleName());
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name());
      // create a new requirement artifact
      newArt = ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch, getClass().getSimpleName());
      newArt.persistAttributesAndRelations();
      artifacts = Arrays.asList(newArt);
   }

   /*
    * Preview Requirements Artifact (includes a child artifact == general document but should not invoke a
    * warning since only previewing (no recurse)).
    */
   public void testPreview() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            Artifact childArt =
                  ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName() + "1");
            newArt.addChild(childArt);
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(null);
            renderer.preview(artifacts);
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 0);
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Preview with children test failed.");
         }
      } else {
         fail("Preview with children test failed.  There were no artifacts to preview.");
      }
   }

   /*
    * Preview Requirements Artifact with valid children. 
    */
   public void testPreviewWithChildrenFault() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            Artifact childArt =
                  ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch,
                        getClass().getSimpleName() + "1");
            newArt.addChild(childArt);
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(new VariableMap(ITemplateRenderer.PREVIEW_WITH_RECURSE_OPTION_PAIR));
            renderer.preview(artifacts);
            // should get one warning since the child is a general document
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 0);
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Preview with children test failed.");
         }
      } else {
         fail("Preview with children test failed.  There were no artifacts to preview.");
      }
   }

   /*
    * Preview Requirement Artifact with a general document child.  Expect Warning.  
    */
   public void testPreviewWithChildren() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            Artifact childArt =
                  ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName() + "1");
            newArt.addChild(childArt);
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(new VariableMap(ITemplateRenderer.PREVIEW_WITH_RECURSE_OPTION_PAIR));
            renderer.preview(artifacts);
            // should get one warning since the child is a general document
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 1);
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).get(0).getMessage().contains(
                  "You chose to preview/edit artifacts that could not be handled"));
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Preview with children test failed.");
         }
      } else {
         fail("Preview with children test failed.  There were no artifacts to preview.");
      }
   }

   /*
    * Preview multiple Requirement Artifacts
    */
   public void testMultiPreview() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            List<Artifact> newMultiArts = new ArrayList<Artifact>();
            Artifact multiArt1 =
                  ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch,
                        getClass().getSimpleName() + "3");
            Artifact multiArt2 =
                  ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch,
                        getClass().getSimpleName() + "2");
            Artifact multiArt3 =
                  ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch,
                        getClass().getSimpleName() + "1");
            newMultiArts = Arrays.asList(multiArt1, multiArt2, multiArt3);
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(null);
            renderer.preview(newMultiArts);
            // should get one warning since the child is a general document
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 0);
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Preview with children test failed.");
         }
      } else {
         fail("Preview with children test failed.  There were no artifacts to preview.");
      }
   }

   public void testWholeWordPreview() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            List<Artifact> arts = new ArrayList<Artifact>();
            Artifact art =
                  ArtifactTypeManager.addArtifact("Test Procedure WML", branch, getClass().getSimpleName() + "4");
            arts = Arrays.asList(art);
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(null);
            renderer.preview(arts);
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 0);
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Preview with children test failed.");
         }
      } else {
         fail("Preview with children test failed.  There were no artifacts to preview.");
      }
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      if (!isWordRunning) {
         FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name()),
               getClass().getSimpleName());
         TestUtil.severeLoggingEnd(monitorLog);
         Thread.sleep(7000);
         FrameworkTestUtil.killAllOpenWinword();
      }
   }

}
