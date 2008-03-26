/*
 * Created on Mar 25, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider;

/**
 * @author Roberto E. Escobar
 */
public class XBranchSelectWidgetProvider implements IXWidgetProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider#createXWidget(java.lang.String, java.lang.String, boolean, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData)
    */
   @Override
   public XWidget createXWidget(String widgetName, String name, boolean labelAfter, DynamicXWidgetLayoutData widgetLayoutData) {
      XWidget toReturn = null;
      if (widgetName.equals(XBranchSelectWidget.WIDGET_ID)) {
         XBranchSelectWidget widget = new XBranchSelectWidget(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         widget.setDefaultBranch(widgetLayoutData.getDefaultValue());
         toReturn = widget;
      }
      return toReturn;
   }

}
