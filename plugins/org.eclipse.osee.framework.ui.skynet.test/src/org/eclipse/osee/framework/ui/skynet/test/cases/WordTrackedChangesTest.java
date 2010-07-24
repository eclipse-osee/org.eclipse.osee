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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
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

   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1),
         WordTrackedChangesTest.class.getSimpleName());
      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testFindTrackChanges() throws Exception {
      assertTrue(WordAnnotationHandler.containsWordAnnotations(getFileContent(TEST_WORD_EDIT_FILE_NAME)));
   }

   @org.junit.Test
   public void testRemoveTrackChanges() throws Exception {
      String content = getFileContent(TEST_WORD_EDIT_FILE_NAME);
      content = WordAnnotationHandler.removeAnnotations(content);
      assertFalse(WordAnnotationHandler.containsWordAnnotations(content));
   }

   @org.junit.Test
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      String content = getFileContent(TEST_WORD_EDIT_FILE_NAME);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      FileSystemRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
      Artifact newArt =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedureWML, branch, getClass().getSimpleName());
      newArt.persist();
      String unlinkedContent = WordMlLinkHandler.unlink(linkType, newArt, content);

      assertTrue(WordAnnotationHandler.containsWordAnnotations(unlinkedContent));
   }

   private String getFileContent(String fileName) throws IOException {
      return Lib.inputStreamToString(new FileInputStream(fileName));
   }

   @After
   public void tearDown() throws Exception {
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid()),
         WordTrackedChangesTest.class.getSimpleName());
      FrameworkTestUtil.cleanupSimpleTest(BranchManager.getCommonBranch(), WordTrackedChangesTest.class.getSimpleName());
   }
}
