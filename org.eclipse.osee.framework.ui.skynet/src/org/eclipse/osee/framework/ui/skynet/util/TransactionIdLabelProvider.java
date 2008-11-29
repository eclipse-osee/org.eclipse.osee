/*
 * Created on Nov 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Donald G. Dunne
 */
public class TransactionIdLabelProvider extends LabelProvider {

   public TransactionIdLabelProvider() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
    */
   @Override
   public String getText(Object element) {
      return ((TransactionId) element).toString() + " - " + ((TransactionId) element).getComment();
   }

}
