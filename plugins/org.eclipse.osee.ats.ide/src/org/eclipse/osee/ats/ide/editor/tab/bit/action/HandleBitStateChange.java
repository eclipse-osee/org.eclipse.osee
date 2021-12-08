/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.bit.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.ats.ide.editor.tab.bit.XBitViewer;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ListSelectionDialogNoSave;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class HandleBitStateChange {

   protected final IAtsTeamWorkflow crTeamWf;
   protected final AtsApi atsApi;
   private final XBitViewer xBitViewer;

   public HandleBitStateChange(IAtsTeamWorkflow crTeamWf, XBitViewer xBitViewer, AtsApi atsApi) {
      this.crTeamWf = crTeamWf;
      this.xBitViewer = xBitViewer;
      this.atsApi = atsApi;
   }

   public void handleMultiEdit() {
      List<BuildImpactData> bids = xBitViewer.getSelectedBuildImpactDatas();
      if (bids.isEmpty()) {
         AWorkbench.popup("Select one or more Build Impact Items");
         return;
      } else {
         List<String> states = getStates();
         ListSelectionDialogNoSave dialog = getListSelectionDialog(states);
         if (dialog.open() == Window.OK) {
            String newState = (String) dialog.getSelected();
            IAtsChangeSet changes = atsApi.createChangeSet("Update BID State");
            for (BuildImpactData bid : bids) {
               Artifact art = (Artifact) atsApi.getQueryService().getArtifact(bid.getBidArt());
               changes.setSoleAttributeValue(art, AtsAttributeTypes.BitState, newState);
            }
            TransactionToken transaction = changes.executeIfNeeded();
            ((Artifact) crTeamWf).reloadAttributesAndRelations();
            atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED,
               Arrays.asList(crTeamWf), transaction);
         }
      }
   }

   public boolean handleChangeState(BuildImpactData bid) {
      List<String> states = getStates();
      states.remove(bid.getState());
      ListSelectionDialogNoSave dialog = getListSelectionDialog(states);

      if (dialog.open() == Window.OK) {
         Artifact bidArt = (Artifact) atsApi.getQueryService().getArtifact(bid.getBidArt());
         bidArt.setSoleAttributeValue(AtsAttributeTypes.BitState, dialog.getSelected());
         TransactionId transactionId = bidArt.persist("Update BID State");
         ((Artifact) crTeamWf).reloadAttributesAndRelations();
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, Arrays.asList(crTeamWf),
            transactionId);

         return true;
      }

      return false;
   }

   private ListSelectionDialogNoSave getListSelectionDialog(List<String> states) {
      ListSelectionDialogNoSave dialog =
         new ListSelectionDialogNoSave(Collections.castAll(states), Displays.getActiveShell().getShell(),
            "Select Transition-To State", null, "Select the state to transition to.\n\n" //
               + "Transition will happen upon selection and Transition button.\n\n" //
               + "Double-click will select, close and transition.",
            2, new String[] {"Transition", "Cancel"}, 0);
      return dialog;
   }

   private List<String> getStates() {
      List<String> states = new ArrayList<String>();
      for (String state : Arrays.asList(BuildImpactState.Open.name(), BuildImpactState.InWork.name(),
         BuildImpactState.Promoted.name(), BuildImpactState.Closed.name(), BuildImpactState.Deferred.name(),
         BuildImpactState.Cancelled.name())) {
         states.add(state);
      }
      return states;
   }

}
