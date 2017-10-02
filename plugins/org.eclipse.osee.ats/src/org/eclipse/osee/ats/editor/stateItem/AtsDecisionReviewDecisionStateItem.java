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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionOption;
import org.eclipse.osee.ats.api.review.DecisionOptions;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.WfeWorkflowSection;
import org.eclipse.osee.ats.internal.AtsClientService;
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
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, IAtsStateDefinition stateDefinition, Artifact art, boolean isEditable)  {
      if (art.isOfType(AtsArtifactTypes.DecisionReview) && stateDefinition.getName().equals(
         DecisionReviewState.Decision.getName())) {
         if (xWidget == null) {
            throw new OseeStateException("Can't retrieve decision review combo widget to set.");
         }
         if (xWidget.getLabel().equals(AtsAttributeTypes.Decision.getUnqualifiedName())) {
            XComboDam decisionComboDam = (XComboDam) xWidget;
            List<String> options = new ArrayList<>();
            DecisionOptions xDecOptions = new DecisionOptions((IAtsDecisionReview) art, AtsClientService.get());
            for (DecisionOption opt : xDecOptions.getDecisionOptions()) {
               options.add(opt.getName());
            }
            decisionComboDam.setDataStrings(options.toArray(new String[options.size()]));
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String getOverrideTransitionToStateName(WfeWorkflowSection section)  {
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
      return section.getSma().isOfType(
         AtsArtifactTypes.DecisionReview) && section.getSma().getCurrentStateName().equals(
            DecisionReviewState.Decision.getName());
   }

   public String getOverrideTransitionToStateName(DecisionReviewArtifact decArt, XComboDam decisionComboDam)  {
      DecisionOption decisionOption = getDecisionOption(decArt, decisionComboDam.get());
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
   public Collection<IAtsUser> getOverrideTransitionToAssignees(AbstractWorkflowArtifact awa, String decision)  {
      if (isApplicable(awa)) {
         DecisionReviewArtifact decArt = (DecisionReviewArtifact) awa;
         return getOverrideTransitionToAssignees(decArt, decision);
      }
      return null;
   }

   public Collection<IAtsUser> getOverrideTransitionToAssignees(DecisionReviewArtifact decArt, String decision)  {
      DecisionOption decisionOption = getDecisionOption(decArt, decision);
      if (decisionOption == null) {
         return null;
      }
      return decisionOption.getAssignees();
   }

   private boolean isApplicable(AbstractWorkflowArtifact awa) {
      return awa.isOfType(AtsArtifactTypes.DecisionReview) && awa.getCurrentStateName().equals(
         DecisionReviewState.Decision.getName());
   }

   private DecisionOption getDecisionOption(DecisionReviewArtifact decRevArt, String decision)  {
      if (decision.equals("")) {
         return null;
      }
      return decRevArt.decisionOptions.getDecisionOption(decision);
   }

}
