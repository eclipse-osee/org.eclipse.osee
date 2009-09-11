/*
 * Created on Jul 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Donald G. Dunne
 */
public class XBranchTextFilter extends XViewerTextFilter {

   /**
    * @param viewer
    */
   public XBranchTextFilter(XViewer viewer) {
      super(viewer);
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (element instanceof TransactionId) {
         return true;
      }
      if (element instanceof ArrayList<?>) {
         return true;
      }
      return super.select(viewer, parentElement, element);
   }

}
