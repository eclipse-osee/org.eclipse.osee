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

import java.sql.Connection;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

public class AtsDatabaseConfig extends DbInitializationTask {

   public void run(Connection connection) throws Exception {
      createAtsTopLevelConfigObjects();

      // Import Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.New, null,
            AtsWorkDefinitions.getAtsWorkDefinitions());

      // Import Team Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.New, null,
            TeamWorkflowDefinition.getAtsWorkDefinitions());

      // Import Task Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.New, null,
            TaskWorkflowDefinition.getAtsWorkDefinitions());

      // Import Simple Team Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.New, null,
            SimpleWorkflowDefinition.getAtsWorkDefinitions());

      // Import Decision Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.New, null,
            DecisionWorkflowDefinition.getAtsWorkDefinitions());

      // Import Peer to Peer Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.New, null,
            PeerToPeerWorkflowDefinition.getAtsWorkDefinitions());

   }

   private void createAtsTopLevelConfigObjects() throws Exception {
      AtsConfig.getInstance().getOrCreateAtsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateWorkFlowsFolderArtifact();
      AtsConfig.getInstance().getOrCreateWorkRulesFolderArtifact();
      AtsConfig.getInstance().getOrCreateWorkWidgetsFolderArtifact();
      AtsConfig.getInstance().getOrCreateWorkPagesFolderArtifact();
   }
}