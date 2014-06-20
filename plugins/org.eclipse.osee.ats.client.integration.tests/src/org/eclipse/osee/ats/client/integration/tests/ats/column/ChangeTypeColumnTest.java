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
package org.eclipse.osee.ats.client.integration.tests.ats.column;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.client.demo.DemoWorkType;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
   public static void cleanup() throws OseeCoreException {
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
      Assert.assertNotNull(ChangeTypeColumn.getInstance().getColumnImage(codeArt, ChangeTypeColumn.getInstance(), 0));

      Artifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(actionArt));

      // clear our req change type
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      ChangeTypeUtil.setChangeType(reqArt, ChangeType.None);
      reqArt.persist(CategoryColumnTest.class.getSimpleName());

      Assert.assertEquals(ChangeType.None, ChangeTypeUtil.getChangeType(reqArt));
      Assert.assertNull(ChangeTypeColumn.getInstance().getColumnImage(reqArt, ChangeTypeColumn.getInstance(), 0));

      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(actionArt));
      Assert.assertEquals("Problem",
         ChangeTypeColumn.getInstance().getColumnText(actionArt, ChangeTypeColumn.getInstance(), 0));

      // set change type to Improvement
      ChangeTypeUtil.setChangeType(reqArt, ChangeType.Improvement);
      reqArt.persist(CategoryColumnTest.class.getSimpleName());

      Assert.assertEquals(ChangeType.Improvement, ChangeTypeUtil.getChangeType(reqArt));
      Assert.assertNotNull(ChangeTypeColumn.getInstance().getColumnImage(reqArt, ChangeTypeColumn.getInstance(), 0));

      Assert.assertEquals(ChangeType.Problem, ChangeTypeUtil.getChangeType(actionArt));
      Assert.assertEquals("Problem; Improvement",
         ChangeTypeColumn.getInstance().getColumnText(actionArt, ChangeTypeColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
