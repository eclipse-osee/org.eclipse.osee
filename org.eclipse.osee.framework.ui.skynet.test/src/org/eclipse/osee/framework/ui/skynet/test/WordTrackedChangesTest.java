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
package org.eclipse.osee.framework.ui.skynet.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.test2.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.test.cases.WordEditTest;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Megumi Telles
 */
public class WordTrackedChangesTest {
   private static final String TEST_PATH_NAME =
         "../org.eclipse.osee.framework.ui.skynet.test/src/org/eclipse/osee/framework/ui/skynet/test/cases/support/";
   private static final String TEST_WORD_EDIT_FILE_NAME = TEST_PATH_NAME + "WordTrackedChangesTest.xml";
   private static InputStream inputStream;

   /**
    * This test Word Edit's are being saved.
    */
   @BeforeClass
   @org.junit.Test
public void testCleanUpPre() throws Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name()),
            getClass().getSimpleName());
   }

   /*
    * Verifies that the document does not save when it has tracked changes on  
    */

   @org.junit.Test
public void testWordSaveWithTrackChanges() throws Exception {
      assertTrue(
            "This test kills all Word Documents. Cannot continue due to existing open Word Documents." + " Please save and close existing Word Documents before running this test.",
            FrameworkTestUtil.areWinWordsRunning() == false);

      List<Artifact> artifacts = new ArrayList<Artifact>();
      TestUtil.severeLoggingStart();
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name());
      Artifact newArt =
            ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch, getClass().getSimpleName());
      newArt.persistAttributesAndRelations();
      artifacts = Arrays.asList(newArt);
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      renderer = WordEditTest.openArtifacts(artifacts);
      makeChangesToArtifact(renderer, artifacts);
      assertFalse("Detected Tracked Changes Succcessfully",
            WordAttribute.getDisplayTrackedChangesErrorMessage().equals("") == false);
      FrameworkTestUtil.killAllOpenWinword();
      inputStream.close();
   }

   @AfterClass
   @org.junit.Test
public void testCleanUpPost() throws Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_2.name()),
            getClass().getSimpleName());
   }

   public static IFile makeChangesToArtifact(FileRenderer renderer, List<Artifact> artifacts) throws IOException, InterruptedException {
      IFile renderedFile = null;
      try {
         WordAttribute.setNoPopUps(true);
         renderedFile = renderer.getRenderedFile(artifacts, PresentationType.SPECIALIZED_EDIT);
         inputStream = new FileInputStream(TEST_WORD_EDIT_FILE_NAME);
         renderedFile.setContents(inputStream, IResource.FORCE, new NullProgressMonitor());
      } catch (Exception ex) {
         System.out.println(ex.toString());
      }
      return renderedFile;
   }

}
