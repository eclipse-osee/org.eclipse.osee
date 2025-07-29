/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.workflow.action;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDataMulti;
import org.eclipse.osee.ats.core.action.AtsActionService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class AtsActionServiceIde extends AtsActionService {

   public AtsActionServiceIde(AtsApi atsApi) {
      super(atsApi);
   }

   /**
    * Creates new action on the server and returns results
    */
   @Override
   public NewActionData createAction(NewActionData data) {
      NewActionData newData = atsApi.getServerEndpoints().getActionEndpoint().createAction(data);

      if (newData.getRd().isErrors()) {
         XResultDataUI.report(newData.getRd(), "Create Action Error");
         throw new OseeCoreException("Error Creating Action: " + newData.getRd());
      }

      // Create ActionResult with loaded Action and TeamWfs used by IDE Client
      if (newData.getActResult().getAction().isValid()) {
         IAtsAction action =
            atsApi.getWorkItemService().getAction(ArtifactToken.valueOf(newData.getActResult().getAction(), "unknown"));
         newData.getActResult().setAtsAction(action);
      }
      for (ArtifactId twId : newData.getActResult().getTeamWfs()) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(ArtifactToken.valueOf(twId, "unknown"));
         if (teamWf != null) {
            newData.getActResult().getAtsTeamWfs().add(teamWf);
         }
      }
      return newData;
   }

   /**
    * Creates new action on the server and returns results
    */
   @Override
   public NewActionDataMulti createActions(NewActionDataMulti datas) {
      NewActionDataMulti datas2 = atsApi.getServerEndpoints().getActionEndpoint().createActions(datas);

      if (datas2.getRd().isErrors()) {
         XResultDataUI.report(datas2.getRd(), "Create Action Error");
         throw new OseeCoreException("Error Creating Action %s", datas2.getRd().toString());
      }

      for (NewActionData nad : datas2.getNewActionDatas()) {
         // Create ActionResult with loaded Action and TeamWfs used by IDE Client
         if (nad.getActResult().getAction().isValid()) {
            IAtsAction action =
               atsApi.getWorkItemService().getAction(ArtifactToken.valueOf(nad.getActResult().getAction(), "unknown"));
            nad.getActResult().setAtsAction(action);
         }
         for (ArtifactId twId : nad.getActResult().getTeamWfs()) {
            IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(ArtifactToken.valueOf(twId, "unknown"));
            if (teamWf != null) {
               nad.getActResult().getAtsTeamWfs().add(teamWf);
            }
         }
      }
      return datas2;
   }

}
