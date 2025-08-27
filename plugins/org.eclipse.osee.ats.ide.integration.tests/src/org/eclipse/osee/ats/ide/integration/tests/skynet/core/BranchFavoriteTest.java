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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class BranchFavoriteTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private User kayJones;

   @Before
   public void setUp() throws Exception {
      kayJones = UserManager.getUser(DemoUsers.Kay_Jones);
      assertNotNull(kayJones);
      kayJones.deleteAttributes(CoreAttributeTypes.FavoriteBranch);
      kayJones.persist(testInfo.getTestName());
   }

   @After
   public void tearDown() throws Exception {
      kayJones.deleteAttributes(CoreAttributeTypes.FavoriteBranch);
      kayJones.persist(testInfo.getTestName());
   }

   @Test
   public void testFavoriteBranch() throws Exception {
      assertFalse(kayJones.isFavoriteBranch(SAW_Bld_1));
      assertFalse(kayJones.isFavoriteBranch(SAW_Bld_2));

      kayJones.toggleFavoriteBranch(SAW_Bld_1);
      assertFalse(kayJones.isDirty());
      assertTrue(kayJones.isFavoriteBranch(SAW_Bld_1));

      kayJones.toggleFavoriteBranch(SAW_Bld_1);
      assertFalse(kayJones.isDirty());
      assertFalse(kayJones.isFavoriteBranch(SAW_Bld_1));
   }

   @Test
   public void testFavoriteBranchCleanup() throws Exception {
      assertFalse(kayJones.isFavoriteBranch(SAW_Bld_1));
      assertFalse(kayJones.isFavoriteBranch(SAW_Bld_2));
      assertEquals(0, kayJones.getAttributeCount(CoreAttributeTypes.FavoriteBranch));

      kayJones.addAttribute(CoreAttributeTypes.FavoriteBranch, SAW_Bld_1.getIdString());
      kayJones.addAttribute(CoreAttributeTypes.FavoriteBranch, SAW_Bld_1.getIdString());
      kayJones.persist(testInfo.getTestName() + " - testFavoriteBranchCleanup");
      assertEquals(2, kayJones.getAttributeCount(CoreAttributeTypes.FavoriteBranch));

      assertTrue(kayJones.isFavoriteBranch(SAW_Bld_1));

      kayJones.toggleFavoriteBranch(SAW_Bld_1);
      assertFalse(kayJones.isDirty());
      assertFalse(kayJones.isFavoriteBranch(SAW_Bld_1));
      assertEquals(0, kayJones.getAttributeCount(CoreAttributeTypes.FavoriteBranch));
   }
}
