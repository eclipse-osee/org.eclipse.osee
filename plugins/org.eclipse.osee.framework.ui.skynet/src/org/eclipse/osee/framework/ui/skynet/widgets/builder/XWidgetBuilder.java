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
package org.eclipse.osee.framework.ui.skynet.widgets.builder;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public class XWidgetBuilder {

   StringBuffer sb = new StringBuffer(2000);
   boolean ended = false;
   private final List<XWidgetRendererItem> datas = new LinkedList<XWidgetRendererItem>();
   private XWidgetRendererItem currItem;

   public XWidgetBuilder andRequired() {
      sb.append(" required=\"%s\" ");
      return this;
   }

   public XWidgetBuilder endWidget() {
      currItem = null;
      return this;
   }

   public void newXWidget() {
      currItem = new XWidgetRendererItem(new SwtXWidgetRenderer());
      datas.add(currItem);
   }

   public XWidgetBuilder andWidget(String displayName, String widgetType) {
      newXWidget();
      currItem.setName(displayName);
      currItem.setXWidgetName(widgetType);
      return this;
   }

   public List<XWidgetRendererItem> getItems() {
      return datas;
   }

   public XWidgetBuilder andHeight(int height) {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setFillVertically(true);
      currItem.setHeight(height);
      return this;
   }

   public XWidgetBuilder andFillVertically() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setFillVertically(true);
      return this;
   }

   public XWidgetBuilder andToolTip(AttributeTypeString attrType) {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setToolTip(attrType.getDescription());
      return this;
   }

   public XWidgetBuilder andToolTip(String toolTip) {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setToolTip(toolTip);
      return this;
   }

   public XWidgetBuilder andXCombo(AttributeTypeToken attrType) {
      return andXCombo(attrType.getUnqualifiedName(), attrType);

   }

   public XWidgetBuilder andXCombo(String displayName, AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setName(displayName);
      String optionsStr = Collections.toString(",", attrType.toEnum().getEnumStrValues());
      currItem.setXWidgetName(String.format("XCombo(%s)", optionsStr));
      return this;
   }

   private void setAttrTypeSettings(AttributeTypeToken attrType) {
      currItem.setName(attrType.getUnqualifiedName());
      currItem.setStoreName(attrType.getName());
      if (Strings.isValid(attrType.getDescription())) {
         currItem.setToolTip(attrType.getDescription());
      }
   }

   public XWidgetBuilder andXCombo(String displayName, List<String> options) {
      newXWidget();
      currItem.setName(displayName);
      String optionsStr = Collections.toString(",", options);
      currItem.setXWidgetName(String.format("XCombo(%s)", optionsStr));
      return this;
   }

   public XWidgetBuilder andXText(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName("XText");
      return this;
   }

   public XWidgetBuilder andXDate(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName("XDate");
      return this;
   }

   public XWidgetBuilder andXText(String displayName) {
      newXWidget();
      currItem.setName(displayName);
      currItem.setXWidgetName("XText");
      return this;
   }

   public XWidgetBuilder andComposite(int columns) {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setBeginComposite(columns);
      return this;
   }

   public XWidgetBuilder andHorizLabel() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setHorizontalLabel(true);
      return this;
   }

   public XWidgetBuilder endComposite() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setEndComposite(true);
      return this;
   }
}
