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

import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.tab.members.IMemberProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;

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
   public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
      Artifact collectorArt = memberProvider.getArtifact();
      List<Artifact> related = collectorArt.getRelatedArtifacts(memberProvider.getMemberRelationTypeSide());
      if (!related.contains(teamWf.getStoreObject())) {
         changes.relate(collectorArt, memberProvider.getMemberRelationTypeSide(), teamWf.getStoreObject());
      }
      if (dropTarget != null) {
         collectorArt.setRelationOrder(memberProvider.getMemberRelationTypeSide(), dropTarget, false,
            AtsApiService.get().getQueryServiceIde().getArtifact(teamWf));
         if (collectorArt.isOfType(AtsArtifactTypes.Goal)) {
            AtsApiService.get().getGoalMembersCache().decache((GoalArtifact) collectorArt);
         } else if (memberProvider.isSprint()) {
            AtsApiService.get().getSprintItemsCache().decache((SprintArtifact) collectorArt);
         }
      }

      changes.add(collectorArt);
   }

}
