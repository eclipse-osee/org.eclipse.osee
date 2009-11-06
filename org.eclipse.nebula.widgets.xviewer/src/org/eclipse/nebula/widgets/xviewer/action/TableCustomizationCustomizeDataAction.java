/*
 * Created on Oct 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;

/**
 * Action to load a specified customize data object
 * 
 * @author Donald G. Dunne
 */
public class TableCustomizationCustomizeDataAction extends Action {

   private final XViewer xViewer;
   private final CustomizeData custData;

   public TableCustomizationCustomizeDataAction(XViewer xViewer, CustomizeData custData) {
      super(custData.getName());
      this.xViewer = xViewer;
      this.custData = custData;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("customize.gif");
   }

   @Override
   public void run() {
      xViewer.getCustomizeMgr().loadCustomization(custData);
      xViewer.refresh();
   }

   @Override
   public String getToolTipText() {
      return "Customize Table";
   }

}
