/*
 * Created on May 3, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change;

public interface IChangeReportPreferences {

   public static interface Listener {
      void onDocumentOrderChange(boolean value);
   }

   void saveState();

   boolean isInDocumentOrder();

   void setInDocumentOrder(boolean isEnabled);

   void addListener(Listener listener);

   void removeListener(Listener listener);
}
