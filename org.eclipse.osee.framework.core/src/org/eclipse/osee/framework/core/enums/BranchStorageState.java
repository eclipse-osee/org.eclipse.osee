/*
 * Created on Mar 18, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.enums;

/**
 * @author Jeff C. Phillips
 *
 */
public enum BranchStorageState {
   
   UN_ARCHIVED(0), ARCHIVED(1);
   private final int value;

   BranchStorageState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }
}
