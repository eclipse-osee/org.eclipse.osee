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
package org.eclipse.osee.framework.core.dsl.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link OseeDslAccessModel}
 *
 * @author Roberto E. Escobar
 */
public class OseeDslAccessModelTest {

   private static IAccessContextId accessContextId;

   @BeforeClass
   public static void setUp() {
      accessContextId = IAccessContextId.valueOf(Lib.generateArtifactIdAsInt(), "Context 1");
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAgumentCheck1() {
      AccessModel accessModel = new OseeDslAccessModel(null, null);
      accessModel.computeAccess(null, new ArrayList<Object>(), new AccessData());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAgumentCheck2() {
      AccessModel accessModel = new OseeDslAccessModel(null, null);
      accessModel.computeAccess(accessContextId, null, new AccessData());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAgumentCheck3() {
      AccessModel accessModel = new OseeDslAccessModel(null, null);
      accessModel.computeAccess(accessContextId, Collections.emptyList(), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullDsl() {
      MockDslProvider dslProvider = new MockDslProvider(null);
      AccessModel accessModel = new OseeDslAccessModel(null, dslProvider);
      accessModel.computeAccess(accessContextId, Collections.emptyList(), new AccessData());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAccessContext() {
      MockAccessModelInterpreter interpreter = new MockAccessModelInterpreter(null);
      MockDslProvider dslProvider = new MockDslProvider(MockModel.createDsl());
      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);
      accessModel.computeAccess(accessContextId, Collections.emptyList(), new AccessData());
   }

   @Test
   public void testComputeAccessTestCollection() {

      final Object checkedObject = new Object();

      Scope detail1Scope = new Scope().add("fail");
      Scope detail2Scope = new Scope().add("fail");
      final AccessDetail<?> detail1 =
         new AccessDetail<Object>(checkedObject, PermissionEnum.READ, detail1Scope, "detail 1");
      final AccessDetail<?> detail2 =
         new AccessDetail<Object>(checkedObject, PermissionEnum.WRITE, detail2Scope, "detail 2");

      final AccessData accessData = new AccessData();

      final AccessContext accessContext =
         MockModel.createAccessContext(Lib.generateArtifactIdAsInt(), "Access Context");

      OseeDsl oseeDsl = MockModel.createDsl();
      oseeDsl.getAccessDeclarations().add(accessContext);

      MockDslProvider dslProvider = new MockDslProvider(oseeDsl);
      MockAccessModelInterpreter interpreter = new MockAccessModelInterpreter(accessContext) {

         @Override
         public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) {
            super.computeAccessDetails(collector, accessContext, objectToCheck);
            Assert.assertEquals(accessContext, context);
            Assert.assertEquals(checkedObject, objectToCheck);
            Assert.assertNotNull(collector);

            Assert.assertTrue(accessData.isEmpty());

            collector.collect(detail1);
            collector.collect(detail2);
         }

      };

      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);
      accessModel.computeAccess(accessContextId, Collections.singleton(checkedObject), accessData);

      Assert.assertEquals(accessContextId, interpreter.getContextId());
      Collection<AccessContext> context = interpreter.getContexts();
      Assert.assertEquals(1, context.size());
      Assert.assertEquals(accessContext, context.iterator().next());
      Assert.assertTrue(interpreter.wasComputeCalled());

      Assert.assertFalse(accessData.isEmpty());
      Collection<AccessDetail<?>> details = accessData.getAccess(checkedObject);
      Assert.assertEquals(1, details.size());

      AccessDetail<?> actualDetail = details.iterator().next();
      Assert.assertEquals(PermissionEnum.READ, actualDetail.getPermission());
      Assert.assertEquals("detail 1", actualDetail.getReason());
      Assert.assertEquals(checkedObject, actualDetail.getAccessObject());
      Assert.assertEquals(detail1, actualDetail);
   }

   private static class MockAccessModelInterpreter implements AccessModelInterpreter {

      private final AccessContext contextToReturn;
      private Collection<AccessContext> contexts;
      private IAccessContextId contextId;
      private boolean wasComputeCalled;

      public MockAccessModelInterpreter(AccessContext contextToReturn) {
         this.contextToReturn = contextToReturn;
      }

      public Collection<AccessContext> getContexts() {
         return contexts;
      }

      public IAccessContextId getContextId() {
         return contextId;
      }

      @Override
      public AccessContext getContext(Collection<AccessContext> contexts, IAccessContextId contextId) {
         this.contextId = contextId;
         this.contexts = contexts;
         return contextToReturn;
      }

      @Override
      public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) {
         //
         wasComputeCalled = true;
      }

      public boolean wasComputeCalled() {
         return wasComputeCalled;
      }
   }
}
