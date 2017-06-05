/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

}
