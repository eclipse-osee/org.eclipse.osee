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
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.ESTIMATED_COMPLETION_DATE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.CATEGORY_ATTRIBUTE.getStoreName());
   }

}
