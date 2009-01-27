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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

public class AtsDatabaseConfig implements IDbInitializationTask {

   public void run(OseeConnection connection) throws OseeCoreException {
      createAtsTopLevelConfigObjects();

      // Configure WorkItemDefinitions
      configWorkItemDefinitions(WriteType.New, null);

   }

   public static void configWorkItemDefinitions(WriteType writeType, XResultData xResultData) throws OseeCoreException {

      // Import Work Item Definitions
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData,
            AtsWorkDefinitions.getAtsWorkDefinitions());

      new TeamWorkflowDefinition().config(writeType, null);
      new TaskWorkflowDefinition().config(writeType, null);
      new SimpleWorkflowDefinition().config(writeType, null);
      new DecisionWorkflowDefinition().config(writeType, null);
      new PeerToPeerWorkflowDefinition().config(writeType, null);

   }

   private void createAtsTopLevelConfigObjects() throws OseeCoreException {
      SkynetTransaction transaction1 = new SkynetTransaction(AtsPlugin.getAtsBranch());
      AtsConfig.getInstance().getOrCreateAtsHeadingArtifact(transaction1);
      transaction1.execute();

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      AtsConfig.getInstance().getOrCreateAtsHeadingArtifact(transaction);
      AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact(transaction);
      AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact(transaction);
      AtsConfig.getInstance().getOrCreateWorkFlowsFolderArtifact(transaction);
      AtsConfig.getInstance().getOrCreateWorkRulesFolderArtifact(transaction);
      AtsConfig.getInstance().getOrCreateWorkWidgetsFolderArtifact(transaction);
      AtsConfig.getInstance().getOrCreateWorkPagesFolderArtifact(transaction);
      transaction.execute();
   }
}