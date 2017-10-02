/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateActionUsingAllActionableItems extends XNavigateItemAction {

   public CreateActionUsingAllActionableItems(XNavigateItem parent) {
      super(parent, "Create Action Using All Actionable Items - Admin", PluginUiImage.ADMIN);
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
            AtsUtil.openATSAction((Artifact) action.getTeamWfs().iterator().next().getStoreObject(),
               AtsOpenOption.OpenOneOrPopupSelect);
         } else {
            AWorkbench.popup("Completed", "Completed");
            AtsUtil.openATSAction((ActionArtifact) action.getAction().getStoreObject(), AtsOpenOption.OpenAll);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static ActionResult createActionWithAllAis()  {
      Set<IAtsActionableItem> aias = new HashSet<>();
      for (IAtsActionableItem aia : ActionableItems.getActionableItems(Active.Active,
         AtsClientService.get().getQueryService())) {
         if (aia.isActionable() && aia.isAllowUserActionCreation()) {
            aias.add(aia);
         }
      }

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Create Action using all AIs");
      ActionResult action = AtsClientService.get().getActionFactory().createAction(null, "Big Action Test - Delete Me",
         "Description", ChangeType.Improvement, "1", false, null, aias, new Date(),
         AtsClientService.get().getUserService().getCurrentUser(), null, changes);
      changes.execute();
      return action;
   }
}
