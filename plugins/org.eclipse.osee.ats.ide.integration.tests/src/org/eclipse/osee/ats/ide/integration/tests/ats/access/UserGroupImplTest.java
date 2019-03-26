/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.access;

import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.core.access.UserGroupService;
import org.eclipse.osee.framework.core.data.IUserGroupService;
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
      IUserGroupService svc = UserGroupService.get();
      Assert.assertNotNull(svc);

      // Joe Smith is no admin by default, but is temp admin
      Assert.assertFalse(svc.getUserGroup(AtsArtifactToken.AtsAdmin).isCurrentUserMember());
      Assert.assertTrue(svc.getUserGroup(AtsArtifactToken.AtsTempAdmin).isCurrentUserMember());
   }

}
