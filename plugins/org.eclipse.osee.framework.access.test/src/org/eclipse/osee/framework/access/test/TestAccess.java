/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.test;

import org.eclipse.osee.framework.access.OseeAccessHandler;
import org.eclipse.osee.framework.access.OseeAccessService;
import org.eclipse.osee.framework.access.internal.OseeAccessPoint;
import org.eclipse.osee.framework.access.internal.OseeAccessServiceImpl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

public class TestAccess {

   @Test
   public void testAccess() throws OseeCoreException {
      OseeAccessService access = new OseeAccessServiceImpl();
      Assert.assertTrue(access.getAccessTypes().isEmpty());

      AccessHandler handler = new AccessHandler();

      access.addHandler(TestAccessPoint.TYPE, handler);

      Assert.assertEquals(1, access.getHandlerCount(TestAccessPoint.TYPE));
      Assert.assertFalse(access.getAccessTypes().isEmpty());

      access.removeHandler(TestAccessPoint.TYPE, handler);
      Assert.assertTrue(access.getAccessTypes().isEmpty());
      Assert.assertEquals(0, access.getHandlerCount(TestAccessPoint.TYPE));
   }

   private final static class TestAccessPoint extends OseeAccessPoint<AccessHandler> {

      private static final Type<AccessHandler> TYPE = new Type<AccessHandler>();

      @Override
      protected void dispatch(AccessHandler handler) {
         handler.setData("Pass In Information Here");
      }

      @Override
      public Type<AccessHandler> getAssociatedType() {
         return TYPE;
      }
   }

   private final class AccessHandler implements OseeAccessHandler {

      // Any Random data needed
      public void setData(Object object) {

      }
   }
}
