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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifact extends Artifact {

   public static String ARTIFACT_NAME = "Actionable Item";
   public static String TOP_AI_STATIC_ID = "osee.ats.TopActionableItem";
   public static Set<ActionableItemArtifact> EMPTY_SET = new HashSet<ActionableItemArtifact>();

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public ActionableItemArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static List<ActionableItemArtifact> getActionableItems(Active active) throws OseeCoreException {
      return AtsCache.getArtifactsByActive(active, ActionableItemArtifact.class);
   }

   public static String getNotActionableItemError(ActionableItemArtifact aia) {
      return "Action can not be written against Actionable Item \"" + aia + "\" (" + aia.getHumanReadableId() + ").\n\nChoose another item.";
   }

   public static Set<ActionableItemArtifact> getTopLevelActionableItems(Active active) throws SQLException, OseeCoreException {
      ActionableItemArtifact topAi = getTopActionableItem();
      if (topAi == null) return EMPTY_SET;
      return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(topAi, ActionableItemArtifact.class, false), active,
            ActionableItemArtifact.class);
   }

   public Collection<User> getLeads() throws SQLException {
      return getArtifacts(AtsRelation.TeamLead_Lead, User.class);
   }

   public static ActionableItemArtifact getTopActionableItem() throws OseeCoreException {
      return (ActionableItemArtifact) StaticIdQuery.getSingletonArtifactOrException(
            ActionableItemArtifact.ARTIFACT_NAME, TOP_AI_STATIC_ID, BranchPersistenceManager.getAtsBranch());
   }

   public static List<ActionableItemArtifact> getActionableItems() throws OseeCoreException {
      return AtsCache.getArtifactsByActive(Active.Both, ActionableItemArtifact.class);
   }

   public boolean isActionable() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), false);
   }

   public static Set<ActionableItemArtifact> getActionableItems(Collection<String> actionableItemNames) throws OseeCoreException, SQLException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (String actionableItemName : actionableItemNames) {
         aias.addAll(AtsCache.getArtifactsByName(actionableItemName, ActionableItemArtifact.class));
      }
      return aias;
   }

   public static Collection<TeamDefinitionArtifact> getImpactedTeamDefs(Collection<ActionableItemArtifact> aias) throws OseeCoreException, SQLException {
      return TeamDefinitionArtifact.getImpactedTeamDefs(aias);
   }

   public Collection<TeamDefinitionArtifact> getImpactedTeamDefs() throws OseeCoreException, SQLException {
      return TeamDefinitionArtifact.getImpactedTeamDefs(Arrays.asList(this));
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(ActionableItemArtifact aia) throws SQLException {
      return TeamDefinitionArtifact.getTeamsFromItemAndChildren(aia);
   }

   public static Set<ActionableItemArtifact> getActionableItemsFromItemAndChildren(ActionableItemArtifact aia) throws SQLException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      getActionableItemsFromItemAndChildren(aia, aias);
      return aias;
   }

   public static void getActionableItemsFromItemAndChildren(ActionableItemArtifact aia, Set<ActionableItemArtifact> aiaTeams) throws SQLException {
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
