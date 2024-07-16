/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.demo.populate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class Pdd83CreateCantSeeTheGraphViewAction implements IPopulateDemoDatabase {

   @Override
   public void run() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.Adapter_AI);

      ActionResult actionResult = AtsApiService.get().getActionService().createAction(null,
         DemoArtifactToken.CantSeeTheGraphView_TeamWf.getName(), "Problem with the Graph View", ChangeTypes.Problem,
         "1", false, null, aias, new Date(), AtsApiService.get().getUserService().getCurrentUser(),
         Arrays.asList(new ArtifactTokenActionListener()), changes);

      transitionTo(actionResult.getFirstTeam(), TeamState.Implement, changes);

      changes.execute();
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return DemoArtifactToken.CantSeeTheGraphView_TeamWf;
      }
   }

}
