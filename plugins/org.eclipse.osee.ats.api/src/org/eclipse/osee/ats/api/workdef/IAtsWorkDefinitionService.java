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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionService {

   void internalSetWorkDefinition(IAtsWorkItem workItem, IAtsWorkDefinition workDef);

   IAtsWorkDefinition getWorkDefinition(IAtsWorkItem workItem);

   IAtsWorkDefinition getWorkDefinition(String name);

   IAtsWorkDefinition getDefaultPeerToPeerWorkflowDefinition();

   IAtsWorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf);

   IAtsWorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem);

   IAtsWorkDefinition computedWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf);

   boolean isStateWeightingEnabled(IAtsWorkDefinition workDef);

   Collection<String> getStateNames(IAtsWorkDefinition workDef);

   List<IAtsStateDefinition> getStatesOrderedByOrdinal(IAtsWorkDefinition workDef);

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   List<IAtsWidgetDefinition> getWidgetsFromLayoutItems(IAtsStateDefinition stateDef);

   IAtsWorkDefinition getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review);

   IAtsStateDefinition getStateDefinitionByName(IAtsWorkItem workItem, String stateName);

   Collection<String> getAllValidStateNames(XResultData resultData) throws Exception;

   Collection<IAtsRuleDefinition> getAllRuleDefinitions();

   void getStatesOrderedByDefaultToState(IAtsWorkDefinition workDef, IAtsStateDefinition stateDefinition, List<IAtsStateDefinition> pages);

   boolean hasWidgetNamed(IAtsStateDefinition stateDef, String name);

   boolean teamDefHasRule(IAtsWorkItem workItem, RuleDefinitionOption rule);

   boolean isInState(IAtsWorkItem workItem, IAtsStateDefinition stateDef);

   Collection<IAtsWorkDefinition> getAllWorkDefinitions();

   IAtsWorkDefinition getWorkDefinition(Long id);

   IAtsWorkDefinition computeWorkDefinition(IAtsWorkItem workItem);

   ArtifactToken getWorkDefArt(String workDefName);

   IAtsWorkDefinition computeWorkDefinitionForTeamWfNotYetCreated(IAtsTeamWorkflow teamWf, INewActionListener newActionListener);

   void setWorkDefinitionAttrs(IAtsTeamDefinition topTeam, NamedIdBase id, IAtsChangeSet changes);

   IAtsWorkDefinition computeAndSetWorkDefinitionAttrs(IAtsWorkItem workItem, INewActionListener newActionListener, IAtsChangeSet changes);

   void setWorkDefinitionAttrs(IAtsWorkItem workItem, IAtsWorkDefinition workDefinition, IAtsChangeSet changes);

   void setWorkDefinitionAttrs(IAtsTeamDefinition teamDef, IAtsWorkDefinition workDefinition, IAtsChangeSet changes);

   void internalClearWorkDefinition(IAtsWorkItem workItem);

   void addWorkDefinition(IAtsWorkDefinitionBuilder workDefBuilder);

   void setWorkDefinitionAttrs(IAtsTeamWorkflow teamWf, NamedIdBase id, IAtsChangeSet changes);

   IAtsWorkDefinition getWorkDefinition(Id id);

   IAtsWorkDefinition getWorkDefinitionFromAsObject(IAtsObject atsObject, AttributeTypeToken workDefAttrTypeId);

   IAtsRuleDefinition getRuleDefinition(String name);

   void addRuleDefinition(IAtsRuleDefinition ruleDef);

}
