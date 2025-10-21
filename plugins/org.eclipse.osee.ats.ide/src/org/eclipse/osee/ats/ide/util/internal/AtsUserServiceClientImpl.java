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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.users.AbstractAtsUserService;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G Dunne
 */
public class AtsUserServiceClientImpl extends AbstractAtsUserService {

   Boolean atsAdmin = null;
   Boolean atsDeleteWorkflowAdmin = null;
   private AtsUser currentUser;
   private AtsApi atsApi;

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
         // Authenticate and get user.  Authenticate needs to remain when legacy userService is removed.
         int x = 0;
         UserToken userTok = ServiceUtil.getOseeClient().userService().getUser();
         while (userTok.isInvalid() && x++ < 10) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               // do nothing
            }
            userTok = ServiceUtil.getOseeClient().userService().getUser();
            if (userTok.isValid()) {
               break;
            }
         }
         if (userTok.isInvalid()) {
            throw new OseeArgumentException("getUser() can not be InValid");
         }
         Artifact user = ArtifactQuery.getArtifactFromId(userTok, CoreBranches.COMMON);
         currentUser = new AtsUser();
         currentUser.setName(user.getName());
         currentUser.setUserId(user.getSoleAttributeValue(CoreAttributeTypes.UserId, ""));
         currentUser.setEmail(user.getSoleAttributeValue(CoreAttributeTypes.Email, ""));
         currentUser.setAbridgedEmail(user.getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, ""));
         currentUser.setActive(user.getSoleAttributeValue(CoreAttributeTypes.Active, false));
         currentUser.setId(user.getId());
         currentUser.getLoginIds().addAll(user.getAttributesToStringList(CoreAttributeTypes.LoginId));
         currentUser.setPhone(user.getSoleAttributeValue(CoreAttributeTypes.Phone, ""));
         currentUser.setStoreObject(user);
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
      return OseeApiService.user().getUserId();
   }

   @Override
   public AtsUser getCurrentUserOrNull() {
      UserToken user = OseeApiService.user();
      if (user.isValid()) {
         return getUserById(user);
      }
      return null;
   }

   @Override
   public boolean isAtsAdmin() {
      if (atsAdmin == null) {
         atsAdmin = AtsApiService.get().userService().isInUserGroup(AtsUserGroups.AtsAdmin);
      }
      return atsAdmin;
   }

   @Override
   public boolean isAtsDeleteWorkflowAdmin() {
      if (atsDeleteWorkflowAdmin == null) {
         atsDeleteWorkflowAdmin = AtsApiService.get().userService().isUserMember(AtsUserGroups.AtsDeleteWorkflowAdmin,
            AtsApiService.get().getUserService().getCurrentUser());
      }

      return atsDeleteWorkflowAdmin;
   }

   @Override
   public Collection<AtsUser> getUsers() {
      return configurationService.getConfigurations().getUsers();
   }

   @Override
   public void setAtsApi(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public AtsApi getAtsApi() {
      return atsApi;
   }

}
