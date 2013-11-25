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
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

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

   IWorkDefinitionMatch getWorkDefinition(IAtsWorkItem workItem) throws OseeCoreException;

   IWorkDefinitionMatch getWorkDefinition(String id) throws OseeCoreException;

   IWorkDefinitionMatch getWorkDefinitionForTask(IAtsTask task) throws OseeCoreException;

   IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData);

   IWorkDefinitionMatch getDefaultPeerToPeerWorkflowDefinitionMatch() throws OseeCoreException;

   IWorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IWorkDefinitionMatch getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem) throws OseeCoreException;

   IWorkDefinitionMatch getWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   boolean isTaskOverridingItsWorkDefinition(IAtsTask task) throws MultipleAttributesExist, OseeCoreException;

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

   IWorkDefinitionMatch getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review) throws OseeCoreException;

   IAtsStateDefinition getStateDefinitionByName(IAtsWorkItem workItem, String stateName) throws OseeCoreException;

   Collection<String> getAllValidStateNames(XResultData resultData) throws Exception;

}