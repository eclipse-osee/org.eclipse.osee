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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IDefaultInitialBranchesProvider;

/**
 * @author Robert A. Fisher
 */
public class TeamBasedDefaultBranchProvider implements IDefaultInitialBranchesProvider {

   @Override
   public Collection<Branch> getDefaultInitialBranches() throws OseeCoreException {
      IAtsUser user = AtsClientService.get().getUserService().getCurrentUser();
      try {
         Collection<IAtsTeamDefinition> teams = new ArrayList<IAtsTeamDefinition>();
         for (Artifact art : AtsClientService.get().getUserServiceClient().getOseeUser(user).getRelatedArtifacts(
            AtsRelationTypes.TeamMember_Team)) {
            teams.add(AtsClientService.get().getConfig().getSoleByGuid(art.getGuid(), IAtsTeamDefinition.class));
         }
         Collection<Branch> branches = new LinkedList<Branch>();

         Branch branch;
         for (IAtsTeamDefinition team : teams) {
            branch = BranchManager.getBranchByUuid(team.getTeamBranchUuid());
            if (branch != null) {
               branches.add(branch);
            }
         }

         return branches;
      } catch (Exception ex) {
         OseeLog.log(TeamBasedDefaultBranchProvider.class, Level.WARNING, ex);
      }

      return Collections.emptyList();
   }
}
