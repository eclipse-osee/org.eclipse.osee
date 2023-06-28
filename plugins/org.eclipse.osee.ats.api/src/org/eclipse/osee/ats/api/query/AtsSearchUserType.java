/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.query;

/**
 * @author Donald G. Dunne
 */
public enum AtsSearchUserType {

   None,
   Assignee,
   AssigneeWas, // user was assigned sometime in past states
   Originated,
   Subscribed,
   Favorites;

   public static AtsSearchUserType valueOfSafe(String userType) {
      try {
         return AtsSearchUserType.valueOf(userType);
      } catch (Exception ex) {
         return null;
      }
   }

   public boolean isNone() {
      return AtsSearchUserType.None.equals(this);
   }

   public boolean isAssignee() {
      return AtsSearchUserType.Assignee.equals(this);
   }

   public boolean isOriginated() {
      return AtsSearchUserType.Originated.equals(this);
   }

   public boolean isFavorites() {
      return AtsSearchUserType.Favorites.equals(this);
   }

   public boolean isSubscribed() {
      return AtsSearchUserType.Subscribed.equals(this);
   }

   public boolean isAssigneeWas() {
      return AtsSearchUserType.AssigneeWas.equals(this);
   }

}
