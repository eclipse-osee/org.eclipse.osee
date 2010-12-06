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
import org.eclipse.osee.ats.artifact.GoalState;
import org.eclipse.osee.ats.column.EstimatedHoursXWidget;
import org.eclipse.osee.ats.workflow.flow.GoalWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class AtsGoalInWorkPageDefinition extends WorkPageDefinition {

   public final static String ID = GoalWorkflowDefinition.ID + "." + GoalState.InWork.getPageName();

   public AtsGoalInWorkPageDefinition(int ordinal) {
      this(GoalState.InWork.getPageName(), ID, null, ordinal);
   }

   public AtsGoalInWorkPageDefinition(String name, String pageId, String parentId, int ordinal) {
      super(name, pageId, parentId, WorkPageType.Working, ordinal);
      addWorkItem("ats.Title");
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(EstimatedHoursXWidget.ATS_ESTIMATED_HOURS_NOT_REQUIRED_ID);
      addWorkItem(AtsAttributeTypes.GoalOrderVote);
   }

}
