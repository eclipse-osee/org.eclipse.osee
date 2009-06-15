/*
 * Created on Jun 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.cases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers.PreviewWithChildWordHandler;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WholeDocumentRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Megumi Telles
 */
public class PreviewAndMultiPreviewTest {
   private static List<Artifact> artifacts = new ArrayList<Artifact>();
   private static Artifact newArt;
   private static SevereLoggingMonitor monitorLog = null;
   private static Branch branch;
   private static boolean isWordRunning = false;

   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      isWordRunning = FrameworkTestUtil.areWinWordsRunning();
      assertTrue(
            "This test kills all Word Documents. Cannot continue due to existing open Word Documents." + " Please save and close existing Word Documents before running this test.",
            isWordRunning == false);
      init();
   }

   private void init() throws Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()),
            PreviewAndMultiPreviewTest.class.getSimpleName());
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name());
      // create a new requirement artifact
      newArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch,
                  PreviewAndMultiPreviewTest.class.getSimpleName());
      newArt.persistAttributesAndRelations();
      artifacts = Arrays.asList(newArt);
   }

   /*
    * Preview Requirements Artifact (includes a child artifact == general document but should not invoke a
    * warning since only previewing (no recurse)).
    */
   @org.junit.Test
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

   @org.junit.Test
   public void testPreviewUsingRendererManager() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            Artifact childArt =
                  ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName() + "1");
            newArt.addChild(childArt);
            RendererManager.previewInJob(artifacts);
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
   @org.junit.Test
   public void testPreviewWithChildren() throws Exception {
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

   @org.junit.Test
   public void testPreviewWithChildrenUsingRendererManager() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            Artifact childArt =
                  ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch,
                        getClass().getSimpleName() + "1");
            newArt.addChild(childArt);
            RendererManager.previewInJob(artifacts);
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

   @org.junit.Test
   public void testPreviewWithChildrenFault() throws Exception {
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
            assertTrue(TestUtil.getNumberOfLogsAtLevel(monitorLog, Level.SEVERE) == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Preview with children test failed.");
         }
      } else {
         fail("Preview with children test failed.  There were no artifacts to preview.");
      }
   }

   /*
    * No warning expected in this fault case because the Renderer Manager resolves which
    * render to use to preview.  
    */
   @org.junit.Test
   public void testPreviewWithChildrenUsingRendererManagerFault() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            Artifact childArt =
                  ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName() + "1");
            newArt.addChild(childArt);
            RendererManager.previewInJob(artifacts);
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
    * Preview multiple Requirement Artifacts
    */
   @org.junit.Test
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

   @org.junit.Test
   public void testMultiPreviewUsingRendererManager() throws Exception {
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
            RendererManager.previewInJob(artifacts);
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
    * Preview a whole word doc 
    */
   @org.junit.Test
   public void testWholeWordPreview() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            List<Artifact> arts = new ArrayList<Artifact>();
            Artifact art =
                  ArtifactTypeManager.addArtifact("Test Procedure WML", branch, getClass().getSimpleName() + "4");
            arts = Arrays.asList(art);
            WholeDocumentRenderer renderer = new WholeDocumentRenderer();
            renderer.setOptions(null);
            renderer.preview(arts);
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 0);
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Whole Word Preview test failed.");
         }
      } else {
         fail("Whoile Word Test failed.");
      }
   }

   @org.junit.Test
   public void testWholeWordPreviewUsingRendererManager() throws Exception {
      if (!artifacts.isEmpty()) {
         try {
            monitorLog = TestUtil.severeLoggingStart();
            List<Artifact> arts = new ArrayList<Artifact>();
            Artifact art =
                  ArtifactTypeManager.addArtifact("Test Procedure WML", branch, getClass().getSimpleName() + "4");
            arts = Arrays.asList(art);
            RendererManager.previewInJob(artifacts);
            assertTrue(monitorLog.getLogsAtLevel(Level.WARNING).size() == 0);
            assertTrue(monitorLog.getLogsAtLevel(Level.SEVERE).size() == 0);
         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWithChildWordHandler.class, Level.SEVERE, ex);
            fail("Whole Word Preview test failed.");
         }
      } else {
         fail("Whoile Word Test failed.");
      }
   }

   @After
   public void tearDown() throws Exception {
      if (!isWordRunning) {
         FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()),
               PreviewAndMultiPreviewTest.class.getSimpleName());
         TestUtil.severeLoggingEnd(monitorLog);
         Thread.sleep(7000);
         FrameworkTestUtil.killAllOpenWinword();
      }
   }

}
