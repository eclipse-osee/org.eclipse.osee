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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.access.internal.cm.CmAccessProvider;
import org.eclipse.osee.framework.access.test.mocks.MockCMWithAccessModel;
import org.eclipse.osee.framework.access.test.mocks.MockConfigurationManagementProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link CmAccessProvider}
 *
 * @author Roberto E. Escobar
 */
public class CmAccessProviderTest {
   private static ArtifactToken expectedUser;
   private static Object expectedObject;
   private static IAccessContextId contextId1;

   @BeforeClass
   public static void setup() {
      expectedUser = ArtifactToken.valueOf(13, null, COMMON);
      expectedObject = new Object();
      contextId1 = MockDataFactory.createAccessContextId(GUID.create(), "context1");
   }

   @Test
   public void testCmProviderReturnsNull() {
      Collection<Object> objects = new ArrayList<>();
      objects.add(expectedObject);
      MockConfigurationManagementProvider cmProvider =
         new MockConfigurationManagementProvider(expectedUser, expectedObject, null);
      IAccessProvider accessProvider = new CmAccessProvider(cmProvider);

      AccessData accessData = new AccessData();
      accessProvider.computeAccess(expectedUser, objects, accessData);
      Assert.assertTrue(cmProvider.wasGetCMCalled());
   }

   @Test
   public void testCmProvider() {
      MockAccessModel accessModel = new MockAccessModel();
      MockCMWithAccessModel cm =
         new MockCMWithAccessModel(accessModel, expectedUser, expectedObject, false, Collections.singleton(contextId1));
      Collection<Object> objects = new ArrayList<>();
      objects.add(expectedObject);
      MockConfigurationManagementProvider cmProvider =
         new MockConfigurationManagementProvider(expectedUser, expectedObject, cm);
      IAccessProvider accessProvider = new CmAccessProvider(cmProvider);

      AccessData accessData = new AccessData();
      accessProvider.computeAccess(expectedUser, objects, accessData);
      Assert.assertTrue(cmProvider.wasGetCMCalled());

      Assert.assertTrue(accessModel.wasComputeAccessCalled);
      Assert.assertEquals(contextId1, accessModel.contextId);
      Assert.assertTrue(!Compare.isDifferent(objects, accessModel.objectsToCheck));
      Assert.assertEquals(accessData, accessModel.accessData);
   }

   private final class MockAccessModel implements AccessModel {

      protected boolean wasComputeAccessCalled;
      protected IAccessContextId contextId;
      protected Collection<Object> objectsToCheck;
      protected AccessData accessData;

      @Override
      public void computeAccess(IAccessContextId contextId, Collection<Object> objectsToCheck, AccessData accessData) {
         wasComputeAccessCalled = true;
         this.contextId = contextId;
         this.objectsToCheck = objectsToCheck;
         this.accessData = accessData;
      }
   }

}
