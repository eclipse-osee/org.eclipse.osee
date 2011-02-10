/*
 * Created on Feb 2, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.provider;

import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;

public class BooleanDefUtil {

   /**
    * @return if BooleanDef == null return defaultValue, else true or false
    */
   public static boolean get(BooleanDef booleanDef, boolean defaultValue) {
      if (booleanDef != null && booleanDef != BooleanDef.NONE) {
         return booleanDef == BooleanDef.TRUE;
      }
      return defaultValue;
   }

}
