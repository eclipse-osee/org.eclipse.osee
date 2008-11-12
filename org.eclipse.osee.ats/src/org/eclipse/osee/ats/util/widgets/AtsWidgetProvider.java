/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.util.widgets;

import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsWidgetProvider implements IXWidgetProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider#createXWidget(java.lang.String, java.lang.String, boolean, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData)
    */
   @Override
   public XWidget createXWidget(String widgetName, String name, DynamicXWidgetLayoutData widgetLayoutData) {
      XWidget toReturn = null;
      if (widgetName.equals(XHyperlabelTeamDefinitionSelection.WIDGET_ID)) {
         XHyperlabelTeamDefinitionSelection widget = new XHyperlabelTeamDefinitionSelection(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         toReturn = widget;
      }
      if (widgetName.equals(XHyperlabelGroupSelection.WIDGET_ID)) {
         XHyperlabelGroupSelection widget = new XHyperlabelGroupSelection(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         toReturn = widget;
      }
      return toReturn;
   }

}
