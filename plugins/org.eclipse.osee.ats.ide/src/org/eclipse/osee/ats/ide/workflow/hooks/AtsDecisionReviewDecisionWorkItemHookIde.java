/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.workflow.hooks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewOption;
import org.eclipse.osee.ats.api.review.DecisionReviewOptions;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Contributed through OSGI-INF as both workflow and transition hooks
 *
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewDecisionWorkItemHookIde implements IAtsWorkItemHookIde, IAtsTransitionHook {

   public String getName() {
      return AtsDecisionReviewDecisionWorkItemHookIde.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "Add decision options to Decision state based on prepare state's entries.";
   }

   @Override
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, boolean isEditable) {
      if (art.isOfType(AtsArtifactTypes.DecisionReview) && stateDefinition.getName().equals(
         DecisionReviewState.Decision.getName())) {
         if (xWidget == null) {
            throw new OseeStateException("Can't retrieve decision review combo widget to set.");
         }
         if (xWidget.getLabel().equals(AtsAttributeTypes.Decision.getUnqualifiedName())) {
            XComboDam decisionComboDam = (XComboDam) xWidget;
            DecisionReviewOptions xDecOptions =
               new DecisionReviewOptions((IAtsDecisionReview) art, AtsApiService.get());
            decisionComboDam.setDataStrings(Named.getNamesArray(xDecOptions.getDecisionOptions()));
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String getOverrideTransitionToStateName(IAtsWorkItem workItem) {
      if (isApplicable(workItem)) {
         WorkflowEditor editor = WorkflowEditor.getWorkflowEditor(workItem);
         if (editor != null) {
            if (editor.getWorkFlowTab().getHeader().getWfeTransitionHeader() == null) {
               return null;
            }
            if (editor.getWorkFlowTab().getHeader().getWfeTransitionHeader().isSelected()) {
               return null;
            }
            if (editor.getWorkFlowTab().getCurrentStateSection() == null) {
               return null;
            }
            XWidget xWidget = editor.getWorkFlowTab().getCurrentStateSection().getPage().getLayoutData(
               AtsAttributeTypes.Decision.getUnqualifiedName()).getXWidget();
            XComboDam decisionComboDam = (XComboDam) xWidget;
            DecisionReviewArtifact decArt = (DecisionReviewArtifact) workItem;
            return getOverrideTransitionToStateName(decArt, decisionComboDam);
         }
      }
      return null;
   }

   private boolean isApplicable(IAtsWorkItem workItem) {
      return workItem.isTypeEqual(AtsArtifactTypes.DecisionReview) && workItem.getCurrentStateName().equals(
         DecisionReviewState.Decision.getName());
   }

   public String getOverrideTransitionToStateName(DecisionReviewArtifact decArt, XComboDam decisionComboDam) {
      DecisionReviewOption decisionOption = getDecisionOption(decArt, decisionComboDam.get());
      if (decisionOption == null) {
         return null;
      }
      boolean followUpRequired = decisionOption.isFollowupRequired();
      if (followUpRequired) {
         return DecisionReviewState.Followup.getName();
      } else {
         return DecisionReviewState.Completed.getName();
      }
   }

   @Override
   public Collection<AtsUser> getOverrideTransitionToAssignees(IAtsWorkItem workItem, String decision) {
      if (isApplicable(workItem)) {
         DecisionReviewArtifact decArt = (DecisionReviewArtifact) workItem;
         return getOverrideTransitionToAssignees(decArt, decision);
      }
      return null;
   }

   public Collection<AtsUser> getOverrideTransitionToAssignees(DecisionReviewArtifact decArt, String decision) {
      DecisionReviewOption decisionOption = getDecisionOption(decArt, decision);
      if (decisionOption == null) {
         return null;
      }
      List<AtsUser> assignees = new LinkedList<>();
      assignees.addAll(AtsApiService.get().getUserService().getUsersByUserIds(decisionOption.getAssignees()));
      return assignees;
   }

   private DecisionReviewOption getDecisionOption(DecisionReviewArtifact decRevArt, String decision) {
      if (decision.equals("")) {
         return null;
      }
      return decRevArt.decisionOptions.getDecisionOption(decision);
   }

}
