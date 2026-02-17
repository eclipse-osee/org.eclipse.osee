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

/**
 * @author Donald G. Dunne
 */
public class AttributeValuesSearchWidget extends AbstractSearchWidget<XDynamicAttrValuesWidget, AttributeValues> {

   public static SearchWidget AttributeValuesSearchWidget =
      new SearchWidget(3893898, "Attribute Values", "XDynamicAttrValuesWidget");

   public AttributeValuesSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(AttributeValuesSearchWidget, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         getWidget().setAttrValues(data.getAttrValues());
      }
   }

   public AttributeValues get() {
      XDynamicAttrValuesWidget attrWidget = getWidget();
      if (attrWidget != null) {
         return attrWidget.getAttrValues();
      }
      return new AttributeValues();
   }

}
