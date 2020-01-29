/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.user;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.Active;

/**
 * Non-artifact based user service
 *
 * @author Donald G. Dunne
 */
public interface IAtsUserService {

   IAtsUser getCurrentUser();

   String getCurrentUserId();

   IAtsUser getUserById(String userId);

   IAtsUser getUserByArtifactId(ArtifactId id);

   boolean isUserIdValid(String userId);

   boolean isUserNameValid(String name);

   IAtsUser getUserByName(String name);

   Collection<IAtsUser> getUsersByUserIds(Collection<String> userIds);

   boolean isAtsAdmin();

   boolean isAtsAdmin(IAtsUser user);

   List<IAtsUser> getUsers(Active active);

   List<IAtsUser> getUsersSortedByName(Active active);

   void reloadCache();

   void releaseUser();

   List<? extends IAtsUser> getUsers();

   List<? extends IAtsUser> getUsersFromDb();

   IAtsUser getUserByAccountId(Long accountId);

   /**
    * @return user specified by osee.account_id or null
    */
   IAtsUser getUserByAccountId(HttpHeaders httpHeaders);

   boolean isAtsAdmin(boolean useCache);

   Collection<IAtsUser> getActiveAndAssignedInActive(Collection<? extends IAtsWorkItem> workItems);

   void setCurrentUser(IAtsUser user);

   AtsUser getAtsUser(IAtsUser user);

   Collection<IAtsUser> getRelatedUsers(AtsApi atsApi, ArtifactToken artifact, RelationTypeSide relation);

   void setConfigurationService(IAtsConfigurationsService configurationService);

}
