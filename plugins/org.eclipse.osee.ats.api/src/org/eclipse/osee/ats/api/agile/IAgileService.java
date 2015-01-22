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

/**
 * @author Donald G. Dunne
 */
public interface IAgileService {

   IAgileTeam getAgileTeam(Object artifact);

   IAgileTeam createUpdateAgileTeam(JaxAgileTeam team);

   IAgileFeatureGroup getAgileFeatureGroup(Object artifact);

   IAgileFeatureGroup createAgileFeatureGroup(long teamUuid, String name, String guid);

   void deleteAgileFeatureGroup(long teamUuid);

   void deleteAgileTeam(long uuid);

   IAgileSprint getAgileSprint(Object artifact);

   IAgileSprint createAgileSprint(long teamUuid, String name, String guid);

   IAgileBacklog createAgileBacklog(long teamUuid, String name, String guid);

   IAgileBacklog getAgileBacklog(Object artifact);

   JaxAgileItem updateItem(JaxAgileItem newItem);

   Collection<IAgileFeatureGroup> getAgileFeatureGroups(List<Long> uuids);

   IAgileBacklog getBacklogForTeam(long teamUuid);

   Collection<IAgileSprint> getSprintsForTeam(long teamUuid);

   Collection<IAgileTeam> getTeams();

   IAgileTeam getAgileTeamById(long teamUuid);

   IAgileFeatureGroup createAgileFeatureGroup(JaxAgileFeatureGroup newFeatureGroup);

}
