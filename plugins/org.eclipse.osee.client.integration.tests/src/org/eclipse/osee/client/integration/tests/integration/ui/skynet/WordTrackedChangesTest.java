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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Before;

/**
 * @author Megumi Telles
 */
public class WordTrackedChangesTest {
   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordTrackedChangesTest.xml";

   @Before
   public void setUp() throws Exception {
      assertFalse("Not to be run on production datbase.", TestUtil.isProductionDb());
      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testFindTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      assertTrue(WordUtil.containsWordAnnotations(content));
   }

   @org.junit.Test
   public void testRemoveTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      content = WordUtil.removeAnnotations(content);
      assertFalse(WordUtil.containsWordAnnotations(content));
   }

   @org.junit.Test
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      Artifact newArt = null;
      try {
         newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedureWML, DemoSawBuilds.SAW_Bld_1,
               getClass().getSimpleName());
         newArt.persist(getClass().getSimpleName());
         String unlinkedContent = WordMlLinkHandler.unlink(linkType, newArt, content);
         assertTrue(WordUtil.containsWordAnnotations(unlinkedContent));
      } finally {
         if (newArt != null) {
            newArt.purgeFromBranch();
         }
      }
   }

}