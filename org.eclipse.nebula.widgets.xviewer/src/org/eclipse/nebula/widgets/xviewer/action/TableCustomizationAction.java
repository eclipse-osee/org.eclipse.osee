/*
 * Created on Oct 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;

/**
 * @author Donald G. Dunne
 */
public class TableCustomizationAction extends Action {

   private final XViewer xViewer;

   public TableCustomizationAction(XViewer xViewer) {
      super("Table Customization");
      this.xViewer = xViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("customize.gif");
   }

   @Override
   public void run() {
      xViewer.getCustomizeMgr().handleTableCustomization();
   }

}
