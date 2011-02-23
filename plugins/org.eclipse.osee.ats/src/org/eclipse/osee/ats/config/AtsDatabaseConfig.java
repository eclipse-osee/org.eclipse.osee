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

import java.util.Arrays;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsArtifactToken;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.GoalWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.SimpleWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

public class AtsDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {
      createAtsFolders();

      AtsWorkDefinitionSheetProviders.initializeDatabase();

      Artifact topAi = ActionableItemArtifact.getTopActionableItem();
      topAi.setSoleAttributeValue(AtsAttributeTypes.Actionable, false);
      topAi.persist("Set Top AI to Non Actionable");

      if (AtsUtil.dbInitWorkItemDefs()) {
         configWorkItemDefinitions(WriteType.New, null);
      }

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

   public static void createAtsFolders() throws OseeCoreException {
      Branch atsBranch = AtsUtil.getAtsBranch();
      SkynetTransaction transaction = new SkynetTransaction(atsBranch, "Create ATS Folders");

      Artifact headingArt = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.HeadingFolder, atsBranch);
      if (!headingArt.hasParent()) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(atsBranch);
         rootArt.addChild(headingArt);
         headingArt.persist(transaction);
      }
      for (IArtifactToken token : Arrays.asList(AtsArtifactToken.TopActionableItem, AtsArtifactToken.TopTeamDefinition,
         AtsArtifactToken.ConfigFolder, //
         AtsArtifactToken.WorkDefinitionsFolder)) {
         Artifact art = OseeSystemArtifacts.getOrCreateArtifact(token, atsBranch);
         headingArt.addChild(art);
         art.persist(transaction);
      }
      if (AtsUtil.dbInitWorkItemDefs()) {
         for (IArtifactToken token : Arrays.asList(AtsArtifactToken.WorkFlowsFolder, //
            AtsArtifactToken.WorkPagesFolder, AtsArtifactToken.WorkWidgetsFolder, //
            AtsArtifactToken.WorkRulesFolder)) {
            Artifact art = OseeSystemArtifacts.getOrCreateArtifact(token, atsBranch);
            headingArt.addChild(art);
            art.persist(transaction);
         }
      }
      transaction.execute();
   }
}