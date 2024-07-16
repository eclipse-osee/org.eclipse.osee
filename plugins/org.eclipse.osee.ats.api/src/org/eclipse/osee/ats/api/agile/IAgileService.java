/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.agile;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAgileService {

   public static final String SPRINT_FOLDER_NAME = "Sprints";
   public static String FEATURE_GROUP_FOLDER_NAME = "Feature Groups";
   public static long EMPTY_VALUE = -1;

   /**
    * @return Points Attr type defined if agile team, else default points attribute type
    */
   AttributeTypeToken getPointsAttrType(IAtsWorkItem workItem);

   IAgileTeam getAgileTeam(ArtifactId artifact);

   IAgileTeam updateAgileTeam(JaxAgileTeam team);

   IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact);

   IAgileFeatureGroup createAgileFeatureGroup(long teamId, String name, String guid, Long id);

   void deleteAgileFeatureGroup(long teamId);

   void deleteAgileTeam(long id);

   IAgileSprint getAgileSprint(ArtifactId artifact);

   IAgileSprint getAgileSprint(long id);

   IAgileSprint createAgileSprint(long teamId, String name, Long id);

   IAgileBacklog createAgileBacklog(long teamId, String name, Long id);

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

   AttributeTypeToken getAgileTeamPointsAttributeType(IAgileTeam team);

   Collection<IAgileItem> getItems(IAgileSprint sprint);

   IAgileTeam getAgileTeam(IAgileItem item);

   ArtifactToken getRelatedBacklogArt(IAtsWorkItem workItem);

   boolean isBacklog(Object object);

   boolean isSprint(ArtifactToken artifact);

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

   Set<AtsUser> getTeamMebers(IAgileTeam agileTeam);

   IAgileProgram createAgileProgram(IAgileProgram agileProgram);

   IAgileProgramFeature createAgileProgramFeature(IAgileProgramBacklogItem programBacklogItem,
      JaxAgileProgramFeature feature);

   void setAgileStory(IAtsTeamWorkflow codeWf, IAgileStory story, IAtsChangeSet changes);

   IAgileStory createAgileStory(IAgileProgramFeature feature, JaxAgileStory story);

   IAgileProgramBacklog createAgileProgramBacklog(IAgileProgram agileProgram, JaxAgileProgramBacklog jaxProgramBacklog);

   IAgileProgramBacklogItem createAgileProgramBacklogItem(IAgileProgramBacklog programBacklog,
      JaxAgileProgramBacklogItem jaxProgramBacklogItem);

   IAgileProgram getAgileProgram(long programId);

   IAgileProgramFeature getAgileProgramFeature(long programFeatureId);

   IAgileProgramFeature createAgileProgramFeature(long teamId, String name, String guid, Long id);

   IAgileProgramBacklogItem getAgileProgramBacklogItem(long programBacklogItemId);

   IAgileProgramBacklog getAgileProgramBacklog(IAgileProgram program);

   ArtifactToken getAgileProgramBacklogArt(IAgileProgram program);

   IAgileTeam getAgileTeamById(ArtifactId agileTeamId);

   IAgileFeatureGroup getAgileFeatureGroupById(ArtifactId agileFeatureGroupId);

   /**
    * @return Active team members (off Agile Team -> Users or ATS Team Def -> Member) sorted with UnAssigned at the end.
    */
   List<ArtifactToken> getTeamMembersOrdered(IAgileTeam aTeam);

   /**
    * @return Active non-team members sorted.
    */
   List<ArtifactToken> getOtherMembersOrdered(IAgileTeam aTeam);

   String getAgileFeatureGroupStr(IAtsWorkItem workItem);

   Collection<IAgileFeatureGroup> getFeatureGroups(IAtsWorkItem workItem);

   IAgileSprint getSprint(IAtsTeamWorkflow teamWf);

   void setSprint(IAtsTeamWorkflow teamWf, IAgileSprint sprint, IAtsChangeSet changes);

   String getPointsStr(IAtsWorkItem workItem);

   AttributeTypeToken getPointsAttrType(IAtsTeamDefinition teamDef);

   XResultData sortAgileBacklog(ArtifactToken backlog, String comment);

}
