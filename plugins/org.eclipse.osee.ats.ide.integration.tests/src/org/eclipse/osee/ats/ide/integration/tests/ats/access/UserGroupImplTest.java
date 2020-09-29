/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.access;

import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for @link UserGroupImpl
 *
 * @author Donald G. Dunne
 */
public class UserGroupImplTest {

   @Test
   public void test() {
      // Joe Smith is not admin by default, but is temp admin
      Assert.assertFalse(UserGroupService.get(AtsUserGroups.AtsAdmin).isCurrentUserMember());
      Assert.assertTrue(UserGroupService.get(AtsUserGroups.AtsTempAdmin).isCurrentUserMember());
   }

}
