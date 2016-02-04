/*
 * Created on Apr 11, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

public class TypeUtil {

   public static long getId(AbstractOseeType type) {
      if (type instanceof AbstractOseeIdType) {
         return ((AbstractOseeIdType) type).getId();
      } else if (type instanceof BranchId) {
         return ((BranchId) type).getId();
      }
      throw new OseeArgumentException("Unsupported type");
   }
}
