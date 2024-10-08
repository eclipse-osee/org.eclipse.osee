/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.column.FoundInVersionColumnUI;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.util.widgets.XFoundInVersionWithPersistWidget;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

/**
 * @tests FoundInVersionColumn
 * @author Jeremy A. Midvidy
 */
public class FoundInVersionColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      IAtsVersion demoVersion = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);

      // FIV not set
      Assert.assertEquals("",
         FoundInVersionColumnUI.getInstance().getColumnText(codeArt, FoundInVersionColumnUI.getInstance(), 0));

      // FIV set
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Update Found-In-Version Test");
      changes.setRelation(codeArt, XFoundInVersionWithPersistWidget.FOUND_VERSION_RELATION, demoVersion);
      changes.executeIfNeeded();
      Assert.assertEquals(demoVersion.toString(),
         FoundInVersionColumnUI.getInstance().getColumnText(codeArt, FoundInVersionColumnUI.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

}
