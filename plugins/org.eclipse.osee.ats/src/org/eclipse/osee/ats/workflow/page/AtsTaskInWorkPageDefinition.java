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
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskInWorkPageDefinition extends WorkPageDefinition {

   public static String ID = TaskWorkflowDefinition.ID + "." + TaskStates.InWork.name();

   public AtsTaskInWorkPageDefinition() {
      this(TaskStates.InWork.name(), ID, null);
   }

   public AtsTaskInWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem("ats.Title");
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(AtsAttributeTypes.ATS_RESOLUTION);
      addWorkItem(AtsAttributeTypes.ATS_ESTIMATED_HOURS);
      addWorkItem(AtsAttributeTypes.ATS_ESTIMATED_COMPLETION_DATE);
      addWorkItem(AtsAttributeTypes.ATS_RELATED_TO_STATE);
      addWorkItem(AtsAttributeTypes.ATS_SMA_NOTE);
      addWorkItem(AtsAttributeTypes.ATS_CATEGORY_1);
   }

}
