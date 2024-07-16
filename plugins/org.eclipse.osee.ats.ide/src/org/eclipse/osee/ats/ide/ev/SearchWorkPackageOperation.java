/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.ev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.WorkPackage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class SearchWorkPackageOperation extends AbstractOperation {

   private final Collection<IAtsTeamDefinition> teamDefs;
   private final Collection<IAtsActionableItem> ais;
   private final Active activeWorkPkgs;
   private final Set<Artifact> results = new HashSet<>();
   private final boolean includeChildrenTeamDefs;
   private final boolean includeChildrenAis;

   public SearchWorkPackageOperation(String operationName, Collection<IAtsTeamDefinition> teamDefs, boolean includeChildrenTeamDefs, Collection<IAtsActionableItem> ais, boolean includeChildrenAis, Active activeWorkPkgs) {
      super(operationName, Activator.PLUGIN_ID);
      this.includeChildrenTeamDefs = includeChildrenTeamDefs;
      this.includeChildrenAis = includeChildrenAis;
      this.teamDefs = teamDefs;
      this.ais = ais;
      this.activeWorkPkgs = activeWorkPkgs;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) {
      if (teamDefs.isEmpty() && ais.isEmpty()) {
         throw new OseeArgumentException("ERROR", "Must provide Team Definitions or Actionable Items");
      }
      checkForCancelledStatus(monitor);
      List<ArtifactId> ids = new ArrayList<>();
      addAllTeamDefIds(monitor, Collections.castAll(teamDefs), includeChildrenTeamDefs, ids);
      addAllAisIds(monitor, ais, includeChildrenAis, ids);

      for (Artifact teamOrAiArt : ArtifactQuery.getArtifactListFrom(ids, AtsApiService.get().getAtsBranch())) {
         for (Artifact workPkgArt : teamOrAiArt.getRelatedArtifacts(
            AtsRelationTypes.TeamDefinitionToWorkPackage_WorkPackage)) {
            boolean active = workPkgArt.getSoleAttributeValue(AtsAttributeTypes.Active, true);
            if (activeWorkPkgs == Active.Both || active && activeWorkPkgs == Active.Active || !active && activeWorkPkgs == Active.InActive) {
               results.add(workPkgArt);
            }
            checkForCancelledStatus(monitor);
         }
         checkForCancelledStatus(monitor);
      }
   }

   private void addAllAisIds(IProgressMonitor monitor, Collection<IAtsActionableItem> ais2, boolean includeChildrenAis2, List<ArtifactId> ids) {
      for (IAtsActionableItem ai : ais2) {
         ids.add(ai.getStoreObject());
         if (includeChildrenAis2) {
            addAllAisIds(monitor, ai.getChildrenActionableItems(), includeChildrenAis2, ids);
            checkForCancelledStatus(monitor);
         }
      }
   }

   private void addAllTeamDefIds(IProgressMonitor monitor, Collection<TeamDefinition> teamDefs2, boolean includeChildrenTeamDefs2, List<ArtifactId> ids) {
      for (IAtsTeamDefinition teamDef : teamDefs2) {
         ids.add(teamDef.getStoreObject());
         if (includeChildrenTeamDefs2) {
            addAllTeamDefIds(monitor,
               AtsApiService.get().getTeamDefinitionService().getChildrenTeamDefinitions(teamDef),
               includeChildrenTeamDefs2, ids);
            checkForCancelledStatus(monitor);
         }
      }
   }

   public Set<Artifact> getResultArtifacts() {
      return results;
   }

   public Set<IAtsWorkPackage> getResults() {
      Set<IAtsWorkPackage> resultWorkPgks = new HashSet<>();
      for (Artifact art : results) {
         resultWorkPgks.add(new WorkPackage(AtsApiService.get().getLogger(), AtsApiService.get(), art));
      }
      return resultWorkPgks;
   }

}
