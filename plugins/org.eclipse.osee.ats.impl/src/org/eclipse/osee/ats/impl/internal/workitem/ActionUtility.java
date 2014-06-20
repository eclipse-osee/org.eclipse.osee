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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class ActionUtility {

   private final OrcsApi orcsApi;

   public ActionUtility(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void initializeNewStateMachine(IAtsUser createdBy, IAtsTeamDefinition teamDef, Date createdDate, String workDefinitionName, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
      ArtifactReadable workDef = getWorkDefinition(orcsApi, workDefinitionName);
      String startState = getStartState(workDef);
      String assignees = getAssigneesStorageString(orcsApi, teamDef);
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CurrentState,
         String.format("%s;%s;;", startState, assignees));

      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CurrentStateType, StateType.Working.name());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedBy, createdBy.getUserId());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedDate, createdDate);
      String log = getLog(createdBy, startState, createdDate);
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.Log, log);
   }

   private String getAssigneesStorageString(OrcsApi orcsApi, IAtsTeamDefinition teamDef) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      for (ArtifactReadable lead : ((ArtifactReadable) teamDef.getStoreObject()).getRelated(AtsRelationTypes.TeamLead_Lead)) {
         sb.append("<");
         sb.append(lead.getSoleAttributeAsString(CoreAttributeTypes.UserId));
         sb.append(">");
      }
      return sb.toString();
   }

   private String getStartState(ArtifactReadable workDef) throws OseeCoreException {
      String workDefContents = workDef.getSoleAttributeAsString(AtsAttributeTypes.DslSheet);
      Matcher m = Pattern.compile("startState \"(.*)\"").matcher(workDefContents);
      if (m.find()) {
         return m.group(1);
      }
      return null;
   }

   private ArtifactReadable getWorkDefinition(OrcsApi orcsApi, String workDefinitionName) throws OseeCoreException {
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andIsOfType(
         AtsArtifactTypes.WorkDefinition).andNameEquals(workDefinitionName).getResults().getExactlyOne();
   }

   public String getWorkDefinitionName(IAtsTeamDefinition teamDef) throws OseeCoreException {
      return getWorkDefinitionName((ArtifactReadable) teamDef.getStoreObject());
   }

   private String getWorkDefinitionName(ArtifactReadable teamDefArt) throws OseeCoreException {
      String workDefName = teamDefArt.getSoleAttributeAsString(AtsAttributeTypes.WorkflowDefinition, null);
      if (Strings.isValid(workDefName)) {
         return workDefName;
      }

      ArtifactReadable parentTeamDef =
         teamDefArt.getRelated(CoreRelationTypes.Default_Hierarchical__Parent).getExactlyOne();
      if (parentTeamDef == null) {
         return "WorkDef_Team_Default";
      }
      return getWorkDefinitionName(parentTeamDef);
   }

   private String getLog(IAtsUser currentUser, String startState, Date createdDate) throws OseeCoreException {
      String log =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsLog><Item date=\"INSERT_DATE\" msg=\"\" state=\"\" type=\"Originated\" userId=\"INSERT_USER_NAME\"/><Item date=\"INSERT_DATE\" msg=\"\" state=\"INSERT_STATE_NAME\" type=\"StateEntered\" userId=\"INSERT_USER_NAME\"/></AtsLog>";
      log = log.replaceAll("INSERT_DATE", String.valueOf(createdDate.getTime()));
      log = log.replaceAll("INSERT_USER_NAME", currentUser.getUserId());
      log = log.replaceAll("INSERT_STATE_NAME", startState);
      return log;
   }

}
