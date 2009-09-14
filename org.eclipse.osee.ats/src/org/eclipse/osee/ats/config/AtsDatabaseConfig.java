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
package org.eclipse.osee.ats.config;

import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.GoalWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

public class AtsDatabaseConfig implements IDbInitializationTask {

   public void run() throws OseeCoreException {
      AtsFolderUtil.createAtsFolders();

      configWorkItemDefinitions(WriteType.New, null);

      AtsUtil.getAtsAdminGroup().getGroupArtifact().persist();
   }

   public static void configWorkItemDefinitions(WriteType writeType, XResultData xResultData) throws OseeCoreException {

      // Import Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData,
            AtsWorkDefinitions.getAtsWorkDefinitions());

      new TeamWorkflowDefinition().config(writeType, xResultData);
      new TaskWorkflowDefinition().config(writeType, xResultData);
      new GoalWorkflowDefinition().config(writeType, xResultData);
      new SimpleWorkflowDefinition().config(writeType, xResultData);
      new DecisionWorkflowDefinition().config(writeType, xResultData);
      new PeerToPeerWorkflowDefinition().config(writeType, xResultData);

   }

}