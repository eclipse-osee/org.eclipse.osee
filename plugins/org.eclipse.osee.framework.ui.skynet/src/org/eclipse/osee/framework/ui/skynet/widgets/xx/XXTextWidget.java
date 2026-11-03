/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.graphics.Color;
import org.osgi.service.component.annotations.Component;

/**
 * Single line text box with in-line edit
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXTextWidget extends XXAbstractStringStyledTextWidget {

   public static final WidgetId ID = WidgetId.XXTextWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXTextWidget() {
      this(ID, "", "");
   }

   public XXTextWidget(String label) {
      this(ID, label, "");
   }

   public XXTextWidget(WidgetId widgetId, String label) {
      this(widgetId, "", "");
   }

   public XXTextWidget(WidgetId widgetId, String label, String value) {
      super(widgetId, label, value);
      set(XOption.VAL_SINGLE_TEXT, XOption.INLINE_EDIT, XOption.BORDER, XOption.FILL_HORZ);
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setText(String text) {
      setSelected(text);
   }

   public void set(String text) {
      setSelected(text);
   }

   public void set(XXTextWidget text) {
      setSelected(text.get());
   }

   public void setBackground(Color color) {
      valueSText.setBackground(color);
   }

   public Color getBackground() {
      return valueSText.getBackground();
   }

   public String getText() {
      return getSelectedFirst();
   }

}
