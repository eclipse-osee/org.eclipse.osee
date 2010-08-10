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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.access.internal.cm.CmAccessControlProviderImpl;
import org.eclipse.osee.framework.access.test.mocks.MockConfigurationManagement;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link CmAccessControlProvider}{@link CmAccessControlProviderImpl}
 * 
 * @author Roberto E. Escobar
 */
public class CmAccessControlProviderTest {

   private IBasicArtifact<?> user;
   private Object objectToCheck;

   @Before
   public void setup() {
      user = MockDataFactory.createArtifact(4);
      objectToCheck = new Object();
   }

   @Test
   public void testNoCms() throws OseeCoreException {
      assertCMProvider(user, objectToCheck, null);
   }

   @Test
   public void testCmButNotApplicable() throws OseeCoreException {
      MockConfigurationManagement cm1 = new MockConfigurationManagement(user, objectToCheck, false, null);
      assertCMProvider(user, objectToCheck, null, cm1);
   }

   @Test
   public void testExtraCmApplicable() throws OseeCoreException {
      MockConfigurationManagement cm1 = new MockConfigurationManagement(user, objectToCheck, false, null);
      MockConfigurationManagement cm2 = new MockConfigurationManagement(user, objectToCheck, true, null);
      assertCMProvider(user, objectToCheck, cm2, cm1, cm2);
   }

   @Test(expected = OseeStateException.class)
   public void testMoreThanOneCMApplies() throws OseeCoreException {
      MockConfigurationManagement cm1 = new MockConfigurationManagement(user, objectToCheck, true, null);
      MockConfigurationManagement cm2 = new MockConfigurationManagement(user, objectToCheck, true, null);
      assertCMProvider(user, objectToCheck, null, cm1, cm2);
   }

   private static void assertCMProvider(IBasicArtifact<?> user, Object objectToCheck, MockConfigurationManagement expectedCM, MockConfigurationManagement... extraCms) throws OseeCoreException {
      Collection<CmAccessControl> cmServices = new HashSet<CmAccessControl>();
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
