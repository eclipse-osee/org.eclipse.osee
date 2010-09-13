/*
 * Created on Sep 8, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.navigate;

import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;

class AtsQuickSearchOperationFactory implements IOperationFactory {

   @Override
   public IOperation createOperation() {
      EntryCheckDialog dialog =
         new EntryCheckDialog("Search by Strings", "Enter search strings", "Include Completed/Cancelled Workflows");
      if (dialog.open() == 0) {
         return new AtsQuickSearchOperation(new AtsQuickSearchData("Search by Strings", dialog.getEntry(),
            dialog.isChecked()));
      }
      return null;
   }
}
