/*********************************************************************
 * Copyright (c) 2025 Boeing
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

/**
 * @author Donald G. Dunne
 */
public class UserUsageStatus {

   public static final UserUsageStatus SENTINEL = new UserUsageStatus(UserUsageType.SENTINEL, -1);
   private final UserUsageType actType;
   private final int daysSince;

   public UserUsageStatus(UserUsageType actType, int daysSince) {
      this.actType = actType;
      this.daysSince = daysSince;
   }

   public UserUsageType getActType() {
      return actType;
   }

   public int getDaysSince() {
      return daysSince;
   }

   public boolean isValid() {
      return this != SENTINEL;
   }

   public boolean isInValid() {
      return !isValid();
   }

   @Override
   public String toString() {
      return String.format("%s - %s days", actType, daysSince);
   }
}
