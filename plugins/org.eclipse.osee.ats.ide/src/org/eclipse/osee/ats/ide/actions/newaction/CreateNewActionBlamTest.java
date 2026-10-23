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

package org.eclipse.osee.ats.ide.actions.newaction;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.NeedBy;
import java.util.Date;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;

/**
 * @author Donald G. Dunne
 */
public class CreateNewActionBlamTest extends AbstractBlamTest {

   public CreateNewActionBlamTest() {
      super(new CreateNewActionBlam());
   }

   public CreateNewActionBlamTest(AbstractBlam blam) {
      super(blam);
   }

   @Override
   protected void runDemoTest(BlamEditor editor, XResultData rd) throws Exception {
      String title = getClass().getSimpleName() + " - " + Lib.generateId();
      setSelected(XWidgetLabels.TITLE, title);
      IAtsActionableItem reqAi =
         AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Requirements_AI);
      if (reqAi == null) {
         rd.errorf("Required demo Actionable Item [%s] not found; ensure demo DB is populated\n",
            DemoArtifactToken.SAW_Requirements_AI);
         return;
      }
      setSelected(XWidgetLabels.ACTIONABLE_ITEMS, reqAi);
      setSelected(Description.getUnqualifiedName(), "my description");
      setSelected(XWidgetLabels.CHANGE_TYPE, "my change type");
      setSelected(XWidgetLabels.PRIORITY, "my priority");
      setSelected(NeedBy.getUnqualifiedName(), new Date());

      editor.executeBlam(rd);
      if (rd.isErrors()) {
         return;
      }

      ArtifactToken workflow =
         AtsApiService.get().getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.DemoReqTeamWorkflow, title);
      if (workflow.isInvalid()) {
         rd.errorf("Action was not created successfully - [%s]\n", rd);
      }
   }

}
