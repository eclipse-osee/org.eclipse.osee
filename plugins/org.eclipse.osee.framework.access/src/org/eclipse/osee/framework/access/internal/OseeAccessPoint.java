/*
 * Created on Jun 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.internal;

import org.eclipse.osee.framework.access.OseeAccessHandler;
import org.eclipse.osee.framework.access.OseeAccessService;

public abstract class OseeAccessPoint<H extends OseeAccessHandler> {

   public static class Type<H> {
      private static int nextHashCode;
      private final int index;

      public Type() {
         index = ++nextHashCode;
      }

      @Override
      public final int hashCode() {
         return index;
      }

      @Override
      public String toString() {
         return "Access Type";
      }
   }

   protected OseeAccessPoint() {
   }

   public abstract Type<H> getAssociatedType();

   /**
    * Should only be called by {@link OseeAccessService}.
    */
   protected abstract void dispatch(H handler);

}
