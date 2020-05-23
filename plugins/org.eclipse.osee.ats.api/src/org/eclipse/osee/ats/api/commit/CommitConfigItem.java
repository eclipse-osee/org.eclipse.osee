/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.commit;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public class CommitConfigItem implements Named {

   private IAtsTeamDefinition teamDef = null;
   private final AtsApi atsApi;
   private IAtsVersion version = null;

   public CommitConfigItem(IAtsTeamDefinition teamDef, AtsApi atsApi) {
      this.teamDef = teamDef;
      this.atsApi = atsApi;
   }

   public CommitConfigItem(IAtsVersion version, AtsApi atsApi) {
      this.version = version;
      this.atsApi = atsApi;
   }

   public BranchId getBaselineBranchId() {
      if (teamDef == null) {
         return version.getBaselineBranch();
      } else {
         return atsApi.getTeamDefinitionService().getBaselineBranchId(teamDef);
      }
   }

   public Result isAllowCommitBranchInherited() {
      if (teamDef == null) {
         return atsApi.getVersionService().isAllowCommitBranchInherited(version);
      } else {
         return atsApi.getTeamDefinitionService().isAllowCommitBranchInherited(teamDef);
      }
   }

   public Result isAllowCreateBranchInherited() {
      if (teamDef == null) {
         return atsApi.getVersionService().isAllowCreateBranchInherited(version);
      } else {
         return atsApi.getTeamDefinitionService().isAllowCreateBranchInherited(teamDef);
      }
   }

   public IAtsConfigObject getConfigObject() {
      if (teamDef == null) {
         return version;
      } else {
         return teamDef;
      }
   }

   public String getCommitFullDisplayName() {
      if (teamDef == null) {
         return version.getName();
      } else {
         return teamDef.getName();
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((teamDef == null) ? 0 : teamDef.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      CommitConfigItem other = (CommitConfigItem) obj;
      if (teamDef == null) {
         if (other.teamDef != null) {
            return false;
         }
      } else if (!teamDef.equals(other.teamDef)) {
         return false;
      }
      if (version == null) {
         if (other.version != null) {
            return false;
         }
      } else if (!version.equals(other.version)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      String teamOrVersion = (version == null ? "Team Def" : "Version");
      String name = (version == null ? teamDef.getName() : version.getName());
      return String.format("%s - %s", teamOrVersion, name);
   }

}
