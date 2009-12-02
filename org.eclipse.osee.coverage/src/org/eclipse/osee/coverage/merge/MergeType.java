/*
 * Created on Nov 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.merge;

/**
 * @author Donald G. Dunne
 */
public enum MergeType {

   // This will result in adding this and all children with all attributes
   Add,
   // This will replace this item and all children; used for the "un-mergable" case
   Replace,
   //
   Update_Attributes,
   //
   Update_Children,
   //
   Error__Message,
   //
   Error__UnMergable;

   public boolean isError() {
      return toString().startsWith("Error__");
   }
}
