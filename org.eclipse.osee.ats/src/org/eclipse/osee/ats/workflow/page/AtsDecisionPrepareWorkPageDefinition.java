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
public class AtsDecisionPrepareWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DecisionWorkflowDefinition.ID + "." + DecisionReviewArtifact.DecisionReviewState.Prepare.name();

   public AtsDecisionPrepareWorkPageDefinition() {
      this(DecisionReviewArtifact.DecisionReviewState.Prepare.name(), ID, null);
   }

   public AtsDecisionPrepareWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem("ats.Title");
      addWorkItem(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName());
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.DEADLINE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
   }

}
