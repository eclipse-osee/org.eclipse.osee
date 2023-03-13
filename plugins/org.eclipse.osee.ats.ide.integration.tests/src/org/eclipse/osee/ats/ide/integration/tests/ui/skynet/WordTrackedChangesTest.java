/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public class WordTrackedChangesTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String TEST_WORD_EDIT_FILE_NAME = "support/WordTrackedChangesTest.xml";

   @Test
   public void testFindTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      assertTrue(WordCoreUtil.containsWordAnnotations(content));
   }

   @Test
   public void testRemoveTrackChanges() throws Exception {
      var content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      var cleanContent = WordCoreUtil.removeAnnotations(content);
      assertFalse(WordCoreUtil.containsWordAnnotations(cleanContent));
   }

   @Test
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      Artifact newArt = null;
      try {
         newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedureWholeWord, SAW_Bld_1,
            getClass().getSimpleName());
         newArt.persist(getClass().getSimpleName());
         String unlinkedContent = WordMlLinkHandler.unlink(linkType, newArt, content);
         assertTrue(WordCoreUtil.containsWordAnnotations(unlinkedContent));
      } finally {
         if (newArt != null) {
            newArt.purgeFromBranch();
         }
      }
   }

}