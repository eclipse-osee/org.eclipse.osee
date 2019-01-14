/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.ide.column.ChangeTypeColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.ChangeTypeUtil;
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
      reqArt.setSoleAttributeValue(AtsAttributeTypes.ChangeType, ChangeType.Problem.name());
      reqArt.persist(CategoryColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void getChangeTypeStrAndImage() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(codeArt));
      Assert.assertNotNull(
         ChangeTypeColumnUI.getInstance().getColumnImage(codeArt, ChangeTypeColumnUI.getInstance(), 0));

      IAtsAction action = codeArt.getParentAction();
      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(action));

      // clear our req change type
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      ChangeTypeUtil.setChangeType(reqArt, ChangeType.None);
      reqArt.persist(CategoryColumnTest.class.getSimpleName());

      Assert.assertEquals(ChangeType.None, ChangeTypeUtil.getChangeType(reqArt));
      Assert.assertNull(ChangeTypeColumnUI.getInstance().getColumnImage(reqArt, ChangeTypeColumnUI.getInstance(), 0));

      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(action));
      Assert.assertEquals("Problem",
         ChangeTypeColumnUI.getInstance().getColumnText(action, ChangeTypeColumnUI.getInstance(), 0));

      // set change type to Improvement
      ChangeTypeUtil.setChangeType(reqArt, ChangeType.Improvement);
      reqArt.persist(CategoryColumnTest.class.getSimpleName());

      Assert.assertEquals(ChangeType.Improvement, ChangeTypeUtil.getChangeType(reqArt));
      Assert.assertNotNull(
         ChangeTypeColumnUI.getInstance().getColumnImage(reqArt, ChangeTypeColumnUI.getInstance(), 0));

      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(action));
      String columnText = ChangeTypeColumnUI.getInstance().getColumnText(action, ChangeTypeColumnUI.getInstance(), 0);
      Assert.assertTrue(columnText.equals("Problem; Improvement") || columnText.equals("Improvement; Problem"));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
