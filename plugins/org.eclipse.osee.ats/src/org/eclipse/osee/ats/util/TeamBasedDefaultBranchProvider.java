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
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IDefaultInitialBranchesProvider;

/**
 * @author Robert A. Fisher
 */
public class TeamBasedDefaultBranchProvider implements IDefaultInitialBranchesProvider {

   @Override
   public Collection<BranchId> getDefaultInitialBranches()  {
      IAtsUser user = AtsClientService.get().getUserService().getCurrentUser();
      try {
         Collection<IAtsTeamDefinition> teams = new ArrayList<>();
         for (Artifact art : AtsClientService.get().getUserServiceClient().getOseeUser(user).getRelatedArtifacts(
            AtsRelationTypes.TeamMember_Team)) {
            teams.add(AtsClientService.get().getCache().getAtsObject(art.getId()));
         }

         Collection<BranchId> branches = new LinkedList<>();
         for (IAtsTeamDefinition team : teams) {
            branches.add(team.getTeamBranchId());
         }

         return branches;
      } catch (Exception ex) {
         OseeLog.log(TeamBasedDefaultBranchProvider.class, Level.WARNING, ex);
      }

      return Collections.emptyList();
   }
}
