/*******************************************************************************
 * Copyright (c) 2022 Boeing.
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
package org.eclipse.osee.ats.ide.actions.newaction;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.ide.editor.tab.members.IMemberProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;

/**
 * @author Donald G. Dunne
 */
public class CreateNewActionAtLocactionBlam extends CreateNewActionBlam {

   private final IMemberProvider memberProvider;
   private final List<IAtsActionableItem> aias;
   private final Artifact dropTarget;

   public CreateNewActionAtLocactionBlam(IMemberProvider memberProvider, List<IAtsActionableItem> aias, Artifact dropTarget) {
      super("Create New Action At This Location",
         BLAM_DESCRIPTION + " and add to [" + memberProvider.getArtifact().getName() + //
            "] " + (dropTarget == null ? "to the end" : "before item " + dropTarget.toStringWithId()));
      this.memberProvider = memberProvider;
      this.aias = aias;
      this.dropTarget = dropTarget;
   }

   @Override
   public void inputSectionCreated(XWidgetPage widgetPage) {
      XHyperlabelActionableItemSelection aiWidget =
         (XHyperlabelActionableItemSelection) widgetPage.getLayoutData("Actionable Item(s)").getXWidget();
      if (aias.isEmpty()) {
         aias.addAll(AtsApiService.get().getActionableItemService().getTopLevelActionableItems(Active.Active));
      }
      aiWidget.setAis(aias);
   }

   @Override
   public void actionCreated(NewActionResult results, TransactionId tx) {
      Artifact collectorArt = memberProvider.getArtifact();
      collectorArt.reloadAttributesAndRelations();

      if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
         AtsApiService.get().getGoalMembersCache().decache((GoalArtifact) collectorArt);

         IAtsWorkItem goalWorkItem = atsApi.getWorkItemService().getWorkItem(collectorArt);
         atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED,
            Collections.singleton(goalWorkItem), tx);
      }
   }

   @Override
   public void createActionData(NewActionData data) {
      data.andMemberData(memberProvider.getArtifact(), memberProvider.getMemberRelationTypeSide().getRelationType(),
         dropTarget);
   }

}
