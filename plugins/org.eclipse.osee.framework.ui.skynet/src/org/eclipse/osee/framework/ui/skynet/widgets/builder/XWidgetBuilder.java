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
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnum;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnumAttr;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState;

/**
 * @author Donald G. Dunne
 */
public class XWidgetBuilder {

   boolean ended = false;
   private final List<XWidgetData> widDatas = new LinkedList<XWidgetData>();
   private XWidgetData widData;

   public XWidgetBuilder andRequired() {
      Conditions.assertNotNull(widData, "currItem");
      widData.getXOptionHandler().add(XOption.REQUIRED);
      return this;
   }

   public XWidgetBuilder endWidget() {
      widData = null;
      return this;
   }

   public void newXWidget() {
      widData = new XWidgetData();
      widDatas.add(widData);
   }

   public XWidgetBuilder andWidget(String displayName, String widgetType) {
      newXWidget();
      widData.getXOptionHandler().add(XOption.SINGLE_SELECT);
      widData.setName(displayName);
      widData.setXWidgetName(widgetType);
      return this;
   }

   public XWidgetBuilder andWidget(AttributeTypeToken attrType, String widgetType) {
      newXWidget();
      widData.getXOptionHandler().add(XOption.SINGLE_SELECT);
      setAttrTypeSettings(attrType);
      widData.setXWidgetName(widgetType);
      return this;
   }

   public List<XWidgetData> getXWidgetDatas() {
      if (widData != null) {
         throw new OseeArgumentException("Can't get items without calling endWidget() on widget [%s]",
            widData.getName());
      }
      return widDatas;
   }

