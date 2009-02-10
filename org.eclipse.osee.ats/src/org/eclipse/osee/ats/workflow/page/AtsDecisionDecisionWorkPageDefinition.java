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
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionDecisionWorkPageDefinition extends WorkPageDefinition {

   public static String ID =
         DecisionWorkflowDefinition.ID + "." + DecisionReviewArtifact.DecisionReviewState.Decision.name();
   public static String DECISION_QUESTION_LABEL = "ats.Decision Question";
   public static String DECISION_ANSWER_LABEL = "ats.Decision Answer";

   public AtsDecisionDecisionWorkPageDefinition() {
      this(DecisionReviewArtifact.DecisionReviewState.Decision.name(), ID, null);
   }

   public AtsDecisionDecisionWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(AtsDecisionDecisionWorkPageDefinition.DECISION_QUESTION_LABEL);
      addWorkItem(ATSAttributes.DECISION_ATTRIBUTE.getStoreName());
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

}
