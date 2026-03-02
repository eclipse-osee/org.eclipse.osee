/*********************************************************************
 * Copyright (c) 2011 Boeing
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

import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

public abstract class XAbstractSelectedWidget extends XWidget {

   protected boolean selected = false;

   public XAbstractSelectedWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
   }

   public XAbstractSelectedWidget(WidgetId widgetId, String displayLabel, OseeImage oseeImage) {
      super(widgetId, displayLabel);
      setOseeImage(oseeImage);
   }

   @Override
   public void refresh() {
      updateCheckWidget();
   }

   public void set(boolean selected) {
      this.selected = selected;
      updateCheckWidget();
   }

   protected void updateCheckWidget() {
      validate();
   }

   public boolean isSelected() {
      return selected;
   }

   @Override
   public Object getData() {
      return Boolean.valueOf(isSelected());
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + selected;
   }

}
