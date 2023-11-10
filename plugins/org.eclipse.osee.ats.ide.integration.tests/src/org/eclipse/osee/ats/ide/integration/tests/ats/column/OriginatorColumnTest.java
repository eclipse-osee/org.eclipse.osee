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

import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.ide.column.OriginatorColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests OriginatorColumn
 * @author Donald G. Dunne
 */
public class OriginatorColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      Assert.assertEquals(UserManager.getUser(DemoUsers.Joe_Smith).getName(),
         OriginatorColumnUI.getInstance().getColumnText(reqArt, OriginatorColumnUI.getInstance(), 0));

      Artifact actionArt = (Artifact) reqArt.getParentAction().getStoreObject();
      Assert.assertEquals(UserManager.getUser(DemoUsers.Joe_Smith).getName(),
         OriginatorColumnUI.getInstance().getColumnText(actionArt, OriginatorColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
