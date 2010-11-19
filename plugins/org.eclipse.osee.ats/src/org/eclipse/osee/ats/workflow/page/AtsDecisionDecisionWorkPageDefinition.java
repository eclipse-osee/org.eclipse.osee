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

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.DecisionReviewState;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionDecisionWorkPageDefinition extends WorkPageDefinition {

   public final static String ID = DecisionWorkflowDefinition.ID + "." + DecisionReviewState.Decision.getPageName();
   public final static String DECISION_QUESTION_LABEL = "ats.Decision Question";
   public final static String DECISION_ANSWER_LABEL = "ats.Decision Answer";

   public AtsDecisionDecisionWorkPageDefinition() {
      this(DecisionReviewState.Decision.getPageName(), ID, null);
   }

   public AtsDecisionDecisionWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId, WorkPageType.Working);
      addWorkItem(AtsDecisionDecisionWorkPageDefinition.DECISION_QUESTION_LABEL);
      addWorkItem(AtsAttributeTypes.Decision);
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(AtsAttributeTypes.Resolution);
   }

}
