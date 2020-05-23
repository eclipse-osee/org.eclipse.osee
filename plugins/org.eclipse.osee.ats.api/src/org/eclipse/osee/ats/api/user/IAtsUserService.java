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

package org.eclipse.osee.ats.api.user;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.Active;

/**
 * Non-artifact based user service
 *
 * @author Donald G. Dunne
 */
public interface IAtsUserService {

   AtsUser getCurrentUser();

   String getCurrentUserId();

   AtsUser getUserByUserId(String userId);

   AtsUser getUserById(ArtifactId id);

   boolean isUserIdValid(String userId);

   boolean isUserNameValid(String name);

   AtsUser getUserByName(String name);

   Collection<AtsUser> getUsersByUserIds(Collection<String> userIds);

   boolean isAtsAdmin();

   boolean isAtsAdmin(AtsUser user);

   Collection<AtsUser> getUsers(Active active);

   Collection<AtsUser> getUsersSortedByName(Active active);

   void reloadCache();

   void releaseUser();

   Collection<AtsUser> getUsers();

   /**
    * @param accountId UserId or null
    * @return if accountId is null, then the IAtsUser corresponding to SystemUser.Anonymous is returned
    */
   AtsUser getUserByAccountId(UserId accountId);

   boolean isAtsAdmin(boolean useCache);

   Collection<AtsUser> getActiveAndAssignedInActive(Collection<? extends IAtsWorkItem> workItems);
   void setCurrentUser(AtsUser user);

   Collection<AtsUser> getRelatedUsers(AtsApi atsApi, ArtifactToken artifact, RelationTypeSide relation);

   void setConfigurationService(IAtsConfigurationsService configurationService);

   void addUser(AtsUser user);

   void clearCaches();

   AtsUser getCurrentUserNoCache();

   boolean isOseeAdmin();

   Boolean isUserMember(IUserGroupArtifactToken userGroup, UserId user);

   Boolean isUserMember(IUserGroupArtifactToken userGroup);

   AtsUser getUserByToken(UserToken userToken);

}