   public XWidgetBuilder andHeight(int height) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setFillVertically(true);
      widData.setHeight(height);
      return this;
   }

   public XWidgetBuilder andFillVertically() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setFillVertically(true);
      return this;
   }

   public XWidgetBuilder andToolTip(AttributeTypeString attrType) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setToolTip(attrType.getDescription());
      return this;
   }

   public XWidgetBuilder andToolTip(String toolTip) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setToolTip(toolTip);
      return this;
   }

   public XWidgetBuilder andXCombo(AttributeTypeToken attrType) {
      return andXCombo(attrType.getUnqualifiedName(), attrType);

   }

   public XWidgetBuilder andXCombo(AttributeTypeToken attrType, List<String> options) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setName(attrType.getUnqualifiedName());
      String optionsStr = Collections.toString(",", options);
      widData.setXWidgetName(String.format("XCombo(%s)", optionsStr));
      return this;
   }

   public XWidgetBuilder andXCombo(String displayName, AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setName(displayName);
      String optionsStr = Collections.toString(",", attrType.toEnum().getEnumStrValues());
      widData.setXWidgetName(String.format("XCombo(%s)", optionsStr));
      return this;
   }

   private void setAttrTypeSettings(AttributeTypeToken attrType) {
      widData.setName(attrType.getUnqualifiedName());
      widData.setStoreName(attrType.getName());
      widData.setAttributeType(attrType);
      if (Strings.isValid(attrType.getDescription())) {
         widData.setToolTip(attrType.getDescription());
      }
   }

   public XWidgetBuilder andXCombo(String displayName, List<String> options) {
      newXWidget();
      widData.setName(displayName);
      String optionsStr = Collections.toString(",", options);
      widData.setXWidgetName(String.format("XCombo(%s)", optionsStr));
      return this;
   }

   public XWidgetBuilder andXInteger(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName("XInteger");
      return this;
   }

   public XWidgetBuilder andXText(String displayName, AttributeTypeString attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setName(displayName);
      widData.setXWidgetName("XText");
      return this;
   }

   public XWidgetBuilder andXText(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName("XText");
      return this;
   }

   public XWidgetBuilder andXDate(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName("XDate");
      return this;
   }

   public XWidgetBuilder andXText(String displayName) {
      newXWidget();
      widData.setName(displayName);
      widData.setXWidgetName("XText");
      return this;
   }

   public XWidgetBuilder andComposite(int columns) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setBeginComposite(columns);
      return this;
   }

   public XWidgetBuilder andComposite(int columns, boolean border) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setBeginComposite(columns, border);
      return this;
   }

   public XWidgetBuilder andHorizLabel() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setHorizontalLabel(true);
      widData.setFillVertically(false);
      return this;
   }

   public XWidgetBuilder endComposite() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setEndComposite(true);
      return this;
   }

   public XWidgetBuilder andDefault(Object value) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setDefaultValueObj(value);
      return this;
   }

   public XWidgetBuilder andCheckbox(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName("XCheckBox");
      return this;
   }

   public XWidgetBuilder andXCheckbox(String name) {
      newXWidget();
      widData.setName(name);
      widData.setXWidgetName("XCheckBox");
      return this;
   }

   public XWidgetBuilder andXCheckBoxThreeState(AttributeTypeBoolean attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName("XCheckBoxThreeState");
      return this;
   }

   public XWidgetBuilder andLabelAfter() {
      Conditions.assertNotNull(widData, "currItem");
      widData.getXOptionHandler().add(XOption.LABEL_AFTER);
      return this;
   }

   public XWidgetBuilder andXHyperlinkActionableItemActive() {
      newXWidget();
      widData.setName("Actionable Item(s)");
      widData.setXWidgetName("XHyperlabelActionableItemSelection");
      return this;
   }

   public XWidgetBuilder andXHyperlinkActionableItemActive(boolean singleSelect) {
      newXWidget();
      widData.getXOptionHandler().add(XOption.SINGLE_SELECT);
      widData.setName("Actionable Item(s)");
      widData.setXWidgetName("XHyperlabelActionableItemSelection");
      return this;
   }

   public XWidgetBuilder andXActionableItem() {
      newXWidget();
      widData.setXWidgetName("XActionableItemWidget");
      return this;
   }

   public XWidgetBuilder andXRadioBooleanTriState(String name) {
      newXWidget();
      widData.setXWidgetName(XRadioButtonsBooleanTriState.class.getSimpleName());
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXButton(String name) {
      newXWidget();
      widData.setXWidgetName("XButton");
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXButtonPush(String name) {
      newXWidget();
      widData.setXWidgetName("XButtonPush");
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXLabel(String name) {
      newXWidget();
      widData.setXWidgetName("XLabel");
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andNewLine() {
      andXLabel("  ").endWidget();
      return this;
   }

   public XWidgetBuilder andValueProvider(ISelectableValueProvider provider) {
      widData.setValueProvider(provider);
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnum(String name, Collection<String> values) {
      newXWidget();
      widData.setXWidgetName(XHyperlinkWfdForEnum.class.getSimpleName());
      widData.setName(name);
      widData.setValues(values);
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnumAttr(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName(XHyperlinkWfdForEnumAttr.class.getSimpleName());
      widData.setName(attrType.getUnqualifiedName());
      return this;
   }

   public XWidgetBuilder andXBoolean(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setXWidgetName(XCheckBox.class.getSimpleName());
      widData.setName(attrType.getUnqualifiedName());
      return this;
   }

   public XWidgetBuilder andXHyperLinkDate(String name) {
      newXWidget();
      widData.setName(name);
      widData.setXWidgetName(XHyperlinkLabelDate.class.getSimpleName());
      return this;
   }

   public XWidgetBuilder andXHyperlinkTriStateBoolean(String name) {
      newXWidget();
      widData.setXWidgetName("XHyperlinkTriStateBoolean");
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXHyperlinkLabelValueStringSel(String name) {
      newXWidget();
      widData.setXWidgetName("XHyperlinkLabelValueStringSel");
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andChangeType(List<ChangeTypes> changeTypes) {
      newXWidget();
      widData.setName("Change Type");
      widData.setXWidgetName("XHyperlinkChangeTypeSelection");
      String string = Collections.toString(";", changeTypes);
      widData.addParameter("ChangeType", string);
      return this;
   }

   public XWidgetBuilder andChangeType() {
      newXWidget();
      widData.setName("Change Type");
      widData.setXWidgetName("XHyperlinkChangeTypeSelection");
      String string = Collections.toString(";", ChangeTypes.DEFAULT_CHANGE_TYPES);
      widData.addParameter("ChangeType", string);
      return this;
   }

   public XWidgetBuilder andCogPriority() {
      newXWidget();
      widData.setName("COG Priority");
      widData.setXWidgetName("XHyperlinkLabelEnumeratedArt");
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnumumeratedArt(AttributeTypeString attrType, ArtifactToken enumeratedArtifact) {
      newXWidget();
      widData.setName(attrType.getUnqualifiedName());
      widData.setXWidgetName("XHyperlinkLabelEnumeratedArt");
      widData.setAttributeType(attrType);
      widData.setEnumeratedArt(enumeratedArtifact);
      return this;
   }

   public XWidgetBuilder andPriority() {
      newXWidget();
      widData.setName("Priority");
      widData.setXWidgetName("XHyperlinkPrioritySelection");
      String string = Collections.toString(";", Priorities.DEFAULT_PRIORITIES);
      widData.addParameter("Priority", string);
      return this;
   }

   public XWidgetBuilder andPriority(List<Priorities> priorities) {
      newXWidget();
      widData.setName("Priority");
      widData.setXWidgetName("XHyperlinkPrioritySelection");
      String string = Collections.toString(";", priorities);
      widData.addParameter("Priority", string);
      return this;
   }

   public XWidgetBuilder andDisplayLabel(boolean b) {
      widData.getXOptionHandler().add(XOption.NO_LABEL);
      return this;
   }

   public XWidgetBuilder andTeamId(ArtifactId id) {
      widData.setTeamId(id);
      return this;
   }

   public XWidgetBuilder andWidgetHint(WidgetHint... widgetHints) {
      for (WidgetHint hint : widgetHints) {
         widData.addWidgetHint(hint);
      }
      return this;
   }

   public XWidgetBuilder andEnumeratedArtifactWidget(String displayName, String widgetType,
      ArtifactToken artifactToken) {
      andWidget(displayName, widgetType);
      widData.setEnumeratedArt(artifactToken);
      return this;
   }

   public XWidgetBuilder andXHyperLinkMember(AttributeTypeToken user) {
      newXWidget();
      widData.setName(user.getUnqualifiedName());
      widData.setXWidgetName("XHyperlabelMemberSelection");
      return this;
   }

   public XWidgetBuilder andSpace() {
      newXWidget();
      widData.setName("     ");
      widData.setXWidgetName("XLabel");
      return this;
   }

   public XWidgetBuilder andParameter(String key, Object obj) {
      widData.getParameters().put(key, obj);
      return this;
   }

   public XWidgetBuilder andSingleSelect() {
      widData.getXOptionHandler().add(XOption.SINGLE_SELECT);
      return this;
   }

   public XWidgetBuilder andMultiSelect() {
      widData.getXOptionHandler().add(XOption.MULTI_SELECT);
      return this;
   }

   public XWidgetBuilder andEnumeratedArt(ArtifactToken enumeratedArt) {
      widData.setEnumeratedArt(enumeratedArt);
      return this;
   }

}
