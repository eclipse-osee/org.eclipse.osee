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
package org.eclipse.osee.framework.core.dsl.integration.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.AccessDetailCollector;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link OseeDslAccessModel}
 * 
 * @author Roberto E. Escobar
 */
public class OseeDslAccessModelTest {

   private static AccessContextId accessContextId;

   @BeforeClass
   public static void setUp() {
      accessContextId = new MockAccessContextId(GUID.create(), "Context 1");
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAgumentCheck1() throws OseeCoreException {
      AccessModel accessModel = new OseeDslAccessModel(null, null);
      accessModel.computeAccess(null, new ArrayList<Object>(), new AccessData());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAgumentCheck2() throws OseeCoreException {
      AccessModel accessModel = new OseeDslAccessModel(null, null);
      accessModel.computeAccess(accessContextId, null, new AccessData());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAgumentCheck3() throws OseeCoreException {
      AccessModel accessModel = new OseeDslAccessModel(null, null);
      accessModel.computeAccess(accessContextId, Collections.emptyList(), null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullDsl() throws OseeCoreException {
      MockDslProvider dslProvider = new MockDslProvider(null);
      AccessModel accessModel = new OseeDslAccessModel(null, dslProvider);
      accessModel.computeAccess(accessContextId, Collections.emptyList(), new AccessData());
   }

   @Test(expected = OseeArgumentException.class)
   public void testComputeAccessNullAccessContext() throws OseeCoreException {
      MockAccessModelInterpreter interpreter = new MockAccessModelInterpreter(null);
      MockDslProvider dslProvider = new MockDslProvider(MockModel.createDsl());
      AccessModel accessModel = new OseeDslAccessModel(interpreter, dslProvider);
      accessModel.computeAccess(accessContextId, Collections.emptyList(), new AccessData());
   }

   @Test
   public void testComputeAccessTestCollection() throws OseeCoreException {

      final Object checkedObject = new Object();

      final AccessDetail<?> detail1 = new AccessDetail<Object>(checkedObject, PermissionEnum.READ, "detail 1");
      final AccessDetail<?> detail2 = new AccessDetail<Object>(checkedObject, PermissionEnum.WRITE, "detail 2");

      final AccessData accessData = new AccessData();

      final AccessContext accessContext = MockModel.createAccessContext(GUID.create(), "Access Context");

      OseeDsl oseeDsl = MockModel.createDsl();
      oseeDsl.getAccessDeclarations().add(accessContext);

      MockDslProvider dslProvider = new MockDslProvider(oseeDsl);
      MockAccessModelInterpreter interpreter = new MockAccessModelInterpreter(accessContext) {

         @Override
         public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) throws OseeCoreException {
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
      private AccessContextId contextId;
      private boolean wasComputeCalled;

      public MockAccessModelInterpreter(AccessContext contextToReturn) {
         this.contextToReturn = contextToReturn;
      }

      public Collection<AccessContext> getContexts() {
         return contexts;
      }

      public AccessContextId getContextId() {
         return contextId;
      }

      @SuppressWarnings("unused")
      @Override
      public AccessContext getContext(Collection<AccessContext> contexts, AccessContextId contextId) throws OseeCoreException {
         this.contextId = contextId;
         this.contexts = contexts;
         return contextToReturn;
      }

      @SuppressWarnings("unused")
      @Override
      public void computeAccessDetails(AccessDetailCollector collector, AccessContext context, Object objectToCheck) throws OseeCoreException {
         // 
         wasComputeCalled = true;
      }

      public boolean wasComputeCalled() {
         return wasComputeCalled;
      }
   }
}
