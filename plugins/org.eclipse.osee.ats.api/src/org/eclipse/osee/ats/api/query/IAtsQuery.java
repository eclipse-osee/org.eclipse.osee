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
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQuery {

   IAtsQuery andTeam(IAtsTeamDefinition teamDef) ;

   IAtsQuery andStateType(StateType... stateType) ;

   <T extends IAtsWorkItem> Collection<T> getItems() ;

   <T extends IAtsWorkItem> Collection<T> getItems(Class<T> clazz);

   IAtsQuery andAttr(AttributeTypeId attributeType, Collection<String> values, QueryOption... queryOption) ;

   IAtsQuery andAtsIds(Collection<String> atsIds);

   IAtsQuery andRelated(IAtsObject object, RelationTypeSide relation);

   IAtsQuery andLegacyIds(Collection<String> legacyIds);

   IAtsWorkItemFilter andFilter() ;

   IAtsQuery isOfType(IArtifactType... artifactType);

   IAtsQuery andUuids(Long... uuid);

   IAtsQuery andAttr(AttributeTypeId attributeType, String value, QueryOption... queryOption);

   <T extends IAtsWorkItem> ResultSet<T> getResults();

   IAtsQuery andAssignee(IAtsUser... assignees);

   <T extends ArtifactId> ResultSet<T> getResultArtifacts();

   IAtsQuery isOfType(Collection<WorkItemType> workItemTypes);

   IAtsQuery isOfType(WorkItemType... workItemType);

   IAtsQuery andOriginator(IAtsUser atsUser);

   IAtsQuery andSubscribed(IAtsUser atsUser);

   IAtsQuery andFavorite(IAtsUser atsUser);

   IAtsQuery andTeam(List<Long> teamDefUuids);

   IAtsQuery andActionableItem(List<Long> aiUuids);

   IAtsQuery andVersion(Long versionUuid);

   IAtsQuery andState(String stateName);

   IAtsQuery andProgram(Long programUuid);

   IAtsQuery andInsertion(Long insertionUuid);

   IAtsQuery andInsertionActivity(Long insertionActivityUuid);

   IAtsQuery andWorkPackage(Long workPackageUuid);

   IAtsQuery andColorTeam(String colorTeam);

   IAtsQuery andActionableItem(IAtsActionableItem actionableItem);

   Collection<ArtifactId> getItemIds() ;

   IAtsQuery andAssigneeWas(IAtsUser... assignees);

   IAtsQuery andTeam(Collection<IAtsTeamDefinition> teamDefs);

   IAtsWorkItemFilter createFilter();

   IAtsQuery andReleased(ReleasedOption releaseOption);

   IAtsQuery andTeamWorkflowAttr(AttributeTypeId attributeType, List<String> values, QueryOption... queryOptions);

   IAtsQuery andFilter(IAtsQueryFilter queryFilter);

   IAtsQuery andTag(String... tags);

   IAtsQuery andActive(boolean active);

   /**
    * @return true if one or more results were found
    */
   boolean exists();

   IAtsQuery andNotExists(AttributeTypeId attributeType);

   IAtsQuery andName(String name);

   IAtsQuery andWorkItemType(WorkItemType... workItemTypes);

   IAtsQuery andVersion(IAtsVersion version);

   IAtsQuery andName(String name, QueryOption... queryOption);
   
   IAtsQuery andExists(AttributeTypeToken attributeType);

}