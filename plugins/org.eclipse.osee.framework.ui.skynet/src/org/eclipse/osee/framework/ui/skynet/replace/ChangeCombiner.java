/*
 * Created on Aug 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.replace;

import java.util.Collection;
import java.util.HashMap;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.change.Change;

/**
 * @author Jeff C. Phillips
 */
public class ChangeCombiner {

   public static Collection<Change> combine(Collection<Change> changes, TransactionRecord baselineTransaction) {
      HashMap<Integer, Change> attributeChanges = new HashMap<Integer, Change>();

      for (Change change : changes) {
         Change storedChange = attributeChanges.get(change.getItemId());
         if (storedChange != null) {
            if (!storedChange.getTxDelta().getStartTx().equals(baselineTransaction)) {
               attributeChanges.put(change.getItemId(), change);
            }
         } else {
            attributeChanges.put(change.getItemId(), change);
         }
      }
      return attributeChanges.values();
   }
}
