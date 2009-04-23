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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetUtility {

   private XWidgetUtility() {
   }

   public static boolean isWidgetAccesible(Widget widget) {
      return widget != null && !widget.isDisposed();
   }

   public static List<XWidget> findXWidgetsInControl(Widget parent) {
      List<XWidget> xWidgets = new ArrayList<XWidget>();
      XWidget xWidget = asXWidget(parent);
      if (xWidget != null) {
         xWidgets.add(xWidget);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            xWidgets.addAll(findXWidgetsInControl(child));
         }
      }
      return xWidgets;
   }

   public static XWidget asXWidget(Widget widget) {
      XWidget toReturn = null;
      if (widget != null) {
         Object object = widget.getData(XWidget.XWIDGET_DATA_KEY);
         toReturn = object instanceof XWidget ? (XWidget) object : null;
      }
      return toReturn;
   }

   public static boolean hasXWidget(Widget widget) {
      boolean result = false;
      if (widget != null) {
         Object object = widget.getData(XWidget.XWIDGET_DATA_KEY);
         result = object instanceof XWidget;
      }
      return result;
   }
}
