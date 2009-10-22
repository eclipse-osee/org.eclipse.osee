/*
 * Created on Oct 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.action;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.HtmlDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ViewSelectedCellDataAction extends Action {

   private final XViewer xViewer;

   public ViewSelectedCellDataAction(XViewer xViewer) {
      super("View Selected Cell Data");
      this.xViewer = xViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("report.gif");
   }

   @Override
   public void run() {
      try {
         TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
         TreeItem treeItem = xViewer.getRightClickSelectedItem();
         if (treeCol != null) {
            XViewerColumn xCol = (XViewerColumn) treeCol.getData();
            String data =
                  ((XViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(treeItem.getData(), xCol,
                        xViewer.getRightClickSelectedColumnNum());
            if (data != null && !data.equals("")) {
               String html = HtmlUtil.simplePage(HtmlUtil.pre(HtmlUtil.textToHtml(data)));
               new HtmlDialog(treeCol.getText() + " Data", treeCol.getText() + " Data", html).open();
            }
         }
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

}
