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
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
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

   /**
    * This test Word Edit's are being saved.
    */
   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1), getClass().getSimpleName());
      RenderingUtil.setPopupsAllowed(false);
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
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1), getClass().getSimpleName());
   }

   private static IFile makeChangesToArtifact(FileSystemRenderer renderer, List<Artifact> artifacts) {
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

   private static void probeWordEditingCapability(boolean useRendererManager, String className) {
      try {
         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
         FileSystemRenderer.setWorkbenchSavePopUpDisabled(true);
         Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
         Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.SoftwareRequirement, branch, className);
         newArt.persist();
         // open the artifacts; testing one artifact for now
         List<Artifact> artifacts = Arrays.asList(newArt);

         FileSystemRenderer preRenderer = null;
         if (useRendererManager) {
            preRenderer = RendererManager.getBestFileRenderer(PresentationType.SPECIALIZED_EDIT, newArt);
         } else {
            preRenderer = new WordTemplateRenderer();
         }
         IFile renderedFile = makeChangesToArtifact(preRenderer, artifacts);
         InputStream preStream = renderedFile.getContents();
         // simulate the saving of the changes:
         // osee creates an event that creates  a listener that takes care of the save
         // open the saved artifact
         WordTemplateRenderer postRenderer = new WordTemplateRenderer();
         renderedFile = postRenderer.getRenderedFile(artifacts, PresentationType.SPECIALIZED_EDIT);
         // verify the contents have changed
         InputStream istream = renderedFile.getContents();

         String postStreamStr = Lib.inputStreamToString(istream);
         istream.close();
         assertTrue(!preStream.equals(postStreamStr));
         inputStream.close();
         TestUtil.severeLoggingEnd(monitorLog);
      } catch (Exception ex) {
         System.out.println(ex.toString());
      }
   }
}
