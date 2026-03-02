/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdForEnumAttrWidget extends XAbstractHyperlinkWithFilteredDialogWidget<String> {

   public static final WidgetId ID = WidgetId.XHyperlinkWfdForEnumAttrWidget;

   private Collection<String> selectable = null;

   public XHyperlinkWfdForEnumAttrWidget() {
      this(ID);
   }

   public XHyperlinkWfdForEnumAttrWidget(WidgetId widgetId) {
      super(widgetId, "unknown");
   }

   @Override
   public Collection<String> getSelectable() {
      if (selectable != null) {
         return selectable;
      }
      List<String> enumStrValues = getAttributeType().toEnum().getEnumStrValues();
      return enumStrValues;
   }

   @Override
   protected ViewerComparator getComparator() {
      if (getAttributeType().hasDisplayHint(DisplayHint.InOrder)) {
         // Do not sort, use enum as supplied to dialogs
         return NoOpViewerComparator.instance;
      }
      return super.getComparator();
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      super.setAttributeType(attributeType);
      if (attributeType.isValid()) {
         setLabel(attributeType.getUnqualifiedName());
      }
   }

   /**
    * Override attributes enum values. If not set, enum type's values will be used.
    */
   @Override
   public void setSelectable(Collection<String> selectable) {
      this.selectable = selectable;
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      validate();
   }

}
