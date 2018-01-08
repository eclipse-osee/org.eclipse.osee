/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.integration.mocks.CheckAccessDetailCollectorNotCalled;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockAccessDetailCollector;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactDataProvider;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockArtifactProxy;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockRestrictionHandler;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AccessModelInterpreterImpl}
 *
 * @author Roberto E. Escobar
 */
public class AccessModelInterpreterImplTest {
   private IAccessContextId contextId1;
   private IAccessContextId contextId2;

   private AccessContext expectedContext1;
   private AccessContext expectedContext2;

   private AccessModelInterpreterImpl interpreterNoArtData;

   @Before
   public void setup() {
      interpreterNoArtData = new AccessModelInterpreterImpl(null, null);

      contextId1 = TokenFactory.createAccessContextId(GUID.create(), "Context 1");
      contextId2 = TokenFactory.createAccessContextId(GUID.create(), "Context 2");

      expectedContext1 = MockModel.createAccessContext(contextId1.getGuid(), "c1");
      expectedContext2 = MockModel.createAccessContext(contextId2.getGuid(), "c2");
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
      interpreterNoArtData.computeAccessDetails(new MockAccessDetailCollector(), null, new Object());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullCheck3() {
      interpreterNoArtData.computeAccessDetails(new MockAccessDetailCollector(), expectedContext1, null);
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
      AccessContext accessContext = MockModel.createAccessContext(contextId2.getGuid(), "c2");

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
