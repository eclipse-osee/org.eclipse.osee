/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.notify;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationEventFactory {

   public static AtsNotificationEvent getNotificationEvent(AtsUser fromUser, Collection<AtsUser> users, String id, String type, String description) {
      AtsNotificationEvent event = new AtsNotificationEvent();
      event.setType(type);
      event.setId(id);
      event.setDescription(description);
      event.setFromUserId(fromUser.getUserId());
      for (AtsUser user : users) {
         event.getUserIds().add(user.getUserId());
      }
      return event;
   }

   public static AtsNotificationEvent getNotificationEvent(AtsUser fromUser, Collection<AtsUser> users, String id, String type, String url, String cancelUrl, String description) {
      AtsNotificationEvent event = getNotificationEvent(fromUser, users, id, type, description);
      event.setUrl(url);
      return event;
   }

   public static AtsNotificationEvent getNotificationEventByUserIds(AtsUser fromUser, Collection<String> userIds, String id, String type, String description) {
      AtsNotificationEvent event = new AtsNotificationEvent();
      event.setType(type);
      event.setId(id);
      event.setDescription(description);
      event.getUserIds().addAll(userIds);
      event.setFromUserId(fromUser.getUserId());
      return event;
   }

   public static AtsNotificationEvent getNotificationEventByUserIds(AtsUser fromUser, Collection<String> userIds, String id, String type, String description, String url) {
      AtsNotificationEvent event = getNotificationEventByUserIds(fromUser, userIds, id, type, description);
      event.setUrl(url);
      return event;
   }

   public static AtsWorkItemNotificationEvent getWorkItemNotificationEvent(AtsUser fromUser, IAtsWorkItem workItem, List<AtsUser> users, AtsNotifyType... notifyType) {
      AtsWorkItemNotificationEvent event = getWorkItemNotificationEvent(fromUser, workItem, notifyType);
      for (AtsUser user : users) {
         event.getUserIds().add(user.getUserId());
      }
      return event;
   }

   public static AtsWorkItemNotificationEvent getWorkItemNotificationEvent(AtsUser fromUser, IAtsWorkItem workItem, AtsNotifyType... notifyType) {
      AtsWorkItemNotificationEvent event = new AtsWorkItemNotificationEvent();
      event.setFromUserId(fromUser.getUserId());
      if (!Strings.isValid(workItem.getAtsId())) {
         throw new OseeArgumentException("ATS Id cannot be null for %s", workItem.toStringWithId());
      }
      event.getAtsIds().add(workItem.getAtsId());
      event.getIds().add(workItem.getId());
      event.setNotifyType(notifyType);
      return event;
   }
}
