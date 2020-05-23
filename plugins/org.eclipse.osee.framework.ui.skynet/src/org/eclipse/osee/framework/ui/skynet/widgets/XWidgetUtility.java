/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Roberto E. Escobar
 */
public final class XWidgetUtility {

   public static boolean isWidgetAccesible(Widget widget) {
      return widget != null && !widget.isDisposed();
   }

   public static List<XWidget> findXWidgetsInControl(Widget parent) {
      List<XWidget> xWidgets = new ArrayList<>();
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
