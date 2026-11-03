/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.metrics;

import java.util.Date;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXTargetedVersionWidget;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class SoftwareReqVolatilityMetricsBlamTest extends AbstractBlamTest {

   public SoftwareReqVolatilityMetricsBlamTest() {
      super(new SoftwareReqVolatilityMetricsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      XWidget.debugXWidgets.syserr("runDemoTest...");
      IAtsTeamDefinition codeTeamDef =
         AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(DemoArtifactToken.SAW_Code);
      setSelected(XXTeamDefinitionsWidget.TEAM_DEFINITION_S, codeTeamDef);
      IAtsVersion saw_bld_2 = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      setSelected(XXTargetedVersionWidget.TARGETED_VERSION, saw_bld_2.getStoreObject());
      setSelected(XWidgetLabels.START_DATE, new Date());
      setSelected(XWidgetLabels.END_DATE, new Date());
      setSelected(XWidgetLabels.ALL_TIME, false);
      setSelected(SoftwareReqVolatilityMetricsBlam.INCLUDE_IMPACTS, false);

      // Just execute and make sure no widget errors
      editor.executeBlam(rd);

      // Don't need to re-test VariableMap cause same are in DevProgressMetricsBlamTest

      rd.warning("Future - Needs Operation Test");
      // Create operation test next time BLAM is updated / used

   }

}
