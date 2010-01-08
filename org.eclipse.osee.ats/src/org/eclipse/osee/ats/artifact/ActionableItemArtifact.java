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

package org.eclipse.osee.ats.artifact;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifact extends Artifact {

   public static String ARTIFACT_NAME = "Actionable Item";

   public ActionableItemArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static List<ActionableItemArtifact> getActionableItems(Active active) throws OseeCoreException {
      return Collections.castAll(AtsCacheManager.getArtifactsByActive(
            ArtifactTypeManager.getType(ActionableItemArtifact.ARTIFACT_NAME), active));
   }

   public static String getNotActionableItemError(Artifact aia) {
      return "Action can not be written against " + aia.getArtifactTypeName() + " \"" + aia + "\" (" + aia.getHumanReadableId() + ").\n\nChoose another item.";
   }

   public static List<ActionableItemArtifact> getTopLevelActionableItems(Active active) throws OseeCoreException {
      ActionableItemArtifact topAi = getTopActionableItem();
      if (topAi == null) return java.util.Collections.emptyList();
      return Collections.castAll(Artifacts.getActive(Artifacts.getChildrenOfTypeSet(topAi,
            ActionableItemArtifact.class, false), active, ActionableItemArtifact.class));
   }

   public Collection<User> getLeads() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead, User.class);
   }

   public static ActionableItemArtifact getTopActionableItem() throws OseeCoreException {
      return (ActionableItemArtifact) AtsFolderUtil.getFolder(AtsFolder.ActionableItem);
   }

   public static List<ActionableItemArtifact> getActionableItems() throws OseeCoreException {
      return Collections.castAll(AtsCacheManager.getArtifactsByActive(
            ArtifactTypeManager.getType(ActionableItemArtifact.ARTIFACT_NAME), Active.Both));
   }

   public boolean isActionable() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), false);
   }

   public static Set<ActionableItemArtifact> getActionableItems(Collection<String> actionableItemNames) throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (String actionableItemName : actionableItemNames) {
         for (Artifact artifact : AtsCacheManager.getArtifactsByName(
               ArtifactTypeManager.getType(ActionableItemArtifact.ARTIFACT_NAME), actionableItemName)) {
            aias.add((ActionableItemArtifact) artifact);
         }
      }
      return aias;
   }

   public static Collection<TeamDefinitionArtifact> getImpactedTeamDefs(Collection<ActionableItemArtifact> aias) throws OseeCoreException {
      return TeamDefinitionArtifact.getImpactedTeamDefs(aias);
   }

   public Collection<TeamDefinitionArtifact> getImpactedTeamDefs() throws OseeCoreException {
      return TeamDefinitionArtifact.getImpactedTeamDefs(Arrays.asList(this));
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(ActionableItemArtifact aia) throws OseeCoreException {
      return TeamDefinitionArtifact.getTeamsFromItemAndChildren(aia);
   }

   public static Set<ActionableItemArtifact> getActionableItemsFromItemAndChildren(ActionableItemArtifact aia) throws OseeCoreException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      getActionableItemsFromItemAndChildren(aia, aias);
      return aias;
   }

   public static void getActionableItemsFromItemAndChildren(ActionableItemArtifact aia, Set<ActionableItemArtifact> aiaTeams) throws OseeCoreException {
      for (Artifact art : aia.getChildren()) {
         if (art instanceof ActionableItemArtifact) {
            aiaTeams.add((ActionableItemArtifact) art);
            for (Artifact childArt : aia.getChildren()) {
               if (childArt instanceof ActionableItemArtifact) getActionableItemsFromItemAndChildren(
                     (ActionableItemArtifact) childArt, aiaTeams);
            }
         }
      }
   }

}
