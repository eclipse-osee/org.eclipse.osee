/**
 * 
 */
package org.eclipse.osee.framework.db.connection.core;

/**
 * @author Ryan D. Brooks
 */
public enum BranchType {
   STANDARD(0), ROOT(1), BASELINE(2), MERGE(3);
   private final int value;

   BranchType(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }

   public static BranchType getBranchType(int value) {
      for (BranchType type : values())
         if (type.getValue() == value) return type;
      return null;
   }
}
