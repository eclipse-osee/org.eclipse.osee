/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowSprintsAction extends AbstractAtsAction {

   private final IAgileBacklog backlog;
   private final AtsApi atsApi;

   public ShowSprintsAction(IAgileBacklog backlog) {
      super();
      this.backlog = backlog;
      setText("Show Open Sprints");
      setImageDescriptor(
         ImageManager.getImageDescriptor(AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_SPRINT)));
      atsApi = AtsApiService.get();
   }

   @Override
   public void runWithException() {
      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeamFromBacklog(backlog);
      Collection<IAgileSprint> sprints = atsApi.getAgileService().getAgileSprints(agileTeam);
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (IAgileSprint sprint : sprints) {
         if (sprint.isInWork()) {
            workItems.add(sprint);
         }
      }
      WorldEditor.open("Open Sprints", workItems);
   }

}
