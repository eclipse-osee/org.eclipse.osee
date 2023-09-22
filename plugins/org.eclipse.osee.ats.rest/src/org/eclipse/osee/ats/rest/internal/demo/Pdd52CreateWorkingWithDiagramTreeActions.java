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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class Pdd52CreateWorkingWithDiagramTreeActions extends AbstractPopulateDemoDatabase {

   public Pdd52CreateWorkingWithDiagramTreeActions(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   static Map<ArtifactToken, ArtifactToken> versionToWorkflowToken;
   private ArtifactToken currentVersion = null;

   static {
      versionToWorkflowToken = new HashMap<>(3);
      versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_1,
         DemoArtifactToken.WorkingWithDiagramTreeForBld1_TeamWf);
      versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_2,
         DemoArtifactToken.WorkingWithDiagramTreeForBld2_TeamWf);
      versionToWorkflowToken.put(DemoArtifactToken.SAW_Bld_3,
         DemoArtifactToken.WorkingWithDiagramTreeForBld3_TeamWf);
   }

   public synchronized Map<ArtifactToken, ArtifactToken> getVersionToWorkflowToken() {
      return versionToWorkflowToken;
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.SAW_SW_Design_AI);
      Date createdDate = new Date();

      for (ArtifactToken version : getVersionToWorkflowToken().keySet()) {
         currentVersion = version;

         IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
         ArtifactToken teamWfArtToken = getVersionToWorkflowToken().get(version);
         ActionResult actionResult = atsApi.getActionService().createAction(null, teamWfArtToken.getName(),
            "Problem with the Diagram Tree", ChangeTypes.Problem, "3", false, null, aias, createdDate,
            atsApi.getUserService().getCurrentUser(), Arrays.asList(new ArtifactTokenActionListener()), changes);
         changes.execute();

         TeamState state = getState(version);
         IAtsTeamWorkflow teamWf = actionResult.getFirstTeam();

         transitionToWithPersist(actionResult.getFirstTeam(), state, teamWf.getAssignees().iterator().next(),
            teamWf.getAssignees(), atsApi);

         setVersionAndReload(actionResult.getFirstTeam(), version);
      }

   }

   private TeamState getState(ArtifactToken version) {
      return version.equals(DemoArtifactToken.SAW_Bld_1) ? TeamState.Completed : TeamState.Implement;
   }

   private class ArtifactTokenActionListener implements INewActionListener {
      @Override
      public ArtifactToken getArtifactToken(List<IAtsActionableItem> applicableAis) {
         return getVersionToWorkflowToken().get(currentVersion);
      }
   }

}
