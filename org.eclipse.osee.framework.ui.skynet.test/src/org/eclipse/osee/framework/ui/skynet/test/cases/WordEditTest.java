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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.After;
import org.junit.Before;

/**
 * @author Paul K. Waldfogel
 * @author Megumi Telles
 */
public class WordEditTest {

   private static final String TEST_PATH_NAME =
         "../org.eclipse.osee.framework.ui.skynet.test/src/org/eclipse/osee/framework/ui/skynet/test/cases/support/";
   private static final String TEST_WORD_EDIT_FILE_NAME = TEST_PATH_NAME + "WordEditTest.xml";
   private static InputStream inputStream;
   private boolean isWordRunning = false;

   /**
    * This test Word Edit's are being saved.
    */
   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      isWordRunning = false;
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()),
            getClass().getSimpleName());
      isWordRunning = FrameworkTestUtil.areWinWordsRunning();
      assertTrue(
            "This test kills all Word Documents. Cannot continue due to existing open Word Documents." + " Please save and close existing Word Documents before running this test.",
            isWordRunning == false);
   }

   @org.junit.Test
   public void testEditUsingWord() throws Exception {
      // use word template renderer
      probeWordEditingCapability(false, getClass().getSimpleName());
      // use renderer manager
      probeWordEditingCapability(true, getClass().getSimpleName());
   }

   @After
   public void tearDown() throws Exception {
      if (!isWordRunning) {
         WordAttribute.setDisplayTrackedChangesErrorMessage("");
         FrameworkTestUtil.cleanupSimpleTest(BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name()),
               getClass().getSimpleName());
         FrameworkTestUtil.killAllOpenWinword();
      }
   }

   public static String convertStreamToString(InputStream is) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      StringBuilder sb = new StringBuilder();
      String line = null;
      try {
         while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            is.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return sb.toString();
   }

   public static List<Artifact> getArtifactsToChange(Branch branch) throws OseeCoreException {
      List<Artifact> arts = new ArrayList<Artifact>();
      Collection<Change> changes = ChangeManager.getChangesPerBranch(branch, null);
      for (Change change : changes) {
         Artifact art = change.getArtifact();
         if (art.isOfType(Requirements.ABSTRACT_SOFTWARE_REQUIREMENT)) {
            arts.add(art);
         }
      }
      return arts;
   }

   public static WordTemplateRenderer openArtifacts(List<Artifact> artifacts) throws OseeCoreException {
      WordTemplateRenderer renderer = new WordTemplateRenderer();
      renderer.open(artifacts);
      return renderer;
   }

   public static IFile makeChangesToArtifact(FileRenderer renderer, List<Artifact> artifacts) throws IOException, InterruptedException {
      IFile renderedFile = null;
      try {
         renderedFile = renderer.getRenderedFile(artifacts, PresentationType.SPECIALIZED_EDIT);
         inputStream = new FileInputStream(TEST_WORD_EDIT_FILE_NAME);
         renderedFile.setContents(inputStream, IResource.FORCE, new NullProgressMonitor());
      } catch (Exception ex) {
         System.out.println(ex.toString());
      }
      return renderedFile;
   }

   public static void probeWordEditingCapability(boolean useRendererManager, String className) throws IOException, InterruptedException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      Branch branch = null;
      IFile renderedFile = null;
      InputStream preStream = null;
      InputStream istream = null;
      WordTemplateRenderer postRenderer = null;
      FileRenderer preRenderer = null;
      String postStreamStr = "";
      try {
         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
         FileRenderer.setWorkbenchSavePopUpDisabled(true);
         branch = BranchManager.getKeyedBranch(DemoSawBuilds.SAW_Bld_1.name());
         Artifact newArt = ArtifactTypeManager.addArtifact(Requirements.SOFTWARE_REQUIREMENT, branch, className);
         newArt.persistAttributesAndRelations();
         // open the artifacts; testing one artifact for now
         artifacts = Arrays.asList(newArt);
         if (useRendererManager) {
            RendererManager.openInJob(artifacts, PresentationType.SPECIALIZED_EDIT);
            preRenderer = RendererManager.getBestFileRenderer(PresentationType.SPECIALIZED_EDIT, newArt);
         } else {
            preRenderer = new WordTemplateRenderer();
            preRenderer = openArtifacts(artifacts);
         }
         renderedFile = makeChangesToArtifact(preRenderer, artifacts);
         preStream = renderedFile.getContents();
         // simulate the saving of the changes:
         // osee creates an event that creates  a listener that takes care of the save
         // open the saved artifact
         postRenderer = openArtifacts(artifacts);
         renderedFile = postRenderer.getRenderedFile(artifacts, PresentationType.SPECIALIZED_EDIT);
         // verify the contents have changed
         istream = renderedFile.getContents();
         postStreamStr = convertStreamToString(istream);
         istream.close();
         assertTrue(!preStream.equals(postStreamStr));
         inputStream.close();
         TestUtil.severeLoggingEnd(monitorLog);
      } catch (Exception ex) {
         System.out.println(ex.toString());
      } finally {
         FrameworkTestUtil.killAllOpenWinword();
      }
   }

}
