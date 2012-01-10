/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionManagerCore {

   public static Collection<TeamDefinitionArtifact> getImpactedTeamDefs(Collection<ActionableItemArtifact> aias) throws OseeCoreException {
      Set<TeamDefinitionArtifact> resultTeams = new HashSet<TeamDefinitionArtifact>();
      for (ActionableItemArtifact aia : aias) {
         resultTeams.addAll(getImpactedTeamDefInherited(aia));
      }
      return resultTeams;
   }

   private static List<TeamDefinitionArtifact> getImpactedTeamDefInherited(ActionableItemArtifact aia) throws OseeCoreException {
      if (aia.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team).size() > 0) {
         return aia.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team, TeamDefinitionArtifact.class);
      }
      Artifact parentArt = aia.getParent();
      if (parentArt instanceof ActionableItemArtifact) {
         return getImpactedTeamDefInherited((ActionableItemArtifact) parentArt);
      }
      return Collections.<TeamDefinitionArtifact> emptyList();
   }

}
