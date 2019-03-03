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
package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemFilter {

   IAtsWorkItemFilter isOfType(ArtifactTypeToken... artifactType);

   IAtsWorkItemFilter union(IAtsWorkItemFilter... atsQuery);

   IAtsWorkItemFilter fromTeam(IAtsTeamDefinition teamDef);

   IAtsWorkItemFilter isStateType(StateType... stateType);

   <T extends IAtsWorkItem> Collection<T> getItems();

   IAtsWorkItemFilter withOrValue(AttributeTypeId attributeType, Collection<? extends Object> values);

   Collection<IAtsAction> getActions();

   /**
    * @return Team Workflows or parent Team Workflows if workItem is Review or Task
    */
   Collection<IAtsTeamWorkflow> getTeamWorkflows();

}
