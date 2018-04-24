/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo.populate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.client.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class Pdd83CreateCantSeeTheGraphViewAction implements IPopulateDemoDatabase {

   @Override
   public void run() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.Adapter_AI);

      ActionResult actionResult = AtsClientService.get().getActionFactory().createAction(null,
         DemoArtifactToken.CantSeeTheGraphView_TeamWf.getName(), "Problem with the Graph View", ChangeType.Problem, "1",
         false, null, aias, new Date(), AtsClientService.get().getUserService().getCurrentUser(),
         new ArtifactTokenActionListener(), changes);

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
