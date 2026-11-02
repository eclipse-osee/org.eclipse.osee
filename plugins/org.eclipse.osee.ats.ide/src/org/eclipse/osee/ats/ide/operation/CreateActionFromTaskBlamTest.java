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

package org.eclipse.osee.ats.ide.operation;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class CreateActionFromTaskBlamTest extends AbstractBlamTest {

   public CreateActionFromTaskBlamTest() {
      super(new CreateActionFromTaskBlam());
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      IAtsActionableItem reqAi =
         AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Requirements_AI);
      setSelected(XWidgetLabels.ACTIONABLE_ITEMS, reqAi);
      setSelected(XWidgetLabels.CHANGE_TYPE, "Improvement");
      setSelected(XWidgetLabels.PRIORITY, "3");

      editor.executeBlam(rd);

      // Verify VariableMap
      VariableMap varMap = editor.getOverviewPage().getVariableMap();
      rd.assertNotNull(varMap.getValue(XWidgetLabels.ACTIONABLE_ITEMS));
      rd.assertNotNull(varMap.getValue(XWidgetLabels.CHANGE_TYPE));
      rd.assertNotNull(varMap.getValue(XWidgetLabels.PRIORITY));

      rd.warning("Future - Needs Operation Test");
   }

}
