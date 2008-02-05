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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.FromArtifactsSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Donald G. Dunne
 */
public class LegacyPCRActionsWorldSearchItem extends WorldSearchItem {

   private boolean returnTeams = false;
   private final Collection<String> pcrIds;
   private final Collection<TeamDefinitionArtifact> teamDefs;

   public LegacyPCRActionsWorldSearchItem(String name, String pcrId, Collection<TeamDefinitionArtifact> teamDefs) {
      this(name, pcrId != null ? Arrays.asList(new String[] {pcrId}) : null, teamDefs);
   }

   public LegacyPCRActionsWorldSearchItem(String name, Collection<String> pcrIds, Collection<TeamDefinitionArtifact> teamDefs) {
      super(name);
      this.pcrIds = pcrIds;
      this.teamDefs = teamDefs;
   }

   public LegacyPCRActionsWorldSearchItem(String name, Collection<TeamDefinitionArtifact> teamDefs) {
      this(name, (String) null, teamDefs);
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws SQLException, IllegalArgumentException {
      List<ISearchPrimitive> prodCriteria = new LinkedList<ISearchPrimitive>();
      if (pcrIds != null && pcrIds.size() > 0) {
         for (String pcrId : pcrIds) {
            prodCriteria.add(new AttributeValueSearch(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), pcrId,
                  Operator.EQUAL));
         }
      } else if (pcrIds == null || pcrIds.size() == 0) {
         prodCriteria.add(new AttributeValueSearch(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName(), "%",
               Operator.LIKE));
      }
      FromArtifactsSearch prodSearch = new FromArtifactsSearch(prodCriteria, false);

      List<ISearchPrimitive> teamDefCriteria = new LinkedList<ISearchPrimitive>();
      boolean teamDefsSpecified = (teamDefs != null && teamDefs.size() > 0);
      if (teamDefsSpecified) {
         for (TeamDefinitionArtifact pcrId : teamDefs) {
            teamDefCriteria.add(new AttributeValueSearch(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName(),
                  pcrId.getGuid(), Operator.EQUAL));
         }
      }
      FromArtifactsSearch teamDefSearch = new FromArtifactsSearch(teamDefCriteria, false);

      List<ISearchPrimitive> bothCriteria = new LinkedList<ISearchPrimitive>();
      bothCriteria.add(prodSearch);
      if (teamDefsSpecified) bothCriteria.add(teamDefSearch);
      FromArtifactsSearch bothSearch = new FromArtifactsSearch(bothCriteria, true);

      if (isReturnTeams()) {
         if (cancelled) return EMPTY_SET;
         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(bothCriteria, true,
                     BranchPersistenceManager.getInstance().getAtsBranch());

         if (cancelled) return EMPTY_SET;
         return arts;
      } else {
         List<ISearchPrimitive> actionCriteria = new LinkedList<ISearchPrimitive>();
         actionCriteria.add(new InRelationSearch(bothSearch, RelationSide.ActionToWorkflow_Action));

         Collection<Artifact> arts =
               ArtifactPersistenceManager.getInstance().getArtifacts(actionCriteria, true,
                     BranchPersistenceManager.getInstance().getAtsBranch());

         if (cancelled) return EMPTY_SET;

         return arts;
      }
   }

   public boolean isReturnTeams() {
      return returnTeams;
   }

   public void setReturnTeams(boolean returnTeams) {
      this.returnTeams = returnTeams;
   }

}
