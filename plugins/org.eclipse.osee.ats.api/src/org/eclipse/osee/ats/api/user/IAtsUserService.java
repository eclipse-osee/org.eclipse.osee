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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
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

   AtsUser getUserByName(String name);

   Collection<AtsUser> getUsersByUserIds(Collection<String> userIds);

   boolean isAtsAdmin();

   /**
    * Denotes if user is able to delete (but not purge) workflows.
    *
    * @return <code> true </code> if user is able to delete (but not purge) workflows.
    */
   boolean isAtsDeleteWorkflowAdmin();

   Collection<AtsUser> getUsers(Active active);

   void reloadCache();

   Collection<AtsUser> getUsers();

   Collection<AtsUser> getActiveAndAssignedInActive(Collection<? extends IAtsWorkItem> workItems);

   void setCurrentUser(AtsUser user);

   Collection<AtsUser> getRelatedUsers(AtsApi atsApi, ArtifactToken artifact, RelationTypeSide relation);

   void setConfigurationService(IAtsConfigurationsService configurationService);

   void clearCaches();

   AtsUser getCurrentUserNoCache();

   AtsUser getUserByToken(UserToken userToken);

   AtsUser getCurrentUserOrNull();

}