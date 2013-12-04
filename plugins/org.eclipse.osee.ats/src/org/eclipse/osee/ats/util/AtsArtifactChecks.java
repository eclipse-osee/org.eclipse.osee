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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserRelatedToAtsObjectSearch;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException {
      String result = checkActionableItems(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      result = checkTeamDefinitions(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      result = checkAtsWorkDefinitions(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      result = checkUsers(artifacts);
      if (result != null) {
         return createStatus(result);
      }

      return OK_STATUS;
   }

   private IStatus createStatus(String message) {
      return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
   }

   private String checkActionableItems(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<IAtsActionableItem> aias = new HashSet<IAtsActionableItem>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.ActionableItem)) {
            IAtsActionableItem aia =
               AtsClientService.get().getAtsConfig().getSoleByGuid(art.getGuid(), IAtsActionableItem.class);
            if (aia != null) {
               aias.add(aia);
            }
         }
      }
      if (aias.size() > 0) {
         ActionableItemWorldSearchItem srch = new ActionableItemWorldSearchItem("AI search", aias, true, true, false);
         if (srch.performSearchGetResults(false).size() > 0) {
            return String.format(
               "Actionable Items (or children AIs) [%s] selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.",
               aias);
         }
      }
      return null;
   }

   private String checkTeamDefinitions(Collection<Artifact> artifacts) throws OseeCoreException {
      List<String> guids = new ArrayList<String>();
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.TeamDefinition)) {
            guids.add(art.getGuid());
         }
      }
      if (guids.size() > 0) {
         List<Artifact> artifactListFromIds =
            ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.TeamDefinition, guids,
               AtsUtil.getAtsBranch(), 5);
         if (artifactListFromIds.size() > 0) {
            return String.format(
               "Team Definition (or children Team Definitions) [%s] selected to delete have related Team Workflows; Delete or re-assign Team Workflows first.",
               guids);
         }
      }
      return null;
   }

   private String checkAtsWorkDefinitions(Collection<Artifact> artifacts) throws OseeCoreException {
      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.WorkDefinition)) {
            List<Artifact> artifactListFromTypeAndAttribute =
               ArtifactQuery.getArtifactListFromTypeAndAttribute(AtsArtifactTypes.WorkDefinition,
                  AtsAttributeTypes.WorkflowDefinition, art.getName(), AtsUtilCore.getAtsBranchToken());
            if (artifactListFromTypeAndAttribute.size() > 0) {
               return String.format(
                  "ATS WorkDefinition [%s] selected to delete has ats.WorkDefinition attributes set to it's name in %d artifact.  These must be changed first.",
                  art, artifactListFromTypeAndAttribute.size());
            }
         }
      }
      return null;
   }

   private String checkUsers(Collection<Artifact> artifacts) throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      for (Artifact art : artifacts) {
         if (art instanceof User) {
            users.add((User) art);
         }
      }
      for (User user : users) {
         UserRelatedToAtsObjectSearch srch =
            new UserRelatedToAtsObjectSearch("User search", AtsClientService.get().getUserAdmin().getUserFromOseeUser(
               user), false, LoadView.None);
         if (srch.performSearchGetResults().size() > 0) {
            return String.format(
               "User name: \"%s\" userId: \"%s\" selected to delete has related ATS Objects; Un-relate to ATS first before deleting.",
               user.getName(), user.getUserId());
         }
      }
      return null;
   }
}