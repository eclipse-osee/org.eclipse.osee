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
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.column.CategoryColumn;
import org.eclipse.osee.ats.demo.api.DemoWorkType;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests CategoryColumn
 * @author Donald G. Dunne
 */
public class CategoryColumnTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         CategoryColumnTest.class.getSimpleName());
      codeArt.deleteAttributes(AtsAttributeTypes.Category1);
      codeArt.persist(transaction);
      reqArt.deleteAttributes(AtsAttributeTypes.Category1);
      reqArt.persist(transaction);
      testArt.deleteAttributes(AtsAttributeTypes.Category1);
      testArt.persist(transaction);
      transaction.execute();
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      Artifact actionArt = codeArt.getParentActionArtifact();

      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(codeArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(reqArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(testArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(actionArt, CategoryColumn.getCategory1Instance(), 0));

      SkynetTransaction transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         CategoryColumnTest.class.getSimpleName());
      codeArt.addAttribute(AtsAttributeTypes.Category1, "this");
      codeArt.persist(transaction);
      reqArt.addAttribute(AtsAttributeTypes.Category1, "that");
      reqArt.persist(transaction);
      testArt.addAttribute(AtsAttributeTypes.Category1, "the other");
      testArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("this",
         CategoryColumn.getCategory1Instance().getColumnText(codeArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("that",
         CategoryColumn.getCategory1Instance().getColumnText(reqArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("the other",
         CategoryColumn.getCategory1Instance().getColumnText(testArt, CategoryColumn.getCategory1Instance(), 0));

      String actionArtStr =
         CategoryColumn.getCategory1Instance().getColumnText(actionArt, CategoryColumn.getCategory1Instance(), 0);
      Assert.assertEquals(3, actionArtStr.split("; ").length);
      Assert.assertTrue(actionArtStr.contains("this"));
      Assert.assertTrue(actionArtStr.contains("that"));
      Assert.assertTrue(actionArtStr.contains("the other"));

      transaction = TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(),
         CategoryColumnTest.class.getSimpleName());
      codeArt.deleteAttributes(AtsAttributeTypes.Category1);
      codeArt.persist(transaction);
      reqArt.deleteSoleAttribute(AtsAttributeTypes.Category1);
      reqArt.persist(transaction);
      testArt.deleteAttribute(AtsAttributeTypes.Category1, "the other");
      testArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(codeArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(reqArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(testArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(actionArt, CategoryColumn.getCategory1Instance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
