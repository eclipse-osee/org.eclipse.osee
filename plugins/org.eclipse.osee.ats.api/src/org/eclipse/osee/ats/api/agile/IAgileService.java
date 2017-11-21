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
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAgileService {

   public static final String SPRINT_FOLDER_NAME = "Sprints";
   public static String FEATURE_GROUP_FOLDER_NAME = "Feature Groups";
   public static long EMPTY_VALUE = -1;

   IAgileTeam getAgileTeam(ArtifactId artifact);

   IAgileTeam updateAgileTeam(JaxAgileTeam team);

   IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact);

   IAgileFeatureGroup createAgileFeatureGroup(long teamId, String name, String guid, Long id);

   void deleteAgileFeatureGroup(long teamId);

   void deleteAgileTeam(long id);

   IAgileSprint getAgileSprint(ArtifactId artifact);

   IAgileSprint getAgileSprint(long id);

   IAgileSprint createAgileSprint(long teamId, String name, String guid, Long id);

   IAgileBacklog createAgileBacklog(long teamId, String name, String guid, Long id);

   IAgileBacklog getAgileBacklog(ArtifactId artifact);

   AgileWriterResult updateAgileItem(JaxAgileItem newItem);

   IAgileBacklog getAgileBacklog(IAgileTeam team);

   Collection<IAgileFeatureGroup> getAgileFeatureGroups(List<Long> ids);

   IAgileBacklog getBacklogForTeam(long teamId);

   Collection<IAgileSprint> getSprintsForTeam(long teamId);

   Collection<IAgileTeam> getTeams();

   IAgileTeam getAgileTeamById(long teamId);

   IAgileFeatureGroup createAgileFeatureGroup(JaxAgileFeatureGroup newFeatureGroup);

   IAgileBacklog updateAgileBacklog(JaxAgileBacklog backlog);

   IAgileTeam getAgileTeam(long id);

   IAgileBacklog getAgileBacklog(long id);

   Collection<IAgileFeatureGroup> getAgileFeatureGroups(IAgileTeam team);

   Collection<IAgileSprint> getAgileSprints(IAgileTeam team);

   Collection<IAgileItem> getItems(IAgileBacklog backlog);

   Collection<IAgileFeatureGroup> getFeatureGroups(IAgileItem aItem);

   IAgileSprint getSprint(IAgileItem item);

   void deleteSprint(long sprintId);

   IAgileTeam createAgileTeam(JaxNewAgileTeam newTeam);

   AttributeTypeId getAgileTeamPointsAttributeType(IAgileTeam team);

   Collection<IAgileItem> getItems(IAgileSprint sprint);

   IAgileTeam getAgileTeam(IAgileItem item);

   ArtifactToken getRelatedBacklogArt(IAtsWorkItem workItem);

   boolean isBacklog(Object object);

   boolean isSprint(ArtifactId artifact);

   Collection<ArtifactToken> getRelatedSprints(ArtifactId artifact);

   XResultData storeSprintReports(long teamId, long sprintId);

   Collection<IAtsTeamDefinition> getAtsTeams(IAgileTeam aTeam);

   IAgileTeam getAgileTeam(IAtsTeamDefinition teamDef);

   IAgileTeam getAgileTeam(IAgileSprint sprint);

   IAgileTeam getAgileTeamByName(String agileTeamName);

   /**
    * Display Points as either "ats.Points" or "ats.Points Numeric" as configured on Agile Team artifact
    */
   String getAgileTeamPointsStr(IAtsWorkItem workItem);

   /**
    * @return agile team from team definition relation, then sprint relation, then backlog relation. Else null.
    */
   IAgileTeam getAgileTeam(IAtsWorkItem workItem);

   /**
    * @return agile team for sprint
    */
   IAgileTeam getAgileTeamFromSprint(IAgileSprint sprint);

   /**
    * @return agile team from backlog
    */
   IAgileTeam getAgileTeamFromBacklog(IAgileBacklog backlog);

   Set<IAtsUser> getTeamMebers(IAgileTeam agileTeam);

   IAgileProgram createAgileProgram(IAgileProgram agileProgram);

   IAgileProgramFeature createAgileProgramFeature(IAgileProgramBacklogItem programBacklogItem, JaxAgileProgramFeature feature);

   void setAgileStory(IAtsTeamWorkflow codeWf, IAgileStory story, IAtsChangeSet changes);

   IAgileStory createAgileStory(IAgileProgramFeature feature, JaxAgileStory story);

   IAgileProgramBacklog createAgileProgramBacklog(IAgileProgram agileProgram, JaxAgileProgramBacklog jaxProgramBacklog);

   IAgileProgramBacklogItem createAgileProgramBacklogItem(IAgileProgramBacklog programBacklog, JaxAgileProgramBacklogItem jaxProgramBacklogItem);

   IAgileProgram getAgileProgram(long programId);

   IAgileProgramFeature getAgileProgramFeature(long programFeatureId);

   IAgileProgramFeature createAgileProgramFeature(long teamId, String name, String guid, Long id);

   IAgileProgramBacklogItem getAgileProgramBacklogItem(long programBacklogItemId);

   IAgileProgramBacklog getAgileProgramBacklog(IAgileProgram program);

   ArtifactToken getAgileProgramBacklogArt(IAgileProgram program);

}
