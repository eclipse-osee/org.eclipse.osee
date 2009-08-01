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

import org.eclipse.osee.ats.util.widgets.defect.XDefectViewer;
import org.eclipse.osee.ats.util.widgets.role.XUserRoleViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IXWidgetProvider;

/**
 * @author Donald G. Dunne
 */
public class XWidgetProvider implements IXWidgetProvider {

   public XWidget createXWidget(String widgetName, String name, DynamicXWidgetLayoutData widgetLayoutData) {

      if (widgetName.equals("XDefectViewer")) {
         return new XDefectViewer();
      } else if (widgetName.equals("XUserRoleViewer")) {
         return new XUserRoleViewer();
      }
      return null;
   }

}
