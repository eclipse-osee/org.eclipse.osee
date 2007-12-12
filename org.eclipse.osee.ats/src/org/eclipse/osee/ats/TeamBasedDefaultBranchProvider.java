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
package org.eclipse.osee.ats;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.IDefaultInitialBranchesProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Robert A. Fisher
 */
public class TeamBasedDefaultBranchProvider implements IDefaultInitialBranchesProvider {

   public Collection<Branch> getDefaultInitialBranches() {
      User user = SkynetAuthentication.getInstance().getAuthenticatedUser();
      try {
         Collection<TeamDefinitionArtifact> teams =
               user.getArtifacts(RelationSide.TeamMember_Team, TeamDefinitionArtifact.class);
         Collection<Branch> branches = new LinkedList<Branch>();

         Branch branch;
         for (TeamDefinitionArtifact team : teams) {
            branch = team.getTeamBranch();
            if (branch != null) {
               branches.add(branch);
            }
         }

         return branches;
      } catch (SQLException ex) {
         OSEELog.logException(TeamBasedDefaultBranchProvider.class, ex, true);
      }

      return null;
   }

}
