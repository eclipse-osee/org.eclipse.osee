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

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.type.LinkType;
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
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

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
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      content = WordCoreUtil.removeAnnotations(content);
      assertFalse(WordCoreUtil.containsWordAnnotations(content));
   }

   @Test
   public void testWholeWordSaveWithTrackChanges() throws Exception {
      String content = Lib.fileToString(getClass(), TEST_WORD_EDIT_FILE_NAME);
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      Artifact newArt = null;
      try {
         newArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedureWholeWord, SAW_Bld_1, getClass().getSimpleName());
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