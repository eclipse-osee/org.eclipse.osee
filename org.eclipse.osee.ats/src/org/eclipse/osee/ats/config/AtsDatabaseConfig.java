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
import java.sql.SQLException;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.database.initialize.tasks.DbInitializationTask;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

public class AtsDatabaseConfig extends DbInitializationTask {

   public void run(Connection connection)throws OseeCoreException, SQLException{
      createAtsTopLevelConfigObjects();

      // Configure WorkItemDefinitions
      configWorkItemDefinitions(WriteType.New, null);

   }

   public static void configWorkItemDefinitions(WriteType writeType, XResultData xResultData)throws OseeCoreException, SQLException{

      // Import Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, null, AtsWorkDefinitions.getAtsWorkDefinitions());

      new TeamWorkflowDefinition().config(writeType, null);
      new TaskWorkflowDefinition().config(writeType, null);
      new SimpleWorkflowDefinition().config(writeType, null);
      new DecisionWorkflowDefinition().config(writeType, null);
      new PeerToPeerWorkflowDefinition().config(writeType, null);

   }

   private void createAtsTopLevelConfigObjects()throws OseeCoreException, SQLException{
      AtsConfig.getInstance().getOrCreateAtsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact();
      AtsConfig.getInstance().getOrCreateWorkFlowsFolderArtifact();
      AtsConfig.getInstance().getOrCreateWorkRulesFolderArtifact();
      AtsConfig.getInstance().getOrCreateWorkWidgetsFolderArtifact();
      AtsConfig.getInstance().getOrCreateWorkPagesFolderArtifact();
   }
}