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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public interface IAtsQuery {

   public abstract IAtsQuery isOfType(IArtifactType... artifactType) throws OseeCoreException;

   public abstract IAtsQuery union(IAtsQuery... atsQuery) throws OseeCoreException;

   public abstract IAtsQuery fromTeam(IAtsTeamDefinition teamDef) throws OseeCoreException;

   public abstract IAtsQuery isStateType(StateType... stateType) throws OseeCoreException;

   public abstract Collection<? extends IAtsWorkItem> getItems() throws OseeCoreException;

   public abstract IAtsQuery withOrValue(IAttributeType attributeType, Collection<? extends Object> values) throws OseeCoreException;

}