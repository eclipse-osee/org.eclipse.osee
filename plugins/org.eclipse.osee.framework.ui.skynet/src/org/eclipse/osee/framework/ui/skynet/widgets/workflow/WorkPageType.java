/*
 * Created on Nov 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

public enum WorkPageType {
   Completed,
   Cancelled,
   Working;

   public boolean isCompletedPage() {
      return this == Completed;
   }

   public boolean isCompletedOrCancelledPage() {
      return isCompletedPage() || isCancelledPage();
   }

   public boolean isCancelledPage() {
      return this == Cancelled;
   }

   public boolean isWorkingPage() {
      return this == Working;
   }
}
