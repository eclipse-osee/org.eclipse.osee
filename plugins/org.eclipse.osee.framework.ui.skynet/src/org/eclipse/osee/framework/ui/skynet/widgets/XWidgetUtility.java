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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

   /**
    * Preferred way to set label fonts so widgets can override
    */
   public static void setLabelFontsBold(Collection<XWidget> allXWidgets) {
      for (XWidget xWidget : allXWidgets) {
         Label labelWidget = xWidget.getLabelWidget();
         if (labelWidget != null) {
            if (xWidget.isUseLabelFont()) {
               // Set all XWidget labels to bold font
               setLabelFontsBold(labelWidget, FontManager.getDefaultLabelFont());
            } else {
               setLabelFontsBold(labelWidget, null);
            }
         }
      }
   }

   public static void setLabelFontsBold(XWidget xWidget) {
      setLabelFontsBold(Arrays.asList(xWidget));
   }

   /**
    * Recursively set label fonts from control. NOT preferred method if you have XWidget list. Use
    * setLabelFontsBold(List<XWidget> allXWidgets).
    */
   public static void setLabelFontsBold(Control parent, Font font) {
      if (parent instanceof Label) {
         Label label = (Label) parent;
         label.setFont(font);
      }
      if (parent instanceof Composite) {
         Composite container = (Composite) parent;
         for (Control child : container.getChildren()) {
            setLabelFontsBold(child, font);
         }
         container.layout();
      }
   }

}
