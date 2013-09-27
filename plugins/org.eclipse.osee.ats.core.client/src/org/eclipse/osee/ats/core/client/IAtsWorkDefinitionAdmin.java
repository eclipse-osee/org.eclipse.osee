/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionAdmin {

   public static final String TaskWorkflowDefinitionId = "WorkDef_Task_Default";
   public static final String GoalWorkflowDefinitionId = "WorkDef_Goal";
   public static final String PeerToPeerDefaultWorkflowDefinitionId = "WorkDef_Review_PeerToPeer";
   public static final String DecisionWorkflowDefinitionId = "WorkDef_Review_Decision";
   public static final String TeamWorkflowDefaultDefinitionId = "WorkDef_Team_Default";

   void clearCaches();

   void addWorkDefinition(IAtsWorkDefinition workDef) throws OseeCoreException;

   void removeWorkDefinition(IAtsWorkDefinition workDef) throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinition(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<IAtsWorkDefinition> getLoadedWorkDefinitions() throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinition(String id) throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinitionForTask(IAtsTask task) throws OseeCoreException;

   Set<IAtsWorkDefinition> loadAllDefinitions() throws OseeCoreException;

   IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData);

   WorkDefinitionMatch getDefaultPeerToPeerWorkflowDefinitionMatch() throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem) throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinitionForTaskNotYetCreated(TeamWorkFlowArtifact teamWf) throws OseeCoreException;

   boolean isTaskOverridingItsWorkDefinition(TaskArtifact taskArt) throws MultipleAttributesExist, OseeCoreException;

   IAtsWorkDefinition getWorkDef(String id, XResultData resultData) throws Exception;

   boolean isStateWeightingEnabled(IAtsWorkDefinition workDef);

   Collection<String> getStateNames(IAtsWorkDefinition workDef);

   List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef);

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef);

   String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) throws Exception;

   Collection<? extends IAtsWorkItem> getWorkItems(List<Artifact> arts);

   List<Artifact> get(Collection<? extends IAtsWorkItem> workItems, Class<Artifact> class1) throws OseeCoreException;

   WorkDefinitionMatch getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review) throws OseeCoreException;

}