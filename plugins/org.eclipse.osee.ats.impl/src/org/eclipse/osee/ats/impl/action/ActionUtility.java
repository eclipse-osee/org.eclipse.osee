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
package org.eclipse.osee.ats.impl.action;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.impl.internal.AtsServerImpl;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

public class ActionUtility {

   public static enum ActionLoadLevel {
      HEADER,
      HEADER_FULL,
      STATE
   }

   public static ArtifactId createAction(OrcsApi orcsApi, String title, String description, String actionableItemName, String changeType, String priority, String asUserId) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(title, "Title");
      Conditions.checkNotNullOrEmpty(description, "Description");
      Conditions.checkNotNullOrEmpty(actionableItemName, "Actionable Item");
      Conditions.checkNotNullOrEmpty(changeType, "Change Type");
      Conditions.checkNotNullOrEmpty(priority, "Priority");
      Conditions.checkNotNullOrEmpty(asUserId, "UserId");
      IAtsUser user = AtsCore.getUserService().getUserById(asUserId);
      if (user == null) {
         throw new OseeStateException("User by id [%s] does not exist", asUserId);
      }
      ArtifactReadable ai = getActionableItem(orcsApi, actionableItemName);
      ArtifactReadable teamDef = getTeamDefinition(orcsApi, ai);
      String workDefinitionName = getWorkDefinitionName(orcsApi, teamDef);
      if (!Strings.isValid(workDefinitionName)) {
         throw new OseeStateException("Work Definition for Team Def [%s] does not exist", teamDef);
      }

      TransactionFactory txFactory = orcsApi.getTransactionFactory(null);
      TransactionBuilder tx =
         txFactory.createTransaction(COMMON, (ArtifactReadable) user.getStoreObject(), "Create ATS Action");
      ArtifactId action = tx.createArtifact(AtsArtifactTypes.Action, title);
      tx.setSoleAttributeFromString(action, AtsAttributeTypes.Description, description);
      tx.setSoleAttributeFromString(action, AtsAttributeTypes.ChangeType, changeType);
      tx.setSoleAttributeFromString(action, AtsAttributeTypes.PriorityType, priority);

      ArtifactId teamWf = tx.createArtifact(getTeamWfArtifactType(null), title);
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Description, description);
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ActionableItem, ai.getGuid());
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ChangeType, changeType);
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.PriorityType, priority);
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.TeamDefinition, teamDef.getGuid());

      ArtifactReadable workDef = getWorkDefinition(orcsApi, workDefinitionName);
      String startState = getStartState(workDef);
      String assignees = getAssigneesStorageString(orcsApi, teamDef);
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.CurrentState,
         String.format("%s;%s;;", startState, assignees));

      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.CurrentStateType, StateType.Working.name());
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.CreatedBy, user.getUserId());
      Date createdDate = new Date();
      tx.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedDate, createdDate);
      String log = getLog(user, startState, createdDate);
      tx.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Log, log);

      // Add relation between Action and TeamWf
      tx.relate(action, AtsRelationTypes.ActionToWorkflow_WorkFlow, teamWf);

      tx.commit();

      return teamWf;
   }

   private static String getAssigneesStorageString(OrcsApi orcsApi, ArtifactReadable teamDef) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      for (ArtifactReadable lead : teamDef.getRelated(AtsRelationTypes.TeamLead_Lead)) {
         sb.append("<");
         sb.append(lead.getSoleAttributeAsString(CoreAttributeTypes.UserId));
         sb.append(">");
      }
      return sb.toString();
   }

   private static String getStartState(ArtifactReadable workDef) throws OseeCoreException {
      String workDefContents = workDef.getSoleAttributeAsString(AtsAttributeTypes.DslSheet);
      Matcher m = Pattern.compile("startState \"(.*)\"").matcher(workDefContents);
      if (m.find()) {
         return m.group(1);
      }
      return null;
   }

   // TODO Need to check extensions to get correct type
   private static IArtifactType getTeamWfArtifactType(ArtifactReadable teamDef) {
      return AtsArtifactTypes.TeamWorkflow;
   }

   public static ResultSet<ArtifactReadable> getAis(OrcsApi orcsApi) throws OseeCoreException {
      return orcsApi.getQueryFactory(null).fromBranch(COMMON).andIsOfType(AtsArtifactTypes.ActionableItem).getResults();
   }

   private static ArtifactReadable getActionableItem(OrcsApi orcsApi, String actionableItemName) throws OseeCoreException {
      ResultSet<ArtifactReadable> results =
         orcsApi.getQueryFactory(null).fromBranch(COMMON).andIsOfType(AtsArtifactTypes.ActionableItem).andNameEquals(
            actionableItemName).getResults();
      if (results.isEmpty()) {
         throw new OseeStateException("No Actionable Item found named [%s]", actionableItemName);
      }

      ArtifactReadable ai = results.getExactlyOne();
      return ai;
   }

   private static ArtifactReadable getTeamDefinition(OrcsApi orcsApi, ArtifactReadable aiArt) throws OseeCoreException {
      IAtsActionableItem ai = AtsServerImpl.get().getWorkItemFactory().getActionableItem(aiArt);
      IAtsTeamDefinition teamDef = ai.getTeamDefinitionInherited();
      if (teamDef == null) {
         throw new OseeStateException("No related Team Definition for AI [%s]", ai);
      }
      return AtsServerImpl.get().getArtifact(teamDef);
   }

   private static ArtifactReadable getWorkDefinition(OrcsApi orcsApi, String workDefinitionName) throws OseeCoreException {
      return orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIsOfType(AtsArtifactTypes.WorkDefinition).andNameEquals(
         workDefinitionName).getResults().getExactlyOne();
   }

   private static String getWorkDefinitionName(OrcsApi orcsApi, ArtifactReadable teamDef) throws OseeCoreException {
      String workDefName = teamDef.getSoleAttributeAsString(AtsAttributeTypes.WorkflowDefinition, null);
      if (Strings.isValid(workDefName)) {
         return workDefName;
      }

      ArtifactReadable parentTeamDef =
         teamDef.getRelated(CoreRelationTypes.Default_Hierarchical__Parent).getExactlyOne();
      if (parentTeamDef == null) {
         return "WorkDef_Team_Default";
      }
      return getWorkDefinitionName(orcsApi, parentTeamDef);
   }

   private static String getLog(IAtsUser currentUser, String startState, Date createdDate) throws OseeCoreException {
      String log =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><AtsLog><Item date=\"INSERT_DATE\" msg=\"\" state=\"\" type=\"Originated\" userId=\"INSERT_USER_NAME\"/><Item date=\"INSERT_DATE\" msg=\"\" state=\"INSERT_STATE_NAME\" type=\"StateEntered\" userId=\"INSERT_USER_NAME\"/></AtsLog>";
      log = log.replaceAll("INSERT_DATE", String.valueOf(createdDate.getTime()));
      log = log.replaceAll("INSERT_USER_NAME", currentUser.getUserId());
      log = log.replaceAll("INSERT_STATE_NAME", startState);
      return log;
   }

   public static String displayAction(IResourceRegistry registry, ArtifactReadable action, String title, ActionLoadLevel actionLoadLevel) throws Exception {
      ActionPage page = new ActionPage(registry, action, title, actionLoadLevel);
      return page.generate();
   }

}
