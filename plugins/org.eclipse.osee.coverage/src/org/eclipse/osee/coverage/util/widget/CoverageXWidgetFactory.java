/*
 * Created on Jan 16, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.util.widget;

import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider;

/**
 * @author Donald G. Dunne
 */
public class CoverageXWidgetFactory implements IXWidgetProvider {

   @Override
   public XWidget createXWidget(String widgetName, String name, DynamicXWidgetLayoutData widgetLayoutData) {
      if (widgetName.equals("XHyperlabelCoverageMethodSelection")) {
         return new XHyperlabelCoverageMethodSelection();
      }
      return null;
   }

}
