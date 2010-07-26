/*
 * Created on Jul 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.action;

import java.util.ArrayList;
import org.eclipse.osee.framework.core.model.TransactionRecord;

public interface ITransactionRecordSelectionProvider {

   public ArrayList<TransactionRecord> getSelectedTransactionRecords();

   public void refreshUI(ArrayList<TransactionRecord> records);
}
