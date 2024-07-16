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
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsQuery {

   IAtsQuery andTeam(IAtsTeamDefinition teamDef);

   IAtsQuery andStateType(StateType... stateType);

   <T extends IAtsWorkItem> Collection<T> getItems();

   <T extends IAtsWorkItem> Collection<T> getItemsNew();

   <T extends IAtsWorkItem> Collection<T> getItems(Class<T> clazz);

   IAtsQuery andAttr(AttributeTypeId attributeType, Collection<String> values, QueryOption... queryOption);

   IAtsQuery andAtsIds(Collection<String> atsIds);

   IAtsQuery andRelated(IAtsObject object, RelationTypeSide relation);

   IAtsQuery andLegacyIds(Collection<String> legacyIds);

   IAtsWorkItemFilter andFilter();

   IAtsQuery isOfType(ArtifactTypeToken... artifactType);

   IAtsQuery andIds(Long... id);

   IAtsQuery andAttr(AttributeTypeId attributeType, String value, QueryOption... queryOption);

   <T extends IAtsWorkItem> ResultSet<T> getResults();

   <T extends IAtsWorkItem> ResultSet<T> getResultsNew();

   IAtsQuery andAssignee(AtsUser... assignees);

   <T extends ArtifactToken> ResultSet<T> getResultArtifacts();

   <T extends ArtifactToken> ResultSet<T> getResultArtifactsNew();

   IAtsQuery isOfType(Collection<WorkItemType> workItemTypes);

   IAtsQuery isOfType(WorkItemType... workItemType);

   IAtsQuery andOriginator(AtsUser atsUser);

   IAtsQuery andSubscribed(AtsUser atsUser);

   IAtsQuery andFavorite(AtsUser atsUser);

   IAtsQuery andTeam(List<Long> teamDefIds);

   IAtsQuery andActionableItem(List<Long> aiIds);

   IAtsQuery andVersion(Long versionId);

   IAtsQuery andState(String stateName);

   IAtsQuery andProgram(Long programId);

   IAtsQuery andInsertion(Long insertionId);

   IAtsQuery andInsertionActivity(Long insertionActivityId);

   IAtsQuery andWorkPackage(Long workPackageId);

   IAtsQuery andActionableItem(IAtsActionableItem actionableItem);

   Collection<ArtifactId> getItemIds();

   IAtsQuery andAssigneeWas(AtsUser... assignees);

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

   IAtsQuery andNotExists(AttributeTypeToken attributeType);

   IAtsQuery andName(String name);

   IAtsQuery andWorkItemType(WorkItemType... workItemTypes);

   IAtsQuery andVersion(IAtsVersion version);

   IAtsQuery andName(String name, QueryOption... queryOption);

   IAtsQuery andExists(AttributeTypeToken attributeType);

   IAtsQuery andIds(Collection<ArtifactId> ids);

   IAtsQuery andChangeType(String changeType);

}