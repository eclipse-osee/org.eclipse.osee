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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAnalyzeWorkPageDefinition extends WorkPageDefinition {

   public static String ID = TeamWorkflowDefinition.ID + "." + DefaultTeamState.Analyze.name();

   public AtsAnalyzeWorkPageDefinition() {
      this(DefaultTeamState.Analyze.name(), ID, null);
   }

   public AtsAnalyzeWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem(AtsAttributeTypes.ATS_WORK_PACKAGE);
      addWorkItem(AtsAttributeTypes.ATS_PROBLEM);
      addWorkItem(AtsAttributeTypes.ATS_PROPOSED_RESOLUTION);
      addWorkItem(AtsAttributeTypes.ATS_CHANGE_TYPE);
      addWorkItem(AtsAttributeTypes.ATS_PRIORITY_TYPE);
      addWorkItem(AtsAttributeTypes.ATS_NEED_BY);
      addWorkItem(AtsAttributeTypes.ATS_ESTIMATED_HOURS);
   }

}
