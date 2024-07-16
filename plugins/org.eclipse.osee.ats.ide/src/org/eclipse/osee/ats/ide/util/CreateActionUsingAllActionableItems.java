/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateActionUsingAllActionableItems extends XNavigateItemAction {

   public CreateActionUsingAllActionableItems(XNavItemCat... xNavItemCat) {
      super("Create Action Using All Actionable Items - Admin", PluginUiImage.ADMIN, xNavItemCat);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      try {
         ActionResult action = createActionWithAllAis();
         int numWfs = action.getTeamWfs().size();
         if (numWfs > 30) {
            AWorkbench.popup(numWfs + " Workflows were created.  Only opening one.");
            AtsEditors.openATSAction(action.getTeamWfs().iterator().next().getStoreObject(),
               AtsOpenOption.OpenOneOrPopupSelect);
         } else {
            AWorkbench.popup("Completed", "Completed");
            AtsEditors.openATSAction(action.getAction().getStoreObject(), AtsOpenOption.OpenAll);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static ActionResult createActionWithAllAis() {
      // Clear out config cache to ensure only get live configs
      AtsApiService.get().reloadServerAndClientCaches();

      Set<IAtsActionableItem> aias = new HashSet<>();
      for (IAtsActionableItem aia : AtsApiService.get().getConfigService().getConfigurations().getIdToAi().values()) {
         if (aia.isActionable() && aia.isAllowUserActionCreation()) {
            aias.add(aia);
         }
      }

      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create Action using all AIs");
      ActionResult action = AtsApiService.get().getActionService().createAction(null, "Big Action Test - Delete Me",
         "Description", ChangeTypes.Improvement, "1", false, null, aias, new Date(),
         AtsApiService.get().getUserService().getCurrentUser(), null, changes);
      changes.execute();
      return action;
   }
}
