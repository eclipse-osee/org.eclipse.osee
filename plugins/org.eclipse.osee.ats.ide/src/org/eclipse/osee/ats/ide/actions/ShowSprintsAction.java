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
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public class ShowSprintsAction extends AbstractAtsAction implements IMenuCreator {

   private final IAgileBacklog backlog;
   private final AtsApi atsApi;
   private Menu menu;

   public ShowSprintsAction(IAgileBacklog backlog) {
      super();
      this.backlog = backlog;
      setText("Show Open Sprints");
      setImageDescriptor(
         ImageManager.getImageDescriptor(AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_SPRINT)));
      atsApi = AtsApiService.get();
      setMenuCreator(this);
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

   @Override
   public Menu getMenu(Control parent) {
      if (menu != null) {
         menu.dispose();
      }
      menu = new Menu(parent);
      populateMenu();
      return menu;
   }

   @Override
   public Menu getMenu(Menu parent) {
      return null;
   }

   private void populateMenu() {
      try {
         IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeamFromBacklog(backlog);
         Collection<IAgileSprint> sprints = atsApi.getAgileService().getAgileSprints(agileTeam);
         List<IAgileSprint> openSprints = new ArrayList<>();
         for (IAgileSprint sprint : sprints) {
            if (sprint.isInWork()) {
               openSprints.add(sprint);
            }
         }
         for (IAgileSprint sprint : openSprints) {
            addActionToMenu(menu, new OpenSprintAction(sprint));
         }
         if (openSprints.isEmpty()) {
            Action noSprintsAction = new Action("No open sprints") {
               // empty
            };
            noSprintsAction.setEnabled(false);
            addActionToMenu(menu, noSprintsAction);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void addActionToMenu(Menu parent, Action action) {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(parent, -1);
   }

   @Override
   public void dispose() {
      if (menu != null) {
         menu.dispose();
         menu = null;
      }
   }

   private class OpenSprintAction extends Action {
      private final IAgileSprint sprint;

      public OpenSprintAction(IAgileSprint sprint) {
         super(sprint.getName());
         this.sprint = sprint;
         setImageDescriptor(ImageManager.getImageDescriptor(
            AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_SPRINT)));
      }

      @Override
      public void run() {
         List<IAtsWorkItem> workItems = new ArrayList<>();
         workItems.add(sprint);
         WorldEditor.open(sprint.getName(), workItems);
      }
   }

}
