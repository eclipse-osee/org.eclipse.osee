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

package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemFilter {

   IAtsWorkItemFilter isOfType(ArtifactTypeToken... artifactType);

   IAtsWorkItemFilter union(IAtsWorkItemFilter... atsQuery);

   IAtsWorkItemFilter fromTeam(IAtsTeamDefinition teamDef);

   IAtsWorkItemFilter isStateType(StateType... stateType);

   <T extends IAtsWorkItem> Collection<T> getItems();

   IAtsWorkItemFilter withOrValue(AttributeTypeToken attributeType, Collection<? extends Object> values);

   Collection<IAtsAction> getActions();

   /**
    * @return Team Workflows or parent Team Workflows if workItem is Review or Task
    */
   Collection<IAtsTeamWorkflow> getTeamWorkflows();

}
