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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemFilter {

   public abstract IAtsWorkItemFilter isOfType(IArtifactType... artifactType) throws OseeCoreException;

   public abstract IAtsWorkItemFilter union(IAtsWorkItemFilter... atsQuery) throws OseeCoreException;

   public abstract IAtsWorkItemFilter fromTeam(IAtsTeamDefinition teamDef) throws OseeCoreException;

   public abstract IAtsWorkItemFilter isStateType(StateType... stateType) throws OseeCoreException;

   public <T extends IAtsWorkItem> Collection<T> getItems() throws OseeCoreException;

   public abstract IAtsWorkItemFilter withOrValue(AttributeTypeId attributeType, Collection<? extends Object> values) throws OseeCoreException;

   public abstract Collection<IAtsAction> getActions();

   /**
    * @return Team Workflows or parent Team Workflows if workItem is Review or Task
    */
   public abstract Collection<IAtsTeamWorkflow> getTeamWorkflows();

}