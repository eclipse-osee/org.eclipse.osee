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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.client.demo.DemoBranches;
import org.eclipse.osee.client.demo.DemoUsers;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class BranchFavoriteTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo testInfo = new TestInfo();

   private User joeSmith;
   private Branch saw1Branch, saw2Branch;

   @Before
   public void setUp() throws Exception {
      joeSmith = UserManager.getUser(DemoUsers.Joe_Smith);
      assertNotNull(joeSmith);
      saw1Branch = BranchManager.getBranch(DemoBranches.SAW_Bld_1);
      assertNotNull(saw1Branch);
      saw2Branch = BranchManager.getBranch(DemoBranches.SAW_Bld_2);
      assertNotNull(saw2Branch);
      joeSmith.deleteAttributes(CoreAttributeTypes.FavoriteBranch);
      joeSmith.persist(testInfo.getTestName());
   }

   @After
   public void tearDown() throws Exception {
      joeSmith.deleteAttributes(CoreAttributeTypes.FavoriteBranch);
      joeSmith.persist(testInfo.getTestName());
   }

   @Test
   public void testFavoriteBranch() throws Exception {
      assertFalse(joeSmith.isFavoriteBranch(saw1Branch));
      assertFalse(joeSmith.isFavoriteBranch(saw2Branch));

      joeSmith.toggleFavoriteBranch(saw1Branch);
      assertFalse(joeSmith.isDirty());
      assertTrue(joeSmith.isFavoriteBranch(saw1Branch));

      joeSmith.toggleFavoriteBranch(saw1Branch);
      assertFalse(joeSmith.isDirty());
      assertFalse(joeSmith.isFavoriteBranch(saw1Branch));
   }

   @Test
   public void testFavoriteBranchCleanup() throws Exception {
      assertFalse(joeSmith.isFavoriteBranch(saw1Branch));
      assertFalse(joeSmith.isFavoriteBranch(saw2Branch));
      assertEquals(0, joeSmith.getAttributeCount(CoreAttributeTypes.FavoriteBranch));

      joeSmith.addAttribute(CoreAttributeTypes.FavoriteBranch, String.valueOf(saw1Branch.getUuid()));
      joeSmith.addAttribute(CoreAttributeTypes.FavoriteBranch, String.valueOf(saw1Branch.getUuid()));
      joeSmith.persist(testInfo.getTestName() + " - testFavoriteBranchCleanup");
      assertEquals(2, joeSmith.getAttributeCount(CoreAttributeTypes.FavoriteBranch));

      assertTrue(joeSmith.isFavoriteBranch(saw1Branch));

      joeSmith.toggleFavoriteBranch(saw1Branch);
      assertFalse(joeSmith.isDirty());
      assertFalse(joeSmith.isFavoriteBranch(saw1Branch));
      assertEquals(0, joeSmith.getAttributeCount(CoreAttributeTypes.FavoriteBranch));
   }
}
