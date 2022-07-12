/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests ChangeTypeColumn
 * @author Donald G. Dunne
 */
public class ChangeTypeColumnTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      reqArt.setSoleAttributeValue(AtsAttributeTypes.ChangeType, ChangeTypes.Problem.name());
      reqArt.persist(CategoryColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void getChangeTypeStrAndImage() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals(ChangeTypes.Problem, ChangeTypeColumn.getChangeType(codeArt, AtsApiService.get()));
      Assert.assertNotNull(
         ChangeTypeColumnUI.getInstance().getColumnImage(codeArt, ChangeTypeColumnUI.getInstance(), 0));

      IAtsAction action = codeArt.getParentAction();
      Assert.assertEquals(ChangeTypes.Problem, ChangeTypeColumn.getChangeType(action, AtsApiService.get()));

      // clear our req change type
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - Set Change Type");
      ChangeTypeColumn.setChangeType(reqArt, ChangeTypes.None, changes);
      changes.execute();

      Assert.assertEquals(ChangeTypes.None, ChangeTypeColumn.getChangeType(reqArt, AtsApiService.get()));
      Assert.assertNull(ChangeTypeColumnUI.getInstance().getColumnImage(reqArt, ChangeTypeColumnUI.getInstance(), 0));

      Assert.assertEquals(ChangeTypes.Problem, ChangeTypeColumn.getChangeType(action, AtsApiService.get()));
      Assert.assertEquals("Problem",
         ChangeTypeColumnUI.getInstance().getColumnText(action, ChangeTypeColumnUI.getInstance(), 0));

      // set change type to Improvement
      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - Set Change Type 2");
      ChangeTypeColumn.setChangeType(reqArt, ChangeTypes.Improvement, changes);
      changes.execute();

      Assert.assertEquals(ChangeTypes.Improvement, ChangeTypeColumn.getChangeType(reqArt, AtsApiService.get()));
      Assert.assertNotNull(
         ChangeTypeColumnUI.getInstance().getColumnImage(reqArt, ChangeTypeColumnUI.getInstance(), 0));

      Assert.assertEquals(ChangeTypes.Problem, ChangeTypeColumn.getChangeType(action, AtsApiService.get()));
      String columnText = ChangeTypeColumnUI.getInstance().getColumnText(action, ChangeTypeColumnUI.getInstance(), 0);
      Assert.assertTrue(columnText, columnText.equals("Problem"));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
