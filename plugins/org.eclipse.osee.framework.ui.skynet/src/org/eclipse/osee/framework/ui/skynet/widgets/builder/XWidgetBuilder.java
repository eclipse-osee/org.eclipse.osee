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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;
import org.eclipse.osee.framework.ui.skynet.widgets.ISelectableValueProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnumAttr;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Donald G. Dunne
 */
public class XWidgetBuilder {

   boolean ended = false;
   private final List<XWidgetRendererItem> datas = new LinkedList<XWidgetRendererItem>();
   private XWidgetRendererItem currItem;

   public XWidgetBuilder andRequired() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.getXOptionHandler().add(XOption.REQUIRED);
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
      currItem.getXOptionHandler().add(XOption.SINGLE_SELECT);
      currItem.setName(displayName);
      currItem.setXWidgetName(widgetType);
      return this;
   }

   public XWidgetBuilder andWidget(AttributeTypeToken attrType, String widgetType) {
      newXWidget();
      currItem.getXOptionHandler().add(XOption.SINGLE_SELECT);
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName(widgetType);
      return this;
   }

   public List<XWidgetRendererItem> getItems() {
      if (currItem != null) {
         throw new OseeArgumentException("Can't get items without calling endWidget() on widget [%s]",
            currItem.getName());
      }
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

   public XWidgetBuilder andXCombo(AttributeTypeToken attrType, List<String> options) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setName(attrType.getUnqualifiedName());
      String optionsStr = Collections.toString(",", options);
      currItem.setXWidgetName(String.format("XCombo(%s)", optionsStr));
      return this;
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
      currItem.setAttributeType(attrType);
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

   public XWidgetBuilder andXInteger(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName("XInteger");
      return this;
   }

   public XWidgetBuilder andXText(String displayName, AttributeTypeString attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setName(displayName);
      currItem.setXWidgetName("XText");
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

   public XWidgetBuilder andComposite(int columns, boolean border) {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setBeginComposite(columns, border);
      return this;
   }

   public XWidgetBuilder andHorizLabel() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setHorizontalLabel(true);
      currItem.setFillVertically(false);
      return this;
   }

   public XWidgetBuilder endComposite() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setEndComposite(true);
      return this;
   }

   public XWidgetBuilder andDefault(Object value) {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.setDefaultValueObj(value);
      return this;
   }

   public XWidgetBuilder andCheckbox(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName("XCheckBox");
      return this;
   }

   public XWidgetBuilder andXCheckbox(String name) {
      newXWidget();
      currItem.setName(name);
      currItem.setXWidgetName("XCheckBox");
      return this;
   }

   public XWidgetBuilder andXCheckBoxThreeState(AttributeTypeBoolean attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName("XCheckBoxThreeState");
      return this;
   }

   public XWidgetBuilder andLabelAfter() {
      Conditions.assertNotNull(currItem, "currItem");
      currItem.getXOptionHandler().add(XOption.LABEL_AFTER);
      return this;
   }

   public XWidgetBuilder andXHyperlinkActionableItemActive() {
      newXWidget();
      currItem.setName("Actionable Item(s)");
      currItem.setXWidgetName("XHyperlabelActionableItemSelection");
      return this;
   }

   public XWidgetBuilder andXHyperlinkActionableItemActive(boolean singleSelect) {
      newXWidget();
      currItem.getXOptionHandler().add(XOption.SINGLE_SELECT);
      currItem.setName("Actionable Item(s)");
      currItem.setXWidgetName("XHyperlabelActionableItemSelection");
      return this;
   }

   public XWidgetBuilder andXActionableItem() {
      newXWidget();
      currItem.setXWidgetName("XActionableItemWidget");
      return this;
   }

   public XWidgetBuilder andXRadioBooleanTriState(String name) {
      newXWidget();
      currItem.setXWidgetName(XRadioButtonsBooleanTriState.class.getSimpleName());
      currItem.setName(name);
      return this;
   }

   public XWidgetBuilder andXButton(String name) {
      newXWidget();
      currItem.setXWidgetName("XButton");
      currItem.setName(name);
      return this;
   }

   public XWidgetBuilder andXButtonPush(String name) {
      newXWidget();
      currItem.setXWidgetName("XButtonPush");
      currItem.setName(name);
      return this;
   }

   public XWidgetBuilder andXLabel(String name) {
      newXWidget();
      currItem.setXWidgetName("XLabel");
      currItem.setName(name);
      return this;
   }

   public XWidgetBuilder andNewLine() {
      andXLabel("  ").endWidget();
      return this;
   }

   public XWidgetBuilder andValueProvider(ISelectableValueProvider provider) {
      currItem.setValueProvider(provider);
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnum(String name, Collection<String> values) {
      newXWidget();
      currItem.setXWidgetName(XHyperlinkWfdForEnum.class.getSimpleName());
      currItem.setName(name);
      currItem.setValues(values);
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnumAttr(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName(XHyperlinkWfdForEnumAttr.class.getSimpleName());
      currItem.setName(attrType.getUnqualifiedName());
      return this;
   }

   public XWidgetBuilder andXBoolean(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      currItem.setXWidgetName(XCheckBox.class.getSimpleName());
      currItem.setName(attrType.getUnqualifiedName());
      return this;
   }

   public XWidgetBuilder andXHyperLinkDate(String name) {
      newXWidget();
      currItem.setName(name);
      currItem.setXWidgetName(XHyperlinkLabelDate.class.getSimpleName());
      return this;
   }

   public XWidgetBuilder andXHyperlinkTriStateBoolean(String name) {
      newXWidget();
      currItem.setXWidgetName("XHyperlinkTriStateBoolean");
      currItem.setName(name);
      return this;
   }

   public XWidgetBuilder andXHyperlinkLabelValueStringSel(String name) {
      newXWidget();
      currItem.setXWidgetName("XHyperlinkLabelValueStringSel");
      currItem.setName(name);
      return this;
   }

   public XWidgetBuilder andChangeType(List<ChangeTypes> changeTypes) {
      newXWidget();
      currItem.setName("Change Type");
      currItem.setXWidgetName("XHyperlinkChangeTypeSelection");
      String string = Collections.toString(";", changeTypes);
      currItem.addParameter("ChangeType", string);
      return this;
   }

   public XWidgetBuilder andChangeType() {
      newXWidget();
      currItem.setName("Change Type");
      currItem.setXWidgetName("XHyperlinkChangeTypeSelection");
      String string = Collections.toString(";", ChangeTypes.DEFAULT_CHANGE_TYPES);
      currItem.addParameter("ChangeType", string);
      return this;
   }

   public XWidgetBuilder andPriority() {
      newXWidget();
      currItem.setName("Priority");
      currItem.setXWidgetName("XHyperlinkPrioritySelection");
      String string = Collections.toString(";", Priorities.DEFAULT_PRIORITIES);
      currItem.addParameter("Priority", string);
      return this;
   }

   public XWidgetBuilder andPriority(List<Priorities> priorities) {
      newXWidget();
      currItem.setName("Priority");
      currItem.setXWidgetName("XHyperlinkPrioritySelection");
      String string = Collections.toString(";", priorities);
      currItem.addParameter("Priority", string);
      return this;
   }

   public XWidgetBuilder andDisplayLabel(boolean b) {
      currItem.getXOptionHandler().add(XOption.NO_LABEL);
      return this;
   }

   public XWidgetBuilder andTeamId(ArtifactId id) {
      currItem.setTeamId(id);
      return this;
   }

   public XWidgetBuilder andWidgetHint(WidgetHint... widgetHints) {
      for (WidgetHint hint : widgetHints) {
         currItem.addWidgetHint(hint);
      }
      return this;
   }

   public XWidgetBuilder andEnumeratedArtifactWidget(String displayName, String widgetType,
      ArtifactToken artifactToken) {
      andWidget(displayName, widgetType);
      currItem.setEnumeratedArt(artifactToken);
      return this;
   }

   public XWidgetBuilder andXHyperLinkMember(AttributeTypeToken user) {
      newXWidget();
      currItem.setName(user.getUnqualifiedName());
      currItem.setXWidgetName("XHyperlabelMemberSelection");
      return this;
   }

}
