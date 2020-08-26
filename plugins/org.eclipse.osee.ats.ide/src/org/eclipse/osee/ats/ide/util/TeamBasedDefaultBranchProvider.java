/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.IDefaultInitialBranchesProvider;

/**
 * @author Robert A. Fisher
 */
public class TeamBasedDefaultBranchProvider implements IDefaultInitialBranchesProvider {

   @Override
   public Collection<BranchId> getDefaultInitialBranches() {
      AtsUser user = AtsApiService.get().getUserService().getCurrentUser();
      try {
         Collection<IAtsTeamDefinition> teams = new ArrayList<>();
         for (ArtifactToken art : AtsApiService.get().getRelationResolver().getRelated((IAtsObject) user,
            AtsRelationTypes.TeamMember_Team)) {
            teams.add(AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(art));
         }

         Collection<BranchId> branches = new LinkedList<>();
         for (IAtsTeamDefinition teamDef : teams) {
            branches.add(AtsApiService.get().getTeamDefinitionService().getTeamBranchId(teamDef));
         }

         return branches;
      } catch (Exception ex) {
         OseeLog.log(TeamBasedDefaultBranchProvider.class, Level.WARNING, ex);
      }

      return Collections.emptyList();
   }
}
