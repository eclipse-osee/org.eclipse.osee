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
package org.eclipse.osee.ats.ide.demo.populate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.demo.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class Pdd51CreateWorkaroundForGraphViewActions implements IPopulateDemoDatabase {

   private static Map<ArtifactToken, ArtifactToken> versionToWorkflowToken;
   private ArtifactToken currentVersion = null;

   public Map<ArtifactToken, ArtifactToken> getVersionToWorkflowToken() {
      if (versionToWorkflowToken == null) {
         versionToWorkflowToken = new HashMap<>(3);
         versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_1,
            DemoArtifactToken.WorkaroundForGraphViewWorkflowForBld1_TeamWf);
         versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_2,
            DemoArtifactToken.WorkaroundForGraphViewWorkflowForBld2_TeamWf);
         versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_3,
            DemoArtifactToken.WorkaroundForGraphViewWorkflowForBld3_TeamWf);
      }
      return versionToWorkflowToken;
   }

   @Override
   public void run() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.Adapter_AI);
      Date createdDate = new Date();

      for (ArtifactToken version : getVersionToWorkflowToken().keySet()) {
         currentVersion = version;
         ArtifactToken teamWfArtToken = getVersionToWorkflowToken().get(version);
         ActionResult actionResult =
            AtsClientService.get().getActionFactory().createAction(null, teamWfArtToken.getName(),
               "Problem with the Graph View", ChangeType.Problem, "1", false, null, aias, createdDate,
               AtsClientService.get().getUserService().getCurrentUser(), new ArtifactTokenActionListener(), changes);

         transitionTo(actionResult.getFirstTeam(), getState(version), changes);

         setVersion(actionResult.getFirstTeam(), version, changes);
      }

      changes.execute();
   }

   private TeamState getState(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? TeamState.Completed : TeamState.Implement;
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return versionToWorkflowToken.get(currentVersion);
      }
   }

}
