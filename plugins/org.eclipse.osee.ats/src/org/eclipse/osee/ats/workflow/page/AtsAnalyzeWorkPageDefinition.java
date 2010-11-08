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
import org.eclipse.osee.ats.field.ChangeTypeColumn;
import org.eclipse.osee.ats.field.EstimatedHoursColumn;
import org.eclipse.osee.ats.field.PriorityColumn;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsAnalyzeWorkPageDefinition extends WorkPageDefinition {

   public final static String ID = TeamWorkflowDefinition.ID + "." + DefaultTeamState.Analyze.name();

   public AtsAnalyzeWorkPageDefinition() {
      this(DefaultTeamState.Analyze.name(), ID, null);
   }

   public AtsAnalyzeWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem(AtsAttributeTypes.WorkPackage);
      addWorkItem(AtsAttributeTypes.Problem);
      addWorkItem(AtsAttributeTypes.ProposedResolution);
      addWorkItem(ChangeTypeColumn.ChangeTypeAttribute);
      addWorkItem(PriorityColumn.PriorityTypeAttribute);
      addWorkItem(AtsAttributeTypes.NeedBy);
      addWorkItem(EstimatedHoursColumn.EstimatedHours);
   }

}
