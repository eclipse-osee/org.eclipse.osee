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
   // This will result in adding this and all children with all attributes
   Add_With_Moves,
   // This will result in updating the package item method number of the imported method number
   Moved_Due_To_Add,
   // Used to display "Nothing to Import" message
   Error__Message,
   // Shows that a item is un-mergeable and needs to be handled manually or have the merge case added
   Error__UnMergable;

   public boolean isError() {
      return toString().startsWith("Error__");
   }
}
