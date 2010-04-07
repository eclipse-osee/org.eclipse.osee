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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WHOLE_WORD_CONTENT;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WORD_TEMPLATE_CONTENT;
import static org.eclipse.osee.framework.skynet.core.utility.Requirements.SOFTWARE_REQUIREMENT;
import static org.junit.Assert.assertFalse;
import java.io.FileInputStream;
import java.io.IOException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.OseeTrackedChangesException;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.eclipse.osee.framework.ui.skynet.render.FileRenderer;
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

   @org.junit.Test(expected = OseeTrackedChangesException.class)
   public void testWordSaveWithTrackChanges() throws Exception {
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
      Artifact newArt = ArtifactTypeManager.addArtifact(SOFTWARE_REQUIREMENT, branch, getClass().getSimpleName());
      try {
         newArt.setSoleAttributeFromString(WORD_TEMPLATE_CONTENT, getFileContent(TEST_WORD_EDIT_FILE_NAME));
      } finally {
         newArt.persist();
      }
   }

   @org.junit.Test(expected = OseeTrackedChangesException.class)
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      String content = getFileContent(TEST_WORD_EDIT_FILE_NAME);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      FileRenderer.setWorkbenchSavePopUpDisabled(true);
      Branch branch = BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1);
      Artifact newArt = ArtifactTypeManager.addArtifact("Test Procedure WML", branch, getClass().getSimpleName());
      String unlinkedContent = WordMlLinkHandler.unlink(linkType, newArt, content);
      try {
         newArt.setSoleAttributeFromString(WHOLE_WORD_CONTENT, unlinkedContent);
      } finally {
         newArt.persist();
      }
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
