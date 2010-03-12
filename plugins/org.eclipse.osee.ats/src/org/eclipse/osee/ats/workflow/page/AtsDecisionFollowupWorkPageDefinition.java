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
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionFollowupWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DecisionWorkflowDefinition.ID + "." + DecisionReviewArtifact.DecisionReviewState.Followup.name();

   public AtsDecisionFollowupWorkPageDefinition() {
      this(DecisionReviewArtifact.DecisionReviewState.Followup.name(), ID, null);
   }

   public AtsDecisionFollowupWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

}
