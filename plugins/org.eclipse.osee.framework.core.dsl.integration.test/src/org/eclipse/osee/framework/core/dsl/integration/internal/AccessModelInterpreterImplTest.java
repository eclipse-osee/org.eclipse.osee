/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.dsl.integration.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.dsl.integration.AccessDataCollector;
import org.eclipse.osee.framework.core.dsl.integration.mocks.CheckAccessDetailCollectorNotCalled;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactDataProvider;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockRestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AccessModelInterpreterImpl}
 *
 * @author Roberto E. Escobar
 */
public class AccessModelInterpreterImplTest {
   private AccessContextToken contextId1;
   private AccessContextToken contextId2;

   private AccessContext expectedContext1;
   private AccessContext expectedContext2;

   private AccessModelInterpreterImpl interpreterNoArtData;

   @Before
   public void setup() {
      interpreterNoArtData = new AccessModelInterpreterImpl(null, null);

      contextId1 = AccessContextToken.valueOf(Lib.generateArtifactIdAsInt(), "Context 1");
      contextId2 = AccessContextToken.valueOf(Lib.generateArtifactIdAsInt(), "Context 2");

      expectedContext1 = MockModel.createAccessContext(contextId1.getId(), "c1");
      expectedContext2 = MockModel.createAccessContext(contextId2.getId(), "c2");
   }

   @Test
   public void testGetContext() {

      Collection<AccessContext> contexts = Arrays.asList(expectedContext1, expectedContext2);
      AccessContext actualContext1 = interpreterNoArtData.getContext(contexts, contextId1);
      Assert.assertEquals(expectedContext1, actualContext1);

      AccessContext actualContext2 = interpreterNoArtData.getContext(contexts, contextId2);
      Assert.assertEquals(expectedContext2, actualContext2);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetContextNullCheck1() {
      interpreterNoArtData.getContext(null, contextId1);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetContextNullCheck2() {
      interpreterNoArtData.getContext(Collections.<AccessContext> emptyList(), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullCheck1() {
      interpreterNoArtData.computeAccessDetails(null, expectedContext1, new Object());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullCheck2() {
      interpreterNoArtData.computeAccessDetails(new AccessDataCollector(), null, new Object());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullCheck3() {
      interpreterNoArtData.computeAccessDetails(new AccessDataCollector(), expectedContext1, null);
   }

   @Test
   public void testComputeAccessNotApplicableObject() {
      final Object objectToCheck = new Object();
      MockArtifactDataProvider provider = new MockArtifactDataProvider(false, objectToCheck, null);
      AccessModelInterpreterImpl interpreter = new AccessModelInterpreterImpl(provider, null);
      interpreter.computeAccessDetails(new CheckAccessDetailCollectorNotCalled(), expectedContext1, objectToCheck);
      Assert.assertTrue("Provider isApplicableCalled failed", provider.wasIsApplicableCalled());
      Assert.assertFalse("Provider asCastedObjectCalled failed", provider.wasAsCastedObjectCalled());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessCastedObjectNull() {
      final Object objectToCheck = new Object();
      MockArtifactDataProvider provider = new MockArtifactDataProvider(true, objectToCheck, null);
      AccessModelInterpreterImpl interpreter = new AccessModelInterpreterImpl(provider, null);
      try {
         interpreter.computeAccessDetails(new CheckAccessDetailCollectorNotCalled(), expectedContext1, objectToCheck);
      } finally {
         Assert.assertTrue("Provider isApplicableCalled failed", provider.wasIsApplicableCalled());
         Assert.assertTrue("Provider asCastedObjectCalled failed", provider.wasAsCastedObjectCalled());
      }
   }

   @Test
   public void testComputeAccessCheckRestriction() {
      AccessContext accessContext = MockModel.createAccessContext(contextId2.getId(), "c2");

      MockArtifactProxy artifactData = new MockArtifactProxy();

      ObjectRestriction objectRestriction = null;
      assertComputeDetails(accessContext, artifactData, objectRestriction, false);
   }

   private static void assertComputeDetails(AccessContext accessContext, MockArtifactProxy artifactData, ObjectRestriction objectRestriction, boolean expectedProcessCalled) {
      final Object objectToCheck = new Object();
      MockArtifactDataProvider provider = new MockArtifactDataProvider(true, objectToCheck, artifactData);
      AccessDetailCollector collector = new CheckAccessDetailCollectorNotCalled();
      MockRestrictionHandler restrictionHandler =
         new MockRestrictionHandler(objectRestriction, artifactData, collector);
      AccessModelInterpreterImpl interpreter = new AccessModelInterpreterImpl(provider, null, restrictionHandler);
      interpreter.computeAccessDetails(collector, accessContext, objectToCheck);
      Assert.assertTrue("Provider isApplicableCalled failed", provider.wasIsApplicableCalled());
      Assert.assertTrue("Provider asCastedObjectCalled failed", provider.wasAsCastedObjectCalled());
      Assert.assertEquals("Restriction process called check failed", expectedProcessCalled,
         restrictionHandler.wasProcessCalled());
   }
}
