/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.commit;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class CommitConfigItem implements ICommitConfigItem {

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

   @Override
   public BranchId getBaselineBranchId() {
      if (teamDef == null) {
         return version.getBaselineBranch();
      } else {
         return atsApi.getTeamDefinitionService().getBaselineBranchId(teamDef);
      }
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (teamDef == null) {
         return atsApi.getVersionService().isAllowCommitBranchInherited(version);
      } else {
         return atsApi.getTeamDefinitionService().isAllowCommitBranchInherited(teamDef);
      }
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (teamDef == null) {
         return atsApi.getVersionService().isAllowCreateBranchInherited(version);
      } else {
         return atsApi.getTeamDefinitionService().isAllowCreateBranchInherited(teamDef);
      }
   }

   @Override
   public IAtsConfigObject getConfigObject() {
      if (teamDef == null) {
         return version;
      } else {
         return teamDef;
      }
   }

   @Override
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

}
