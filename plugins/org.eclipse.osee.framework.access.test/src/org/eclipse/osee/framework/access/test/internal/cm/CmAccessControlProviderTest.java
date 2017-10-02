/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.test.internal.cm;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.access.internal.cm.CmAccessControlProviderImpl;
import org.eclipse.osee.framework.access.test.mocks.MockConfigurationManagement;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link CmAccessControlProvider}{@link CmAccessControlProviderImpl}
 *
 * @author Roberto E. Escobar
 */
public class CmAccessControlProviderTest {

   private ArtifactToken user;
   private Object objectToCheck;

   @Before
   public void setup() {
      user = ArtifactToken.valueOf(1, null, COMMON);
      objectToCheck = new Object();
   }

   @Test
   public void testNoCms() {
      assertCMProvider(user, objectToCheck, null);
   }

   @Test
   public void testCmButNotApplicable() {
      MockConfigurationManagement cm1 = new MockConfigurationManagement(user, objectToCheck, false, null);
      assertCMProvider(user, objectToCheck, null, cm1);
   }

   @Test
   public void testExtraCmApplicable() {
      MockConfigurationManagement cm1 = new MockConfigurationManagement(user, objectToCheck, false, null);
      MockConfigurationManagement cm2 = new MockConfigurationManagement(user, objectToCheck, true, null);
      assertCMProvider(user, objectToCheck, cm2, cm1, cm2);
   }

   @Test(expected = OseeStateException.class)
   public void testMoreThanOneCMApplies() {
      MockConfigurationManagement cm1 = new MockConfigurationManagement(user, objectToCheck, true, null);
      MockConfigurationManagement cm2 = new MockConfigurationManagement(user, objectToCheck, true, null);
      assertCMProvider(user, objectToCheck, null, cm1, cm2);
   }

   private static void assertCMProvider(ArtifactToken user, Object objectToCheck, MockConfigurationManagement expectedCM, MockConfigurationManagement... extraCms) {
      Collection<CmAccessControl> cmServices = new HashSet<>();
      for (CmAccessControl extraCm : extraCms) {
         cmServices.add(extraCm);
      }
      CmAccessControlProvider provider = new CmAccessControlProviderImpl(cmServices);
      CmAccessControl actualCM = provider.getService(user, objectToCheck);
      Assert.assertEquals(expectedCM, actualCM);
      for (CmAccessControl cmService : cmServices) {
         MockConfigurationManagement cm = (MockConfigurationManagement) cmService;
         Assert.assertTrue(cm.wasIsApplicableCalled());
      }
   }
}
