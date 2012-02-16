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
package org.eclipse.osee.ats.column;

import java.util.Collection;
import org.eclipse.osee.ats.core.client.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests ActionableItemsColumn
 * @author Donald G. Dunne
 */
public class ActionableItemsColumnTest {

   @org.junit.Test
   public void testGetActionableItems() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Collection<ActionableItemArtifact> aias = ActionableItemsColumn.getActionableItems(codeArt);
      Assert.assertEquals(1, aias.size());
      Assert.assertEquals("SAW Code", aias.iterator().next().getName());

      Artifact actionArt = codeArt.getParentActionArtifact();
      aias = ActionableItemsColumn.getActionableItems(actionArt);
      Assert.assertEquals(4, aias.size());

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   @org.junit.Test
   public void testGetActionableItemsStr() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("SAW Code", ActionableItemsColumn.getActionableItemsStr(codeArt));

      Artifact actionArt = codeArt.getParentActionArtifact();

      String results = ActionableItemsColumn.getActionableItemsStr(actionArt);
      Assert.assertTrue(results.contains("SAW Code"));
      Assert.assertTrue(results.contains("SAW SW Design"));
      Assert.assertTrue(results.contains("SAW Test"));
      Assert.assertTrue(results.contains("SAW Requirements"));
      Assert.assertEquals(4, results.split(", ").length);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
