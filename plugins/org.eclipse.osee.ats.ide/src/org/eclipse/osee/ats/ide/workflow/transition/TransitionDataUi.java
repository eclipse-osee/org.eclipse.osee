/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.ide.workflow.transition;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.CancelledReasonEnumDialog;
import org.eclipse.osee.ats.ide.util.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class TransitionDataUi {

   public static void getCancellationReason(TransitionData transData) {

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            IAtsWorkItem workItem = transData.getWorkItems().iterator().next();
            StateDefinition stateDef = null;
            try {
               stateDef = AtsApiService.get().getWorkDefinitionService().getStateDefinitionByName(workItem,
                  transData.getToStateName());
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            if (stateDef != null && stateDef.isCancelled()) {
               TransitionResults results = new TransitionResults();
               AtsApiService.get().getWorkItemService().validateUserGroupTransition(workItem, stateDef, results);
               if (!results.isEmpty()) {
                  results.getTimeRd().addTimeMapToResultData();
               } else {
                  EntryDialog cancelDialog;
                  boolean useEntryCancelWidgetDialog = false;
                  for (LayoutItem layoutItem : stateDef.getLayoutItems()) {
                     if (layoutItem.getName().contains("Cancel")) {
                        useEntryCancelWidgetDialog = true;
                        break;
                     }
                  }
                  if (useEntryCancelWidgetDialog) {
                     cancelDialog = new CancelledReasonEnumDialog("Cancellation Reason",
                        "Select cancellation reason.  If other, please specify with details in the text entry.");
                  } else {
                     cancelDialog = new EntryDialog("Cancellation Reason", "Enter cancellation reason.");
                  }
                  if (cancelDialog.open() != 0) {
                     transData.setDialogCancelled(true);
                  }
                  if (useEntryCancelWidgetDialog) {
                     transData.setCancellationReason(((CancelledReasonEnumDialog) cancelDialog).getEntry());
                     transData.setCancellationReasonAttrType(AtsAttributeTypes.CancelledReasonEnum);
                     transData.setCancellationReasonDetails(
                        ((CancelledReasonEnumDialog) cancelDialog).getCancelledDetails());
                  } else {
                     transData.setCancellationReason(cancelDialog.getEntry());
                     transData.setCancellationReasonAttrType(AtsAttributeTypes.CancelledReason);
                  }
               }
            }
         }
      }, true);
   }

   public static void getTransitionBy(TransitionData transData) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            AtsApi atsApi = AtsApiService.get();
            IAtsWorkItem workItem = transData.getWorkItems().iterator().next();
            AtsUser currUser = atsApi.getUserService().getCurrentUser();
            String toStateName = transData.getToStateName();
            StateDefinition toState = workItem.getWorkDefinition().getStateByName(toStateName);
            Set<AtsUser> users = new HashSet<>();
            String title = "";
            // If completed/cancelled, choose from implementers or current user
            if (workItem.isCompletedOrCancelled() && toState.isWorking()) {
               users.addAll(workItem.getImplementers());
               users.add(currUser);
               title = "Select new Assignee";
            }
            // Else if not assignee, choose from assignees or current user
            else if (!workItem.getAssignees().contains(currUser)) {
               users.addAll(workItem.getAssignees());
               users.remove(AtsCoreUsers.UNASSIGNED_USER);
               // Prompt to select if UnAssigned
               if (users.isEmpty()) {
                  users.addAll(atsApi.getUserService().getUsers(Active.Active));
               }
               // Else prompt for any assignee or current user
               else {
                  users.add(currUser);
               }
               title = "Not Assignee; Select Transition-By User";
            }
            if (!users.isEmpty()) {
               UserListDialog diag = new UserListDialog(Displays.getActiveShell(), title, users);
               if (diag.open() == Window.OK) {
                  AtsUser user = diag.getSelectedFirst();
                  transData.setTransitionUser(user);
               }
            }
         }
      }, true);
   }

}
