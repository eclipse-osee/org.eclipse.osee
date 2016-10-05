/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IAttributeType;

/**
 * @author Donald G. Dunne
 */
public interface IAgileService {

   IAgileTeam getAgileTeam(ArtifactId artifact);

   IAgileTeam updateAgileTeam(JaxAgileTeam team);

   IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact);

   IAgileFeatureGroup createAgileFeatureGroup(long teamUuid, String name, String guid, Long uuid);

   void deleteAgileFeatureGroup(long teamUuid);

   void deleteAgileTeam(long uuid);

   IAgileSprint getAgileSprint(ArtifactId artifact);

   IAgileSprint createAgileSprint(long teamUuid, String name, String guid, Long uuid);

   IAgileBacklog createAgileBacklog(long teamUuid, String name, String guid, Long uuid);

   IAgileBacklog getAgileBacklog(ArtifactId artifact);

   JaxAgileItem updateItem(JaxAgileItem newItem);

   IAgileBacklog getAgileBacklog(IAgileTeam team);

   Collection<IAgileFeatureGroup> getAgileFeatureGroups(List<Long> uuids);

   IAgileBacklog getBacklogForTeam(long teamUuid);

   Collection<IAgileSprint> getSprintsForTeam(long teamUuid);

   Collection<IAgileTeam> getTeams();

   IAgileTeam getAgileTeamById(long teamUuid);

   IAgileFeatureGroup createAgileFeatureGroup(JaxAgileFeatureGroup newFeatureGroup);

   IAgileBacklog updateAgileBacklog(JaxAgileBacklog backlog);

   IAgileTeam getAgileTeam(long uuid);

   IAgileBacklog getAgileBacklog(long uuid);

   Collection<IAgileFeatureGroup> getAgileFeatureGroups(IAgileTeam team);

   Collection<IAgileSprint> getAgileSprints(IAgileTeam team);

   Collection<IAgileItem> getItems(IAgileBacklog backlog);

   Collection<IAgileFeatureGroup> getFeatureGroups(IAgileItem aItem);

   IAgileSprint getSprint(IAgileItem item);

   void deleteSprint(long sprintUuid);

   IAgileTeam createAgileTeam(JaxNewAgileTeam newTeam);

   IAttributeType getAgileTeamPointsAttributeType(IAgileTeam team);

   Collection<IAgileItem> getItems(IAgileSprint sprint);

   IAgileTeam getAgileTeam(IAgileItem item);

}
