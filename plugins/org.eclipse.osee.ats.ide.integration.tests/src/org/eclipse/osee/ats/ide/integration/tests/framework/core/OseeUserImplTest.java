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

package org.eclipse.osee.ats.ide.integration.tests.framework.core;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class OseeUserImplTest {

   @Test
   public void testMapEntryAttribute() {
      UserToken user1 = UserToken.create(10L, "This", "", "3324", true);
      UserToken user2 = UserToken.create(10L, "That", "", "3325", true);

      Assert.assertEquals(user1, user2); // Same id, different class

      OseeUser oseeUser1 = OseeApiService.userSvc().create(user1);

      Assert.assertEquals(user1, oseeUser1); // Same id, different class

      ArtifactId userArt1 = ArtifactId.valueOf(10L);
      Assert.assertEquals(userArt1, oseeUser1); // Same id, different class
      Assert.assertEquals(oseeUser1, userArt1); // Same id, different class

      ArtifactId userArt2 = ArtifactId.valueOf(20L);
      List<ArtifactId> users = new ArrayList<>();
      users.add(userArt2);
      Assert.assertFalse(users.contains(userArt1));

      users.add(userArt1);
      Assert.assertTrue(users.contains(userArt1));
      Assert.assertTrue(users.contains(oseeUser1));
   }

}
