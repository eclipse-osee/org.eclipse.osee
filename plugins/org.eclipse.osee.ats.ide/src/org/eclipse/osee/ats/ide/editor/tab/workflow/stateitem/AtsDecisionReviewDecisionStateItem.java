/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.stateitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewOption;
import org.eclipse.osee.ats.api.review.DecisionReviewOptions;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.ide.editor.tab.workflow.section.WfeWorkflowSection;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewDecisionStateItem extends AtsStateItem {

   public AtsDecisionReviewDecisionStateItem() {
      super(AtsDecisionReviewDecisionStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Add decision options to Decision state based on prepare state's entries.";
   }

   @Override
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable) {
      if (art.isOfType(AtsArtifactTypes.DecisionReview) && stateDefinition.getName().equals(
         DecisionReviewState.Decision.getName())) {
         if (xWidget == null) {
            throw new OseeStateException("Can't retrieve decision review combo widget to set.");
         }
         if (xWidget.getLabel().equals(AtsAttributeTypes.Decision.getUnqualifiedName())) {
            XComboDam decisionComboDam = (XComboDam) xWidget;
            List<String> options = new ArrayList<>();
            DecisionReviewOptions xDecOptions =
               new DecisionReviewOptions((IAtsDecisionReview) art, AtsClientService.get());
            for (DecisionReviewOption opt : xDecOptions.getDecisionOptions()) {
               options.add(opt.getName());
            }
            decisionComboDam.setDataStrings(options.toArray(new String[options.size()]));
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String getOverrideTransitionToStateName(WfeWorkflowSection section) {
      if (isApplicable(section)) {
         if (section.getTransitionToStateCombo() == null || section.getTransitionToStateCombo().getSelected() == null) {
            return null;
         }
         XWidget xWidget =
            section.getPage().getLayoutData(AtsAttributeTypes.Decision.getUnqualifiedName()).getXWidget();
         XComboDam decisionComboDam = (XComboDam) xWidget;
         DecisionReviewArtifact decArt = (DecisionReviewArtifact) section.getSma();
         return getOverrideTransitionToStateName(decArt, decisionComboDam);
      }
      return null;
   }

   private boolean isApplicable(WfeWorkflowSection section) {
      return section.getSma().isTypeEqual(
         AtsArtifactTypes.DecisionReview) && section.getSma().getCurrentStateName().equals(
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
   public Collection<IAtsUser> getOverrideTransitionToAssignees(AbstractWorkflowArtifact awa, String decision) {
      if (isApplicable(awa)) {
         DecisionReviewArtifact decArt = (DecisionReviewArtifact) awa;
         return getOverrideTransitionToAssignees(decArt, decision);
      }
      return null;
   }

   public Collection<IAtsUser> getOverrideTransitionToAssignees(DecisionReviewArtifact decArt, String decision) {
      DecisionReviewOption decisionOption = getDecisionOption(decArt, decision);
      if (decisionOption == null) {
         return null;
      }
      List<IAtsUser> assignees = new LinkedList<>();
      assignees.addAll(AtsClientService.get().getUserService().getUsersByUserIds(decisionOption.getAssignees()));
      return assignees;
   }

   private boolean isApplicable(AbstractWorkflowArtifact awa) {
      return awa.isTypeEqual(AtsArtifactTypes.DecisionReview) && awa.getCurrentStateName().equals(
         DecisionReviewState.Decision.getName());
   }

   private DecisionReviewOption getDecisionOption(DecisionReviewArtifact decRevArt, String decision) {
      if (decision.equals("")) {
         return null;
      }
      return decRevArt.decisionOptions.getDecisionOption(decision);
   }

}
