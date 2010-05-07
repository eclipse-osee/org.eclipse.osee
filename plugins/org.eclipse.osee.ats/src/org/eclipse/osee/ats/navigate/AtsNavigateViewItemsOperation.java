/*
 * Created on May 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.navigate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.operation.AbstractOperation;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateViewItemsOperation extends AbstractOperation {

   public AtsNavigateViewItemsOperation() {
      super("Loading ATS Navigate View Items", AtsPlugin.PLUGIN_ID);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      AtsNavigateViewItems.getInstance().getSearchNavigateItems();
   }

}
