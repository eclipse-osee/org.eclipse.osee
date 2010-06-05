/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.test;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.access.OseeAccessHandler;
import org.eclipse.osee.framework.access.OseeAccessService;
import org.eclipse.osee.framework.access.internal.OseeAccessPoint;
import org.eclipse.osee.framework.access.internal.OseeAccessServiceImpl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
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

      OseeAccessPoint<?> accessPoint = new TestAccessPoint();
      IStatus status = access.dispatch(new NullProgressMonitor(), "", accessPoint);
      //      Assert.assertTrue(handler.isWasChecked());

   }

   private final static class TestAccessPoint extends OseeAccessPoint<AccessHandler> {

      private static final Type<AccessHandler> TYPE = new Type<AccessHandler>();

      @Override
      public Type<AccessHandler> getAssociatedType() {
         return TYPE;
      }

      //
      //      @Override
      //      protected void dispatch(AccessHandler handler) {
      //         handler.setData("Pass In Information Here");
      //         // Initialize data
      //
      //         //         run the rule
      //
      //      }

      @Override
      protected IStatus dispatch(AccessHandler handler) {
         handler.create("A", "B", "C");
         return null;
      }
   }

   private final class AccessHandler extends AbstractOperation implements OseeAccessHandler {

      private final String a;
      private final String b;

      public AccessHandler() {
         this("", "");
      }

      public AccessHandler(String a, String b) {
         super("", "");
         this.a = a;
         this.b = b;
      }

      public IOperation create(String string, String string2, String string3) {
         return new AccessHandler();
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {

      }

      // Any Random data needed
      //      public void setData(Object object) {
      //         wasChecked = true;
      //      }

      //      public void setWasChecked(boolean wasChecked) {
      //         this.wasChecked = wasChecked;
      //      }
      //
      //      public boolean isWasChecked() {
      //         return wasChecked;
      //      }

   }
}
