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
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionFollowupWorkPageDefinition extends WorkPageDefinition {

   public final static String ID = DecisionWorkflowDefinition.ID + "." + DecisionReviewState.Followup.getPageName();

   public AtsDecisionFollowupWorkPageDefinition(int ordinal) {
      this(DecisionReviewState.Followup.getPageName(), ID, null, ordinal);
   }

   public AtsDecisionFollowupWorkPageDefinition(String name, String pageId, String parentId, int ordinal) {
      super(name, pageId, parentId, WorkPageType.Working, ordinal);
      addWorkItem(AtsAttributeTypes.Resolution);
   }
}