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
package org.eclipse.osee.ats.client.integration.tests.ats.util;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.junit.Assert.assertFalse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.Import.ImportActionsViaSpreadsheetBlam;
import org.eclipse.osee.ats.util.Import.ImportActionsViaSpreadsheetBlam.ImportOption;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This test is intended to be run against a demo database.
 * 
 * @author Donald G. Dunne
 */
public class ImportActionsViaSpreadsheetTest {

   private static final String FIRST_ACTION_TITLE = "Fix the SAW Editor";
   private static final String SECOND_ACTION_TITLE = "Add the new feature";
   private static final String THIRD_ACTION_TITLE = "Help the users";
   private static List<String> ActionTitles =
      Arrays.asList(FIRST_ACTION_TITLE, SECOND_ACTION_TITLE, THIRD_ACTION_TITLE);

   @BeforeClass
   @AfterClass
   public static void cleanUp() throws Exception {
      AtsTestUtil.cleanupSimpleTest(ActionTitles);
   }

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse(AtsUtil.isProductionDb());

      for (String title : ActionTitles) {
         List<Artifact> arts = ArtifactQuery.getArtifactListFromName(title, AtsUtil.getAtsBranch(), EXCLUDE_DELETED);
         Assert.assertEquals(String.format("Action [%s] should have been purged before test start.", title), 0,
            arts.size());
      }
   }

   @org.junit.Test
   public void testImport() throws Exception {
      ImportActionsViaSpreadsheetBlam blam = new ImportActionsViaSpreadsheetBlam();

      File file = blam.getSampleSpreadsheetFile();
      Assert.assertNotNull(file);

      XResultData rd = blam.importActions(file, ImportOption.NONE);
      Assert.assertEquals("No errors should be reported", "", rd.toString());

      List<Artifact> arts =
         ArtifactQuery.getArtifactListFromName(FIRST_ACTION_TITLE, AtsUtil.getAtsBranch(), EXCLUDE_DELETED);
      Assert.assertEquals("One Action and 3 Team Workflows should be created", 4, arts.size());
      int codeCount = 0, testCount = 0;
      TeamWorkFlowArtifact testWf = null;
      for (Artifact art : arts) {
         if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) art;
            if (teamArt.getTeamDefinition().getName().contains("Code")) {
               codeCount++;
            } else if (teamArt.getTeamDefinition().getName().contains("Test")) {
               testCount++;
               testWf = teamArt;
            }
         }
      }
      Assert.assertEquals(2, codeCount);
      Assert.assertEquals(1, testCount);

      Assert.assertEquals("What needs to be done by Test team", testWf.getDescription());
      Assert.assertEquals("5", testWf.getSoleAttributeValue(AtsAttributeTypes.PriorityType, ""));
      Assert.assertTrue(testWf.getSoleAttributeValue(AtsAttributeTypes.EstimatedHours, 0.0) == 4.0);
      Assert.assertEquals("Improvement", testWf.getSoleAttributeValue(AtsAttributeTypes.ChangeType, null));
      Assert.assertEquals("SAW_Bld_3", AtsVersionService.get().getTargetedVersion(testWf).getName());
   }
}
