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
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewState;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.widgets.DecisionOption;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
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
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, StateDefinition stateDefinition, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException {
      if (art.isOfType(AtsArtifactTypes.DecisionReview) && stateDefinition.getPageName().equals(
         DecisionReviewState.Decision.getPageName())) {
         if (xWidget == null) {
            throw new OseeStateException("Can't retrieve decision review combo widget to set.");
         }
         if (xWidget.getLabel().equals(AtsAttributeTypes.Decision.getUnqualifiedName())) {
            XComboDam decisionComboDam = (XComboDam) xWidget;
            List<String> options = new ArrayList<String>();
            XDecisionOptions xDecOptions = new XDecisionOptions(WorkflowManager.cast(art));
            for (DecisionOption opt : xDecOptions.getDecisionOptions()) {
               options.add(opt.getName());
            }
            decisionComboDam.setDataStrings(options.toArray(new String[options.size()]));
         }
      }
      return Result.TrueResult;
   }

   @Override
   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws OseeCoreException {
      if (isApplicable(section)) {
         if (section.getTransitionToStateCombo() == null || section.getTransitionToStateCombo().getSelected() == null) {
            return null;
         }
         XWidget xWidget = section.getPage().getLayoutData(AtsAttributeTypes.Decision.getName()).getXWidget();
         XComboDam decisionComboDam = (XComboDam) xWidget;
         DecisionReviewArtifact decArt = (DecisionReviewArtifact) section.getSma();
         return getOverrideTransitionToStateName(decArt, decisionComboDam);
      }
      return null;
   }

   public String getOverrideTransitionToStateName(DecisionReviewArtifact decArt, XComboDam decisionComboDam) throws OseeCoreException {
      DecisionOption decisionOption = getDecisionOption(decArt, decisionComboDam);
      if (decisionOption == null) {
         return null;
      }
      boolean followUpRequired = decisionOption.isFollowupRequired();
      if (followUpRequired) {
         return DecisionReviewState.Followup.getPageName();
      } else {
         return DecisionReviewState.Completed.getPageName();
      }
   }

   @Override
   public Collection<User> getOverrideTransitionToAssignees(SMAWorkFlowSection section) throws OseeCoreException {
      if (isApplicable(section)) {
         XWidget xWidget = section.getPage().getLayoutData(AtsAttributeTypes.Decision.getName()).getXWidget();
         XComboDam decisionComboDam = (XComboDam) xWidget;
         DecisionReviewArtifact decArt = (DecisionReviewArtifact) section.getSma();
         return getOverrideTransitionToAssignees(decArt, decisionComboDam);
      }
      return null;
   }

   public Collection<User> getOverrideTransitionToAssignees(DecisionReviewArtifact decArt, XComboDam decisionComboDam) throws OseeCoreException {
      DecisionOption decisionOption = getDecisionOption(decArt, decisionComboDam);
      if (decisionOption == null) {
         return null;
      }
      return decisionOption.getAssignees();
   }

   private boolean isApplicable(SMAWorkFlowSection section) {
      return section.getSma().isOfType(AtsArtifactTypes.DecisionReview) && section.getSma().getCurrentStateName().equals(
         DecisionReviewState.Decision.getPageName());
   }

   private DecisionOption getDecisionOption(DecisionReviewArtifact decRevArt, XComboDam decisionComboDam) throws OseeCoreException {
      String decision = decisionComboDam.get();
      if (decision.equals("")) {
         return null;
      }
      return decRevArt.decisionOptions.getDecisionOption(decision);
   }

}
