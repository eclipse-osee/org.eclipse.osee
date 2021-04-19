/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.demo.workflow.cr;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.cr.CreateNewChangeRequestBlam;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Donald G. Dunne
 */
public class CreateNewDemoChangeRequestBlam extends CreateNewChangeRequestBlam {

   public CreateNewDemoChangeRequestBlam() {
      super("Create New Demo Change Request");
   }

   @Override
   public String getDescriptionUsage() {
      return "Create program top level Demo Change Request for any new feature or problem found.\nThis will mature into all the work for all teams needed to resolve this request.";
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return java.util.Collections.singleton(CoreUserGroups.Everyone);
   }

   @Override
   public Collection<IAtsActionableItem> getProgramCrAis() {
      IAtsActionableItem ai =
         AtsApiService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_PL_CR_AI);
      return Collections.singleton(ai);
   }

   @Override
   public String getRunText() {
      return "Create Change Request";
   }

   @Override
   public boolean isOverrideAccess() {
      return !AtsApiService.get().getStoreService().isProductionDb();
   }

}
