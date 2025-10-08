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

import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForObject;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkSelectionSearchWidget<ObjectType extends Object> extends AbstractSearchWidget<XHyperlinkWfdForObject, Object> {

   public static final String CLEAR = "--clear--";

   public AbstractXHyperlinkSelectionSearchWidget(String name, WorldEditorParameterSearchItem searchItem) {
      super(name, "XHyperlinkWfdForObject", searchItem);
   }

   @SuppressWarnings("unchecked")
   public Collection<ObjectType> get() {
      XHyperlinkWfdForObject widget = getWidget();
      if (widget != null) {
         return (Collection<ObjectType>) widget.getSelectedItems();
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public XHyperlinkWfdForObject getWidget() {
      return super.getWidget();
   }

   public abstract void set(AtsSearchData data);

   public abstract Collection<ObjectType> getSelectable();

   boolean listenerAdded = false;
   private XHyperlinkWfdForObject hypWidget;

   abstract boolean isMultiSelect();

   public void setup(XWidget xWidget) {
      if (hypWidget == null && xWidget != null) {
         hypWidget = (XHyperlinkWfdForObject) xWidget;
         hypWidget.setMultiSelect(isMultiSelect());
         hypWidget.setLabel(getLabel());
         hypWidget.setSelectable(Collections.castAll(getSelectable()));
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

   protected abstract String getLabel();

   protected void clear() {
      if (getWidget() != null) {
         setup(getWidget());
         XHyperlinkWfdForObject widget = getWidget();
         widget.clear();
      }
   }

   public String getInitialText() {
      return "";
   }

}
