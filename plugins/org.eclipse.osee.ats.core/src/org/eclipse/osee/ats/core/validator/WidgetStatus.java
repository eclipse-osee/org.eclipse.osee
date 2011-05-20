/*
 * Created on May 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

/**
 * @author Donald G. Dunne
 */
public enum WidgetStatus {

   Valid,
   Empty,
   Invalid_Type, // string entered when should be int
   Invalid_Range, // entered 8 when should be >2 and <5 or entered 5 characters when min =8
   Invalid_Incompleted, // entered 2 integers when should be 3; have to enter hours for defect
   Exception;

   public boolean isValid() {
      return this == Valid;
   }

   public boolean isEmpty() {
      return this == Empty;
   }
}
