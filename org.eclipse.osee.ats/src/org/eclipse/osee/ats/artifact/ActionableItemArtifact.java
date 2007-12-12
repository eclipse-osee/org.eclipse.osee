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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ActiveArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactStaticIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactStaticIdSearch.SearchOperator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifact extends BasicArtifact {

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
   public ActionableItemArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   public static Set<ActionableItemArtifact> getActionableItems(Active active) throws SQLException {
      ActiveArtifactTypeSearch search =
            new ActiveArtifactTypeSearch(ARTIFACT_NAME, active, BranchPersistenceManager.getInstance().getAtsBranch());
      return search.getArtifacts(ActionableItemArtifact.class);
   }

   public static Set<ActionableItemArtifact> getTopLevelActionableItems(Active active) throws SQLException {
      ActionableItemArtifact topAi = getTopActionableItem();
      if (topAi == null) return EMPTY_SET;
      return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(topAi, ActionableItemArtifact.class, false), active,
            ActionableItemArtifact.class);
   }

   public Collection<User> getLeads() throws SQLException {
      return getArtifacts(RelationSide.TeamLead_Lead, User.class);
   }

   public static ActionableItemArtifact getTopActionableItem() throws SQLException {
      return ArtifactStaticIdSearch.getSingletonArtifactOrException(ActionableItemArtifact.ARTIFACT_NAME,
            TOP_AI_STATIC_ID, BranchPersistenceManager.getInstance().getAtsBranch(), SearchOperator.EQUAL,
            ActionableItemArtifact.class);
   }

   public static Set<ActionableItemArtifact> getActionableItems() throws SQLException {
      ActiveArtifactTypeSearch search =
            new ActiveArtifactTypeSearch(ARTIFACT_NAME, Active.Active,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      return search.getArtifacts(ActionableItemArtifact.class);
   }

   public static Set<ActionableItemArtifact> getActionableItems(Collection<String> aiaStrs) throws SQLException {
      Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
      for (String aia : aiaStrs) {
         aias.add(getSoleActionableItem(aia));
      }
      return aias;
   }

   /**
    * Refrain from using this method as Actionable Items names can be changed by the user.
    * 
    * @param name
    * @return
    * @throws SQLException
    */
   public static ActionableItemArtifact getSoleActionableItem(String name) throws SQLException {
      ArtifactTypeNameSearch search =
            new ArtifactTypeNameSearch(ARTIFACT_NAME, name, BranchPersistenceManager.getInstance().getAtsBranch());
      return search.getSingletonArtifactOrException(ActionableItemArtifact.class);
   }

   public static Set<TeamDefinitionArtifact> getImpactedTeamDefs(Set<ActionableItemArtifact> aias) throws SQLException {
      return TeamDefinitionArtifact.getImpactedTeamDefs(aias);
   }

   public static Set<TeamDefinitionArtifact> getImpactedTeamDef(ActionableItemArtifact aia) throws SQLException {
      return TeamDefinitionArtifact.getImpactedTeamDef(aia);
   }

   public Set<TeamDefinitionArtifact> getImpactedTeamDefs() throws SQLException {
      return TeamDefinitionArtifact.getImpactedTeamDef(this);
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
