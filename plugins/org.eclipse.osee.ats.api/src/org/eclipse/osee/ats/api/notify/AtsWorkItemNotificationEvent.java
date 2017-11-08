/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.notify;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemNotificationEvent {

   private String fromUserId;
   private final Collection<String> userIds = new HashSet<>();
   private final Collection<String> atsIds = new HashSet<>();
   private final Set<AtsNotifyType> notifyTypes = new HashSet<>();

   public Collection<String> getAtsIds() {
      return atsIds;
   }

   public Collection<String> getUserIds() {
      return userIds;
   }

   public void setNotifyType(AtsNotifyType... notifyTypes) {
      for (AtsNotifyType type : notifyTypes) {
         this.notifyTypes.add(type);
      }
   }

   public Set<AtsNotifyType> getNotifyTypes() {
      return notifyTypes;
   }

   public String getFromUserId() {
      return fromUserId;
   }

   public void setFromUserId(String fromUserId) {
      this.fromUserId = fromUserId;
   }

   @Override
   public String toString() {
      return "AtsWorkItemNotificationEvent [fromUserId=" + fromUserId + ", userIds=" + userIds + ", atsIds=" + atsIds + ", notifyTypes=" + notifyTypes + "]";
   }
}
