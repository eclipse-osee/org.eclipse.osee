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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider;

/**
 * @author Roberto E. Escobar
 */
public class XBranchSelectWidgetProvider implements IXWidgetProvider {

   @Override
   public XWidget createXWidget(String widgetName, String name, DynamicXWidgetLayoutData widgetLayoutData) {
      XWidget toReturn = null;
      if (widgetName.equals(XBranchSelectWidget.WIDGET_ID)) {
         XBranchSelectWidget widget = new XBranchSelectWidget(name);
         widget.setToolTip(widgetLayoutData.getToolTip());
         try {
            String branchGuid = widgetLayoutData.getDefaultValue();
            if (branchGuid != null) {
               widget.setSelection(BranchManager.getBranchByGuid(branchGuid));
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         toReturn = widget;
      }
      return toReturn;
   }

}
