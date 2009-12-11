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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
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
public class WordTrackedChangesTest {
   private static final String TEST_PATH_NAME =
         "../org.eclipse.osee.framework.ui.skynet.test/src/org/eclipse/osee/framework/ui/skynet/test/cases/support/";
   private static final String TEST_WORD_EDIT_FILE_NAME = TEST_PATH_NAME + "WordTrackedChangesTest.xml";
   private static final String TEST_GEN_WORD_EDIT_FILE_NAME = TEST_PATH_NAME + "GeneralWordTrackedChangesTest.doc";

   /**
    * This test Word Edit's are being saved.
    */
   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1),
            WordTrackedChangesTest.class.getSimpleName());
      WordAttribute.setDisplayTrackedChangesErrorMessage("");
      WholeDocumentRenderer.setNoPopups(true);
      WordTemplateRenderer.setNoPopups(true);
      FileSystemRenderer.setNoPopups(true);
   }

   /*
    * Verifies that the document does not save when it has tracked changes on
    */

   @org.junit.Test
   public void testWordSaveWithTrackChanges() throws Exception {

      List<Artifact> artifacts = new ArrayList<Artifact>();
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
      // create a new requirement artifact
      Artifact newArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch, getClass().getSimpleName());
      newArt.persist();
      artifacts = Arrays.asList(newArt);
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      makeChangesToArtifact(renderer, TEST_WORD_EDIT_FILE_NAME, artifacts);
      Thread.sleep(5000);
      assertTrue("Did not detect Tracked Changes Succcessfully",
            WordAttribute.getDisplayTrackedChangesErrorMessage().contains(
                  "Cannot save - Detected tracked changes on this artifact. ") == true);
      TestUtil.severeLoggingEnd(monitorLog);
   }

   /*
    * Verifies that on a general document the save was success with tracked changes
    */
   @org.junit.Test
   public void testGeneralWordSaveWithTrackChanges() throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getCommonBranch();
      // create a new general document artifact
      Artifact newArt = ArtifactTypeManager.addArtifact("General Document", branch, getClass().getSimpleName());
      newArt.persist();
      artifacts = Arrays.asList(newArt);
      FileRenderer renderer = RendererManager.getBestFileRenderer(PresentationType.SPECIALIZED_EDIT, newArt);
      makeChangesToArtifact(renderer, TEST_GEN_WORD_EDIT_FILE_NAME, artifacts);
      Thread.sleep(10000);
      assertTrue("Did not Detect Tracked Changes",
            WordAttribute.getDisplayTrackedChangesErrorMessage().equals("") == true);
      TestUtil.severeLoggingEnd(monitorLog);
   }

   /*
    * Verifies that a whole word document cannot save with tracked changes on
    */
   @org.junit.Test
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      WordAttribute.setNoPopUps(true);
      InputStream inputStream = new FileInputStream(TEST_WORD_EDIT_FILE_NAME);
      String content = Lib.inputStreamToString(inputStream);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
      // create a new requirement artifact
      Artifact newArt = ArtifactTypeManager.addArtifact("Test Procedure WML", branch, getClass().getSimpleName());
      content = WordMlLinkHandler.unlink(linkType, newArt, content);
      try {
         newArt.setSoleAttributeFromString(WordAttribute.WHOLE_WORD_CONTENT, content);
         newArt.persist();
      } catch (OseeArgumentException ex) {
         if (ex.getLocalizedMessage().equals("Cannot save - Detected tracked changes on this artifact. ")) {
            assertTrue("Did not Detect Tracked Changes", WordAttribute.getDisplayTrackedChangesErrorMessage().contains(
                  "Cannot save - Detected tracked changes on this artifact. ") == true);
            newArt.purgeFromBranch();
         } else {
            throw ex;
         }
      } finally {
         TestUtil.severeLoggingEnd(monitorLog);
      }
   }

   @After
   public void tearDown() throws Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid()),
            WordTrackedChangesTest.class.getSimpleName());
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getCommonBranch(), WordTrackedChangesTest.class.getSimpleName());
   }

   public static IFile makeChangesToArtifact(FileRenderer renderer, String file, List<Artifact> artifacts) throws IOException, InterruptedException {
      IFile renderedFile = null;
      try {
         WordAttribute.setNoPopUps(true);
         renderedFile = renderer.getRenderedFile(artifacts, PresentationType.SPECIALIZED_EDIT);
         InputStream inputStream = new FileInputStream(file);
         final IFile rFile = renderedFile;
         rFile.setContents(inputStream, IResource.FORCE, new NullProgressMonitor());
         inputStream.close();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         FrameworkTestUtil.killAllOpenWinword();
      }
      return renderedFile;
   }

}
