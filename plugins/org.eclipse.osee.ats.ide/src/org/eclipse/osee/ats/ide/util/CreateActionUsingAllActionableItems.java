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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.NewActionData;
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
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
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
         NewActionData newActionData = createActionWithAllAis();
         int numWfs = newActionData.getActResult().getTeamWfs().size();
         if (numWfs > 30) {
            AWorkbench.popup(numWfs + " Workflows were created.  Only opening one.");
            AtsEditors.openATSAction(newActionData.getActResult().getAtsTeamWfs().iterator().next().getStoreObject(),
               AtsOpenOption.OpenOneOrPopupSelect);
         } else {
            AWorkbench.popup("Completed", "Completed");
            AtsEditors.openATSAction(newActionData.getActResult().getAtsAction().getStoreObject(),
               AtsOpenOption.OpenAll);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static NewActionData createActionWithAllAis() {
      AtsApi atsApi = AtsApiService.get();
      // Clear out config cache to ensure only get live configs
      atsApi.reloadServerAndClientCaches();

      Set<IAtsActionableItem> aias = new HashSet<>();
      for (IAtsActionableItem aia : AtsApiService.get().getConfigService().getConfigurations().getIdToAi().values()) {
         if (aia.isActionable() && aia.isAllowUserActionCreation()) {
            aias.add(aia);
         }
      }

      String opName = CreateActionUsingAllActionableItems.class.getSimpleName();
      NewActionData data = atsApi.getActionService() //
         .createActionData(opName, "Big Action Test - Delete Me", "Description") //
         .andAis(aias) //
         .andChangeType(ChangeTypes.Improvement) //
         .andPriority("1");
      NewActionData newActionData = atsApi.getActionService().createAction(data);
      if (newActionData.getRd().isErrors()) {
         XResultDataUI.report(newActionData.getRd(), opName);
      }

      return newActionData;
   }
}
