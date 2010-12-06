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
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class AtsEndorseWorkPageDefinition extends WorkPageDefinition {

   public final static String ID = TeamWorkflowDefinition.ID + "." + TeamState.Endorse.getPageName();

   public AtsEndorseWorkPageDefinition(int ordinal) {
      this(TeamState.Endorse.getPageName(), ID, null, ordinal);
   }

   public AtsEndorseWorkPageDefinition(String name, String pageId, String parentId, int ordinal) {
      super(name, pageId, parentId, WorkPageType.Working, ordinal);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem("ats.Title");
      addWorkItem(AtsAttributeTypes.Description);
      addWorkItem(AtsAttributeTypes.ProposedResolution);
      addWorkItem(AtsAttributeTypes.ChangeType);
      addWorkItem(AtsAttributeTypes.PriorityType);
      addWorkItem(AtsAttributeTypes.NeedBy);
      addWorkItem(AtsAttributeTypes.ValidationRequired);
      addWorkItem(AtsAttributeTypes.WorkPackage);
      addWorkItem(AtsAttributeTypes.UserCommunity);
   }

}
