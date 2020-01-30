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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class CommitConfigItem implements ICommitConfigItem {

   private final IAtsTeamDefinition teamDef;
   private final AtsApi atsApi;

   public CommitConfigItem(IAtsTeamDefinition teamDef, AtsApi atsApi) {
      this.teamDef = teamDef;
      this.atsApi = atsApi;
   }

   @Override
   public BranchId getBaselineBranchId() {
      return atsApi.getTeamDefinitionService().getBaselineBranchId(teamDef);
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      return atsApi.getTeamDefinitionService().isAllowCommitBranchInherited(teamDef);
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      return atsApi.getTeamDefinitionService().isAllowCreateBranchInherited(teamDef);
   }

   @Override
   public IAtsConfigObject getConfigObject() {
      return teamDef;
   }

   @Override
   public String getCommitFullDisplayName() {
      if (teamDef != null) {
         return teamDef.getName();
      }
      return "unknown";
   }

}
