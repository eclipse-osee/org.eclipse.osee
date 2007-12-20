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

import java.util.Collection;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.XDecisionOptions;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItem extends AtsStateItem {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   public String getId() {
      return "osee.ats.decisionReview.Prepare";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#transitioning(java.lang.String, java.lang.String,
    *      java.util.Collection)
    */
   public Result transitioning(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees) throws Exception {
      if (fromState.equals(DecisionReviewArtifact.StateNames.Prepare) && toState.equals(DecisionReviewArtifact.StateNames.Decision)) {
         XDecisionOptions decOptions = new XDecisionOptions(smaMgr.getSma());
         return decOptions.validateDecisionOptions();
      }
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() {
      return "AtsDecisionReviewPrepareStateItem - Add validation of decision options prior to transitioning.";
   }

}
