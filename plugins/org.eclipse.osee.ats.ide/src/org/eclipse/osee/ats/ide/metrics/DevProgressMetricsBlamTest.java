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

import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXTeamDefinitionsWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXVersionSelWidget;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class DevProgressMetricsBlamTest extends AbstractBlamTest {

   public DevProgressMetricsBlamTest() {
      super(new DevProgressMetricsBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      rd.log("Executing runDemoTest");
      IAtsTeamDefinition codeTeamDef =
         AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(DemoArtifactToken.SAW_Code);
      setSelected(XXTeamDefinitionsWidget.TEAM_DEFINITION_S, codeTeamDef);
      IAtsVersion saw_bld_2 = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
      setSelected(XXVersionSelWidget.VERSION, saw_bld_2.getArtifactToken());
      // Don't need to set START_TIME, it's defaults to first of this month
      setSelected(XWidgetLabels.END_DATE, new Date());
      setSelected(XWidgetLabels.ALL_TIME, true);

      rd.log("Executing BLAM");
      // Just execute and make sure no widget errors
      editor.executeBlam(rd);

      rd.log("Verifying VariableMap");
      // Verify VariableMap
      // NOTE: Don't need to do this for every Blam since all widgets are same, if one works, they all should
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertEquals(varMap.getValue(XXTeamDefinitionsWidget.TEAM_DEFINITION_S),
         Arrays.asList(DemoArtifactToken.SAW_Code));
      rd.assertEquals(varMap.getValue(XXVersionSelWidget.VERSION), DemoArtifactToken.SAW_Bld_2);
      rd.assertEquals(varMap.getBoolean(XWidgetLabels.ALL_TIME), true);
      rd.assertNotNull(varMap.getValue(XWidgetLabels.END_DATE));

      rd.warning("Future - Needs Operation Test");
      // Create operation test next time BLAM is updated / used

   }

}
