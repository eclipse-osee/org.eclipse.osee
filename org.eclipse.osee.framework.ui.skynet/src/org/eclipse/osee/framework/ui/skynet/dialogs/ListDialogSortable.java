/*
 * Created on Oct 30, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.dialogs;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumnSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ListDialogSortable extends ListDialog {

   private ViewerSorter viewerSorter;

   /**
    * @param parent
    */
   public ListDialogSortable(Shell parent) {
      super(parent);
   }

   /**
    * @param parent
    */
   public ListDialogSortable(ViewerSorter viewerSorter, Shell parent) {
      super(parent);
      this.viewerSorter = viewerSorter;
   }

   public void setSorter(ViewerSorter viewerSorter) {
      this.viewerSorter = viewerSorter;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      if (viewerSorter != null) {
         getTableViewer().setSorter(new XViewerColumnSorter());
      }
      return control;
   }

}
