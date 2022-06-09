/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.ats.api.util.AttributeValues;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class AttributeValuesSearchWidget extends AbstractSearchWidget<XDynamicAttrValuesWidget, AttributeValues> {

   public static final String ATTR_VALUE = "Attribute Values";
   private XDynamicAttrValuesWidget attrWidget;

   public AttributeValuesSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(ATTR_VALUE, "XDynamicAttrValuesWidget", searchItem);
   }

   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         attrWidget.setAttrValues(data.getAttrValues());
      }
   }

   public AttributeValues get() {
      XDynamicAttrValuesWidget attrWidget = getWidget();
      if (attrWidget != null) {
         return attrWidget.getAttrValues();
      }
      return new AttributeValues();
   }

   @Override
   public XDynamicAttrValuesWidget getWidget() {
      return attrWidget;
   }

   public void setup(XWidget widget) {
      if (widget != null) {
         attrWidget = (XDynamicAttrValuesWidget) widget;
      }
   }

}
