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

import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueStringSel;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkEntrySearchWidget extends AbstractSearchWidget<XHyperlinkLabelValueStringSel, String> {

   public AbstractXHyperlinkEntrySearchWidget(String name, WorldEditorParameterSearchItem searchItem) {
      super(name, "XHyperlinkLabelValueStringSel", searchItem);
   }

   public String get() {
      XHyperlinkLabelValueStringSel widget = getWidget();
      return widget.getCurrentValue();
   }

   @Override
   public XHyperlinkLabelValueStringSel getWidget() {
      return super.getWidget();
   }

   public abstract void set(AtsSearchData data);

   boolean listenerAdded = false;
   private XHyperlinkLabelValueStringSel hypWidget;

   public void setup(XWidget xWidget) {
      if (hypWidget == null && xWidget != null) {
         hypWidget = (XHyperlinkLabelValueStringSel) xWidget;
         hypWidget.setLabel(name);
         hypWidget.setValue(get());
         if (!listenerAdded) {
            listenerAdded = true;
            hypWidget.addLabelMouseListener(new MouseAdapter() {

               @Override
               public void mouseUp(MouseEvent e) {
                  if (e.button == 3) {
                     clear();
                  }
               }

            });
         }
      }
   }

   protected void clear() {
      if (getWidget() != null) {
         setup(getWidget());
         XHyperlinkLabelValueStringSel widget = getWidget();
         widget.clear();
      }
   }

}
