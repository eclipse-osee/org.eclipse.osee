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

package org.eclipse.osee.ats.core.users;

import org.eclipse.osee.ats.api.user.UserActivityData;
import org.eclipse.osee.ats.api.user.UserUsageType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link UserActivityData}
 *
 * @author Donald G. Dunne
 */
public class UserActivityDataTest {

   @Test
   public void testGetDaysSinceUse() {
      UserActivityData uad = new UserActivityData();
      Assert.assertEquals(UserUsageType.SENTINEL, uad.getEarliestActivityStatus().getActType());

      uad = new UserActivityData();
      uad.addUsageType(UserUsageType.IDE_CLIENT_USE, -1);
      uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, -1);
      int daysSinceLastUse = uad.getEarliestActivityStatusDaysSince();
      Assert.assertEquals(-1, daysSinceLastUse);
      Assert.assertEquals(UserUsageType.SENTINEL, uad.getEarliestActivityStatus().getActType());

      uad = new UserActivityData();
      uad.addUsageType(UserUsageType.IDE_CLIENT_USE, -1);
      uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, 45);
      daysSinceLastUse = uad.getEarliestActivityStatusDaysSince();
      Assert.assertEquals(45, daysSinceLastUse);
      Assert.assertEquals(UserUsageType.AUTHOR_TX_ENTRY, uad.getEarliestActivityStatus().getActType());

      uad = new UserActivityData();
      uad.addUsageType(UserUsageType.IDE_CLIENT_USE, 55);
      uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, -1);
      daysSinceLastUse = uad.getEarliestActivityStatusDaysSince();
      Assert.assertEquals(55, daysSinceLastUse);
      Assert.assertEquals(UserUsageType.IDE_CLIENT_USE, uad.getEarliestActivityStatus().getActType());

      uad = new UserActivityData();
      uad.addUsageType(UserUsageType.IDE_CLIENT_USE, 45);
      uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, 55);
      daysSinceLastUse = uad.getEarliestActivityStatusDaysSince();
      Assert.assertEquals(45, daysSinceLastUse);
      Assert.assertEquals(UserUsageType.IDE_CLIENT_USE, uad.getEarliestActivityStatus().getActType());

      uad = new UserActivityData();
      uad.addUsageType(UserUsageType.IDE_CLIENT_USE, 77);
      uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, 66);
      daysSinceLastUse = uad.getEarliestActivityStatusDaysSince();
      Assert.assertEquals(66, daysSinceLastUse);
      Assert.assertEquals(UserUsageType.AUTHOR_TX_ENTRY, uad.getEarliestActivityStatus().getActType());

      uad = new UserActivityData();
      uad.addUsageType(UserUsageType.IDE_CLIENT_USE, 77);
      uad.addUsageType(UserUsageType.AUTHOR_TX_ENTRY, 66);
      uad.addUsageType(UserUsageType.USER_REACTIVATED, 1);
      daysSinceLastUse = uad.getEarliestActivityStatusDaysSince();
      Assert.assertEquals(1, daysSinceLastUse);
      Assert.assertEquals(UserUsageType.USER_REACTIVATED, uad.getEarliestActivityStatus().getActType());

   }

}
