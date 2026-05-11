/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.search.widget;

import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueStringSel;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkEntrySearchWidget extends AbstractSearchWidget<XHyperlinkLabelValueStringSel, String> {

   private XHyperlinkLabelValueStringSel hypWidget;

   public AbstractXHyperlinkEntrySearchWidget(SearchWidget srchWidget, WorldEditorParameterSearchItem searchItem) {
      super(srchWidget, searchItem);
   }

   public String get() {
      XHyperlinkLabelValueStringSel widget = getWidget();
      if (widget != null) {
         return widget.getCurrentValue();
      }
      return "";
   }

   @Override
   public void widgetCreated(XWidget xWidget) {
      super.widgetCreated(xWidget);
      if (hypWidget == null && xWidget != null) {
         hypWidget = (XHyperlinkLabelValueStringSel) xWidget;
         hypWidget.setLabel(srchWidget.getName());
         hypWidget.setValue(get());
      }
   }

}
