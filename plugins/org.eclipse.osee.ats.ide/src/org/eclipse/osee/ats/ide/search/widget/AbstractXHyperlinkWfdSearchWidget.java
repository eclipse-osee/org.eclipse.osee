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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForObject;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkWfdSearchWidget<ObjectType extends Object> extends AbstractSearchWidget<XHyperlinkWfdForObject, Object> {

   public static final String CLEAR = "--clear--";
   private XHyperlinkWfdForObject hypWidget;

   public AbstractXHyperlinkWfdSearchWidget(SearchWidget srchWidget, WorldEditorParameterSearchItem searchItem) {
      super(srchWidget, searchItem);
   }

   @SuppressWarnings("unchecked")
   public ObjectType getSingle() {
      XHyperlinkWfdForObject widget = getWidget();
      if (widget != null) {
         return (ObjectType) widget.getSelected();
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public Collection<ObjectType> get() {
      XHyperlinkWfdForObject widget = getWidget();
      if (widget != null) {
         if (widget.isMultiSelect()) {
            return (Collection<ObjectType>) widget.getSelectedItems();
         } else {
            return Arrays.asList((ObjectType) widget.getSelected());
         }
      }
      return java.util.Collections.emptyList();
   }

   public abstract Collection<ObjectType> getSelectable();

   abstract boolean isMultiSelect();

   @Override
   public void widgetCreated(XWidget xWidget) {
      super.widgetCreated(xWidget);
      if (hypWidget == null && xWidget != null) {
         hypWidget = (XHyperlinkWfdForObject) xWidget;
         hypWidget.setMultiSelect(isMultiSelect());
         hypWidget.setLabel(getLabel());
         hypWidget.setSelectable(Collections.castAll(getSelectable()));
      }
   }

   protected String getLabel() {
      return srchWidget.getName();
   }

}
