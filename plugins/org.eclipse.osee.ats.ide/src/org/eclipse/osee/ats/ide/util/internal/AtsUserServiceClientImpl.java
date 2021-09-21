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

package org.eclipse.osee.ats.ide.util.internal;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G Dunne
 */
public class AtsUserServiceClientImpl extends AbstractAtsUserService {

   Boolean atsAdmin = null;
   private AtsUser currentUser;

   public AtsUserServiceClientImpl() {
      // For OSGI Instantiation
   }

   // for ReviewOsgiXml public void setConfigurationService(IAtsConfigurationsService configurationService)

   @Override
   public void setCurrentUser(AtsUser currentUser) {
      this.currentUser = currentUser;
   }

   @Override
   public void clearCaches() {
      currentUser = null;
      atsAdmin = null;
   }

   @Override
   public AtsUser getCurrentUser() {
      if (currentUser == null) {
         currentUser = configurationService.getUserByLoginId(System.getProperty("user.name"));
      }
      return currentUser;
   }

   @Override
   public AtsUser getCurrentUserNoCache() {
      clearCaches();
      return getCurrentUser();
   }

   @Override
   public AtsUser getUserById(ArtifactId userId) {
      return configurationService.getConfigurations().getIdToUser().get(userId.getId());
   }

   @Override
   public String getCurrentUserId() {
      return UserManager.getUser().getUserId();
   }

   @Override
   public boolean isAtsAdmin(boolean useCache) {
      if (!useCache) {
         getCurrentUser().getUserGroups().contains(AtsUserGroups.AtsAdmin);
      }
      return isAtsAdmin();
   }

   @Override
   public boolean isAtsAdmin() {
      if (atsAdmin == null) {
         atsAdmin = getCurrentUser().getUserGroups().contains(AtsUserGroups.AtsAdmin);
      }
      return atsAdmin;
   }

   @Override
   public Collection<AtsUser> getUsers() {
      return configurationService.getConfigurations().getUsers();
   }

}
