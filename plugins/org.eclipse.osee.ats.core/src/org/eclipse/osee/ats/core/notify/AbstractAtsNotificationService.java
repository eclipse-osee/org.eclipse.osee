/*******************************************************************************
 * Copyright (c) 2020 Boeing.
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
package org.eclipse.osee.ats.core.notify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsNotificationService implements IAtsNotificationService {

   private volatile boolean emailEnabled = true;
   protected AtsApi atsApi;

   public AbstractAtsNotificationService() {
   }

   public AbstractAtsNotificationService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public boolean isNotificationsEnabled() {
      return emailEnabled;
   }

   @Override
   public void setNotificationsEnabled(boolean enabled) {
      this.emailEnabled = enabled;
   }

   @Override
   public Collection<AtsUser> getJournalSubscribedUsers(IAtsWorkItem workItem) {
      Set<AtsUser> users = new HashSet<>();
      Collection<ArtifactId> userArts =
         atsApi.getAttributeResolver().getAttributeValues(workItem, AtsAttributeTypes.JournalSubscriber);
      for (ArtifactId userArt : userArts) {
         AtsUser user = atsApi.getConfigService().getUser(userArt);
         if (user != null) {
            users.add(user);
         }
      }
      return users;
   }

   @Override
   public void setJournalSubscribedUsers(IAtsWorkItem workItem, Collection<AtsUser> users) {
      List<Object> userIds = new ArrayList<>();
      for (AtsUser user : users) {
         userIds.add(user.getArtifactId());
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Set Journal Subscribers");
      changes.setAttributeValues(workItem, AtsAttributeTypes.JournalSubscriber, userIds);
      changes.executeIfNeeded();
   }

}
