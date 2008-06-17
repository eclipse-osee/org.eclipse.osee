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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAWorkFlowSection;
import org.eclipse.osee.ats.util.widgets.DecisionOption;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewDecisionStateItem extends AtsStateItem {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   public String getId() {
      return "osee.ats.decisionReview.Decision";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#xWidgetCreating(org.eclipse.osee.framework.ui.skynet.widgets.XWidget,
    *      org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.ats.workflow.AtsWorkPage,
    *      org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   public Result xWidgetCreating(XWidget xWidget, FormToolkit toolkit, AtsWorkPage page, Artifact art, XModifiedListener xModListener, boolean isEditable) throws OseeCoreException, SQLException {
      if (xWidget.getLabel().equals(ATSAttributes.DECISION_ATTRIBUTE.getDisplayName())) {
         if (xWidget == null) throw new IllegalStateException("Can't retrieve decision review combo widget to set.");
         XComboDam decisionComboDam = (XComboDam) xWidget;
         List<String> options = new ArrayList<String>();
         XDecisionOptions xDecOptions = new XDecisionOptions((StateMachineArtifact) art);
         for (DecisionOption opt : xDecOptions.getDecisionOptions())
            options.add(opt.getName());
         decisionComboDam.setDataStrings(options.toArray(new String[options.size()]));
      }
      return Result.TrueResult;
   }

   public String getOverrideTransitionToStateName(SMAWorkFlowSection section) throws OseeCoreException, SQLException {
      DecisionOption decisionOption = getDecisionOption(section);
      if (decisionOption == null) return null;
      boolean followUpRequired = decisionOption.isFollowupRequired();
      if (section.getTransitionToStateCombo() == null || section.getTransitionToStateCombo().getSelected() == null) return null;
      if (followUpRequired)
         return DecisionReviewArtifact.StateNames.Followup.name();
      else
         return DecisionReviewArtifact.StateNames.Completed.name();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getOverrideTransitionToAssignees(org.eclipse.osee.ats.editor.SMAWorkFlowSection)
    */
   public Collection<User> getOverrideTransitionToAssignees(SMAWorkFlowSection section) throws OseeCoreException, SQLException {
      DecisionOption decisionOption = getDecisionOption(section);
      if (decisionOption == null) return null;
      return decisionOption.getAssignees();
   }

   private DecisionOption getDecisionOption(SMAWorkFlowSection section) throws OseeCoreException, SQLException {
      XWidget xWidget = section.getPage().getLayoutData(ATSAttributes.DECISION_ATTRIBUTE.getStoreName()).getXWidget();
      XComboDam decisionComboDam = (XComboDam) xWidget;
      String decision = decisionComboDam.get();
      if (decision.equals("")) return null;
      DecisionReviewArtifact decArt = (DecisionReviewArtifact) section.getSmaMgr().getSma();
      DecisionOption decisionOption = decArt.decisionOptions.getDecisionOption(decision);
      return decisionOption;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() throws OseeCoreException, SQLException {
      return "AtsDecisionReviewDecisionStateItem - Add decision options to review state based on prepare state's entries.";
   }

}
