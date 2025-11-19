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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class UserServiceImplTest {

   @Test
   public void testRolesAndAdmin() {
      UserToken joeUser = OseeApiService.user();
      Assert.assertEquals(joeUser, DemoUsers.Joe_Smith);
      Assert.assertTrue(joeUser.getRoles().contains(CoreUserGroups.AccountAdmin));

      Assert.assertTrue(joeUser.getRoles().size() > 5);

      UserToken jasonUser = OseeApiService.userSvc().getUser(DemoUsers.Jason_Michael);
      Assert.assertTrue(jasonUser.isOseeAdmin());
   }

}
