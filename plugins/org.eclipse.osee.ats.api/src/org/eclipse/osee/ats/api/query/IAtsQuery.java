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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQuery {

   IAtsQuery isOfType(Class<? extends IAtsWorkItem> clazz) throws OseeCoreException;

   IAtsQuery fromTeam(IAtsTeamDefinition teamDef) throws OseeCoreException;

   IAtsQuery isStateType(StateType... stateType) throws OseeCoreException;

   <T extends IAtsWorkItem> Collection<T> getItems() throws OseeCoreException;

   IAtsQuery andAttr(IAttributeType attributeType, Collection<String> values, QueryOption... queryOption) throws OseeCoreException;

   IAtsQuery andAtsIds(Collection<String> atsIds);

   IAtsQuery andRelated(IAtsObject object, IRelationTypeSide relation);

   IAtsQuery andLegacyIds(Collection<String> legacyIds);

   IAtsWorkItemFilter andFilter() throws OseeCoreException;

   IAtsQuery isOfType(IArtifactType... artifactType);

   IAtsQuery andUuids(Long... uuid);

   IAtsQuery andAttr(IAttributeType attributeType, String value, QueryOption... queryOption);

   <T extends IAtsWorkItem> ResultSet<T> getResults();

   IAtsQuery isGoal();

   IAtsQuery andAssignee(IAtsUser... assignees);

   <T extends ArtifactId> ResultSet<T> getResultArtifacts();

}