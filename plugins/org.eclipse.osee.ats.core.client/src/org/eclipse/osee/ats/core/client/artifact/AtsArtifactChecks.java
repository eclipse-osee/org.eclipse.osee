/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCheck;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Check for certain conditions that must be met to delete an ATS object or User artifact.
 *
 * @author Donald G. Dunne
 */
public class AtsArtifactChecks extends ArtifactCheck {

   private static final List<Long> Admin_Only_Relation_Type_Ids = Arrays.asList(
      AtsRelationTypes.TeamWorkflowToReview_Review.getId(), AtsRelationTypes.ActionToWorkflow_Action.getId());
   private static boolean deletionChecksEnabled = !AtsUtilCore.isInTest();

   @Override
   public IStatus isDeleteableRelation(Artifact artifact, IRelationType relationType) {
      if (deletionChecksEnabled) {
         boolean isAtsAdmin = AtsClientService.get().getUserService().isAtsAdmin();
         if (!isAtsAdmin && Admin_Only_Relation_Type_Ids.contains(relationType.getId())) {
            return createStatus(
               String.format("Deletion of relation type [%s] off artifact [%s] is only permitted by ATS Admin",
                  relationType, artifact));
         }
      }
      return Status.OK_STATUS;
   }

   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts) {
      boolean isAtsAdmin = AtsClientService.get().getUserService().isAtsAdmin();

      IStatus result = Status.OK_STATUS;

      if (deletionChecksEnabled) {
         Set<Artifact> allArtifacts = getAllArtifacts(artifacts, new HashSet<>());

         if (result.isOK()) {
            result = checkActionableItems(isAtsAdmin, allArtifacts);
         }

         if (result.isOK()) {
            result = checkTeamDefinitions(isAtsAdmin, allArtifacts);
         }

         if (result.isOK()) {
            result = checkAtsWorkDefinitions(isAtsAdmin, allArtifacts);
         }

         if (result.isOK()) {
            result = checkUsers(allArtifacts);
         }

         if (result.isOK()) {
            result = checkActions(isAtsAdmin, allArtifacts);
         }

         if (result.isOK()) {
            result = checkWorkPackages(isAtsAdmin, allArtifacts);
         }
      }

      return result;
   }

   // Get all artifacts and recurse down default hierarchy
   private Set<Artifact> getAllArtifacts(Collection<Artifact> artifacts, Set<Artifact> allArtifacts) {
      for (Artifact art : artifacts) {
         if (art.isOnBranch(AtsClientService.get().getAtsBranch())) {
            allArtifacts.addAll(art.getDescendants());
         }
      }
      return allArtifacts;
   }

   private IStatus checkActions(boolean isAtsAdmin, Collection<Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (!isAtsAdmin && isWorkflowOrAction(art) && !isTask(art)) {
            return createStatus(String.format("Deletion of [%s] is only permitted by ATS Admin; %s invalid",
               art.getArtifactTypeName(), art.toStringWithId()));
         }
         String error = isWorkflowOrActionPermittedByAnyone(art, artifacts);
         if (Strings.isValid(error)) {
            return createStatus(String.format("Deletion of artifact type [%s] object %s is not permitted. Error: [%s]",
               art.getArtifactTypeName(), art.toStringWithId(), error));
         }
      }
      return Status.OK_STATUS;
   }

   private String isWorkflowOrActionPermittedByAnyone(Artifact art, Collection<Artifact> allArtifacts) {
      if (art.isOfType(AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow teamWf : AtsClientService.get().getWorkItemService().getTeams(art)) {
            if (!allArtifacts.contains(teamWf)) {
               return String.format("Can't delete action %s without deleting workflow %s, use ATS World Editor",
                  art.toStringWithId(), teamWf.toStringWithId());
            }
         }
      }
      if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         IAtsTeamWorkflow teamWf = AtsClientService.get().getWorkItemFactory().getTeamWf(art);
         if (!allArtifacts.contains(teamWf.getParentAction())) {
            return String.format("Can't delete workflow %s without deleting action %s, use ATS World Editor",
               teamWf.toStringWithId(), teamWf.getParentAction().toStringWithId());
         }
      }
      return null;
   }

   private boolean isWorkflowOrAction(Artifact art) {
      return art instanceof AbstractWorkflowArtifact || art instanceof IAtsAction;
   }

   private boolean isTask(Artifact art) {
      return art instanceof IAtsTask;
   }

   private IStatus createStatus(String message) {
      return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
   }

   private IStatus checkActionableItems(boolean isAtsAdmin, Collection<Artifact> artifacts) {
      Set<ArtifactId> aiIds = getActionableItemIdsWithRecurse(new HashSet<>(), artifacts);
      if (!aiIds.isEmpty()) {
         List<Artifact> teamWfsRelatedToAis =
            ArtifactQuery.getArtifactListFromTypeAndAttribute(AtsArtifactTypes.TeamWorkflow,
               AtsAttributeTypes.ActionableItemReference, aiIds, AtsClientService.get().getAtsBranch());
         if (!teamWfsRelatedToAis.isEmpty()) {
            return createStatus(String.format(
               "Actionable Items (or children AIs) [%s] selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.",
               aiIds));
         }
         if (!isAtsAdmin) {
            return createStatus("Deletion of Actionable Items is only permitted by ATS Admin.");
         }
      }
      return Status.OK_STATUS;
   }

   private Set<ArtifactId> getActionableItemIdsWithRecurse(HashSet<ArtifactId> aiIds, Collection<Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem ai = AtsClientService.get().getCache().getAtsObject(art.getId());
            if (ai != null) {
               aiIds.add(ArtifactId.valueOf(ai));
               Collection<Artifact> childArts = art.getChildren();
               if (!ai.getChildrenActionableItems().isEmpty()) {
                  getActionableItemIdsWithRecurse(aiIds, childArts);
               }
            }
         }
      }
      return aiIds;
   }

   private IStatus checkTeamDefinitions(boolean isAtsAdmin, Collection<Artifact> artifacts) {
      List<String> ids = new ArrayList<>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.TeamDefinition)) {
            ids.add(art.getIdString());
         }
      }
      if (!ids.isEmpty()) {
         List<Artifact> artifactListFromIds = ArtifactQuery.getArtifactListFromAttributeValues(
            AtsAttributeTypes.TeamDefinitionReference, ids, AtsClientService.get().getAtsBranch(), 5);
         if (artifactListFromIds.size() > 0) {
            return createStatus(String.format(
               "Team Definition (or children Team Definitions) [%s] selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.",
               ids));
         }
         if (!isAtsAdmin) {
            return createStatus("Deletion of Team Definitions is only permitted by ATS Admin.");
         }
      }
      return Status.OK_STATUS;
   }

   private IStatus checkWorkPackages(boolean isAtsAdmin, Collection<Artifact> artifacts) {
      List<ArtifactId> ids = new ArrayList<>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.WorkPackage)) {
            ids.add(art);
         }
      }
      if (!ids.isEmpty()) {
         List<Artifact> artifactListFromIds = ArtifactQuery.getArtifactListFromAttributeValues(
            AtsAttributeTypes.WorkPackageReference, ids, AtsClientService.get().getAtsBranch());
         if (artifactListFromIds.size() > 0) {
            return createStatus(String.format(
               "Work Packages [%s] selected to delete have related Work Items; Delete or re-assign Work Packages first.",
               ids));
         }
      }
      return Status.OK_STATUS;
   }

   private IStatus checkAtsWorkDefinitions(boolean isAtsAdmin, Collection<Artifact> artifacts) {
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.WorkDefinition)) {
            List<Artifact> artifactListFromTypeAndAttribute =
               ArtifactQuery.getArtifactListFromTypeAndAttribute(AtsArtifactTypes.WorkDefinition,
                  AtsAttributeTypes.WorkflowDefinition, art.getName(), AtsClientService.get().getAtsBranch());
            if (artifactListFromTypeAndAttribute.size() > 0) {
               return createStatus(String.format(
                  "ATS WorkDefinition [%s] selected to delete has ats.WorkDefinition attributes set to it's name in %d artifact.  These must be changed first.",
                  art, artifactListFromTypeAndAttribute.size()));
            }
            if (!isAtsAdmin) {
               return createStatus("Deletion of Work Definitions is only permitted by ATS Admin.");
            }
         }
      }
      return Status.OK_STATUS;
   }

   private IStatus checkUsers(Collection<Artifact> artifacts) {
      Set<User> users = new HashSet<>();
      for (Artifact art : artifacts) {
         if (art instanceof User) {
            users.add((User) art);
         }
      }
      for (User user : users) {
         UserRelatedToAtsObjectSearch srch = new UserRelatedToAtsObjectSearch(
            AtsClientService.get().getUserServiceClient().getUserFromOseeUser(user), false);
         if (srch.getResults().size() > 0) {
            return createStatus(String.format(
               "User name: \"%s\" userId: \"%s\" selected to delete has related ATS Objects; Un-relate to ATS first before deleting.",
               user.getName(), user.getUserId()));
         }
      }
      return Status.OK_STATUS;
   }

   public static void setDeletionChecksEnabled(boolean deletionChecksEnabled) {
      AtsArtifactChecks.deletionChecksEnabled = deletionChecksEnabled;
   }
}