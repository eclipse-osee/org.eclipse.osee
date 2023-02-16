/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
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

   void internalSetWorkDefinition(IAtsWorkItem workItem, WorkDefinition workDef);

   WorkDefinition getWorkDefinition(IAtsWorkItem workItem);

   WorkDefinition getWorkDefinitionByName(String name);

   WorkDefinition getDefaultPeerToPeerWorkflowDefinition();

   WorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreated(IAtsTeamWorkflow teamWf);

   WorkDefinition getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(IAtsActionableItem actionableItem);

   WorkDefinition computedWorkDefinitionForTaskNotYetCreated(IAtsTeamWorkflow teamWf);

   Collection<String> getStateNames(WorkDefinition workDef);

   List<StateDefinition> getStatesOrderedByOrdinal(WorkDefinition workDef);

   /**
    * Recursively decend StateItems and grab all widgetDefs.<br>
    * <br>
    * Note: Modifing this list will not affect the state widgets. Use addStateItem().
    */
   List<WidgetDefinition> getWidgetsFromLayoutItems(StateDefinition stateDef);

   List<WidgetDefinition> getWidgetsFromLayoutItems(StateDefinition stateDef, List<LayoutItem> layoutItems);

   WorkDefinition getWorkDefinitionForPeerToPeerReview(IAtsPeerToPeerReview review);

   StateDefinition getStateDefinitionByName(IAtsWorkItem workItem, String stateName);

   /**
    * @return unique set of state names from work definitions. This should only be used to store state names, not
    * retrieve them for use. Use getAllValidStateNamesFromConfig.
    */
   Collection<String> computeAllValidStateNames();

   /**
    * @return cached set of state names for use in UI
    */
   Collection<String> getAllValidStateNamesFromConfig();

   boolean hasWidgetNamed(StateDefinition stateDef, String name);

   boolean teamDefHasRule(IAtsWorkItem workItem, RuleDefinitionOption rule);

   boolean isInState(IAtsWorkItem workItem, StateDefinition stateDef);

   Collection<WorkDefinition> getAllWorkDefinitions();

   WorkDefinition getWorkDefinition(Long id);

   WorkDefinition computeWorkDefinition(IAtsWorkItem workItem);

   ArtifactToken getWorkDefArt(String workDefName);

   WorkDefinition computeWorkDefinitionForTeamWfNotYetCreated(IAtsTeamDefinition teamDef, Collection<INewActionListener> newActionListeners);

   void setWorkDefinitionAttrs(IAtsTeamDefinition topTeam, NamedIdBase id, IAtsChangeSet changes);

   void setWorkDefinitionAttrs(IAtsWorkItem workItem, WorkDefinition workDefinition, IAtsChangeSet changes);

   void setWorkDefinitionAttrs(IAtsTeamDefinition teamDef, WorkDefinition workDefinition, IAtsChangeSet changes);

   void internalClearWorkDefinition(IAtsWorkItem workItem);

   void addWorkDefinition(IAtsWorkDefinitionBuilder workDefBuilder);

   void setWorkDefinitionAttrs(IAtsTeamWorkflow teamWf, NamedIdBase id, IAtsChangeSet changes);

   WorkDefinition getWorkDefinition(Id id);

   WorkDefinition getWorkDefinitionFromAsObject(IAtsObject atsObject, AttributeTypeToken workDefAttrTypeId);

   XResultData validateWorkDefinitions();

   WorkDefinition computeWorkDefinition(IAtsWorkItem workItem, boolean useAttr);

   /**
    * @return widget definitions from header and all states
    */
   Collection<WidgetDefinition> getWidgets(WorkDefinition workDef);

   public Collection<String> updateAllValidStateNames();

}
