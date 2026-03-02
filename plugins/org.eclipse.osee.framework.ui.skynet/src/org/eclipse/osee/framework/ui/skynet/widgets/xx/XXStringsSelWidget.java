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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Widget to handle a list of strings to choose from in a filtered dialog. Also handles the default Enumerated Attribute
 * where selectable items are provided by default enums. This should NOT need to be extended for different cases.
 * Options can be provided through WidgetBuilder or default XWidget methods.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStringsSelWidget extends XAbstractXXWidget<String> {

   public static final WidgetId ID = WidgetId.XXStringsSelWidget;

   public XXStringsSelWidget() {
      this(ID, "");
   }

   public XXStringsSelWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
   }

   @Override
   protected String getSentinel() {
      return "";
   }

   @Override
   public Collection<String> getSelected() {
      if (hasArtifact() && getAttributeType().isValid()) {
         selected = getArtifact().getAttributesToStringList(getAttributeType());
      }
      return super.getSelected();
   }

   @Override
   public Collection<String> getSelectable() {
      if (!selectable.isEmpty()) {
         return selectable;
      }
      if (getAttributeType().isEnumerated()) {
         List<String> enumStrValues = getAttributeType().toEnum().getEnumStrValues();
         return enumStrValues;
      }
      return selectable;
   }

}
