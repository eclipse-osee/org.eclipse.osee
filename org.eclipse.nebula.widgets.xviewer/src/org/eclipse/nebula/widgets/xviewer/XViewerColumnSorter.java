/*
 * Created on Oct 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumnSorter extends ViewerSorter {

   public XViewerColumnSorter() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      if (e1 instanceof XViewerColumn) {
         return getComparator().compare(((XViewerColumn) e1).toString(), ((XViewerColumn) e2).toString());
      } else if ((e1 instanceof TreeColumn) && ((TreeColumn) e1).getData() instanceof XViewerColumn) {

         return getComparator().compare(((XViewerColumn) ((TreeColumn) e1).getData()).toString(),
               ((XViewerColumn) ((TreeColumn) e2).getData()).toString());
      } else
         return 0;
   }
}
