/*
 * Created on Oct 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTreeReport;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;

/**
 * @author Donald G. Dunne
 */
public class ViewTableReportAction extends Action {

   private final XViewer xViewer;

   public ViewTableReportAction(XViewer xViewer) {
      super("View Table Report");
      this.xViewer = xViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("report.gif");
   }

   @Override
   public void run() {
      if (xViewer.getXViewerFactory().getXViewerTreeReport(xViewer) != null) {
         xViewer.getXViewerFactory().getXViewerTreeReport(xViewer).open();
      } else {
         new XViewerTreeReport(xViewer).open();
      }
   }

}
