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
import org.eclipse.osee.ats.api.user.UserActivityStatus;
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
      Assert.assertEquals(UserActivityStatus.Not_Set, uad.getStatus());

      uad.setDaysSinceIdeUse(-1);
      uad.setDaysSinceTxsAuthored(-1);
      int daysSinceLastUse = uad.getDaysSinceLastUse();
      Assert.assertEquals(-1, daysSinceLastUse);
      Assert.assertEquals(UserActivityStatus.No_Use_Detected, uad.getStatus());

      uad.setDaysSinceIdeUse(-1);
      uad.setDaysSinceTxsAuthored(45);
      daysSinceLastUse = uad.getDaysSinceLastUse();
      Assert.assertEquals(45, daysSinceLastUse);
      Assert.assertEquals(UserActivityStatus.Only_Days_Since_Txs_Found, uad.getStatus());

      uad.setDaysSinceIdeUse(55);
      uad.setDaysSinceTxsAuthored(-1);
      daysSinceLastUse = uad.getDaysSinceLastUse();
      Assert.assertEquals(55, daysSinceLastUse);
      Assert.assertEquals(UserActivityStatus.Only_Days_Since_Ide_Found, uad.getStatus());

      uad.setDaysSinceIdeUse(45);
      uad.setDaysSinceTxsAuthored(55);
      daysSinceLastUse = uad.getDaysSinceLastUse();
      Assert.assertEquals(45, daysSinceLastUse);
      Assert.assertEquals(UserActivityStatus.Both_Days_Found_Ide_Is_Less, uad.getStatus());

      uad.setDaysSinceIdeUse(77);
      uad.setDaysSinceTxsAuthored(66);
      daysSinceLastUse = uad.getDaysSinceLastUse();
      Assert.assertEquals(66, daysSinceLastUse);
      Assert.assertEquals(UserActivityStatus.Both_Days_Found_Txs_Is_Less, uad.getStatus());
   }

}
