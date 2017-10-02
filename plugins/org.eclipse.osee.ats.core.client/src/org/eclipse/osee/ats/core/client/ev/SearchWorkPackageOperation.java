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
package org.eclipse.osee.ats.core.client.ev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.model.WorkPackage;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

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
      Conditions.notNull(teamDefs);
      Conditions.notNull(ais);
      Conditions.notNull(activeWorkPkgs);
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
      List<String> guids = new ArrayList<>();
      addAllTeamDefGuids(monitor, teamDefs, includeChildrenTeamDefs, guids);
      addAllAisGuids(monitor, ais, includeChildrenAis, guids);

      for (Artifact teamOrAiArt : AtsArtifactQuery.getArtifactListFromIds(guids)) {
         for (Artifact workPkgArt : teamOrAiArt.getRelatedArtifacts(AtsRelationTypes.WorkPackage_WorkPackage)) {
            boolean active = workPkgArt.getSoleAttributeValue(AtsAttributeTypes.Active, true);
            if (activeWorkPkgs == Active.Both || active && activeWorkPkgs == Active.Active || !active && activeWorkPkgs == Active.InActive) {
               results.add(workPkgArt);
            }
            checkForCancelledStatus(monitor);
         }
         checkForCancelledStatus(monitor);
      }
   }

   private void addAllAisGuids(IProgressMonitor monitor, Collection<IAtsActionableItem> ais2, boolean includeChildrenAis2, List<String> guids) {
      for (IAtsActionableItem ai : ais2) {
         guids.add(AtsUtilCore.getGuid(ai));
         if (includeChildrenAis2) {
            addAllAisGuids(monitor, ai.getChildrenActionableItems(), includeChildrenAis2, guids);
            checkForCancelledStatus(monitor);
         }
      }
   }

   private void addAllTeamDefGuids(IProgressMonitor monitor, Collection<IAtsTeamDefinition> teamDefs2, boolean includeChildrenTeamDefs2, List<String> guids) {
      for (IAtsTeamDefinition teamDef : teamDefs2) {
         guids.add(AtsUtilCore.getGuid(teamDef));
         if (includeChildrenTeamDefs2) {
            addAllTeamDefGuids(monitor, teamDef.getChildrenTeamDefinitions(), includeChildrenTeamDefs2, guids);
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
         resultWorkPgks.add(
            new WorkPackage(AtsClientService.get().getLogger(), art, AtsClientService.get().getServices()));
      }
      return resultWorkPgks;
   }

}
