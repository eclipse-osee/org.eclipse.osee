/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.xx.fund;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class XXAbstractFundingGoalWidget extends XXAbstractBaseXWidget<ArtifactToken> {

   private final AtsApi atsApi;
   private final ArtifactTypeToken artType;
   private final RelationTypeSide relType;
   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractFundingGoalWidget(WidgetId widgetId, String label, ArtifactTypeToken artType, RelationTypeSide relType) {
      super(widgetId, label);
      this.artType = artType;
      this.relType = relType;
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
      atsApi = AtsApiService.get();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      return new ArrayList<>(atsApi.getQueryService().getArtifacts(artType, AtsAttributeTypes.CurrentStateType,
         StateType.Working.name(), CoreBranches.COMMON));
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      ArtifactToken sel = getSelectedFirst();
      IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
      IAtsChangeSet changes = atsApi.createChangeSet("Set Funding Goal");

      // Add to Goal relation if not already related
      Collection<ArtifactToken> relatedGoals =
         atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.Goal_Goal, artType);
      if (sel != null && sel.isValid() && !relatedGoals.contains(sel)) {
         relatedGoals.add(sel);
         changes.setRelations(teamWf, AtsRelationTypes.Goal_Goal, relatedGoals);
      }

      // Unrelate other funding goals from the specific relation, relate the selected one
      boolean found = false;
      for (ArtifactToken existing : atsApi.getRelationResolver().getRelated(teamWf, relType, artType)) {
         if (sel != null && existing.equals(sel)) {
            found = true;
         } else {
            changes.unrelate(teamWf, relType, existing);
         }
      }
      if (sel != null && sel.isValid() && !found) {
         changes.setRelation(teamWf, relType, sel);
      }

      return changes.executeIfNeeded();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof GoalArtifact;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxWid.isTeamWf()) {
         Collection<ArtifactToken> goals = atsApi.getRelationResolver().getRelated(xxWid.getTeamWf(), relType, artType);
         if (goals.isEmpty()) {
            clear();
         } else {
            setSelected(goals.iterator().next(), false);
         }
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
