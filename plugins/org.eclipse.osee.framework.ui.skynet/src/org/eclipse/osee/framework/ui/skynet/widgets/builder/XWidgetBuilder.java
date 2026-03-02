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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
public class XWidgetBuilder {

   boolean ended = false;
   private final List<XWidgetData> widDatas = new LinkedList<XWidgetData>();
   protected XWidgetData widData;
   private BranchQueryBuilder branchBuilder;

   public XWidgetBuilder andRequired() {
      Conditions.assertNotNull(widData, "currItem");
      widData.add(XOption.REQUIRED);
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

   public XWidgetBuilder andWidget(String displayName, String widgetName) {
      WidgetId widgetId = WidgetId.getByName(widgetName);
      if (widgetId.isInvalid()) {
         throw new OseeArgumentException("Can not find Widget for [%s]", widgetName);
      }
      return andWidget(displayName, widgetId);
   }

   public XWidgetBuilder andWidget(String displayName, WidgetId widgetId) {
      newXWidget();
      widData.add(XOption.SINGLE_SELECT);
      widData.setName(displayName);
      widData.setWidgetId(widgetId);
      return this;
   }

   public XWidgetBuilder andWidget(AttributeTypeToken attrType, WidgetId widgetId) {
      newXWidget();
      widData.add(XOption.SINGLE_SELECT);
      setAttrTypeSettings(attrType);
      widData.setWidgetId(widgetId);
      return this;
   }

   public XWidgetBuilder andWidget(String label, AttributeTypeString attrType, WidgetId widgetId) {
      andWidget(attrType, widgetId);
      widData.setName(label);
      return this;
   }

   public XWidgetBuilder andWidget(WidgetId widgetId) {
      newXWidget();
      widData.setWidgetId(widgetId);
      return this;
   }

   public List<XWidgetData> getXWidgetDatas() {
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

   public XWidgetBuilder andFillHoriz() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setFillHorzontally(true);
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
      widData.setWidgetId(WidgetId.XComboWidget);
      widData.setSelectable(options);
      return this;
   }

   public XWidgetBuilder andXCombo(String displayName, AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setName(displayName);
      widData.setWidgetId(WidgetId.XComboWidget);
      widData.setSelectable(attrType.toEnum().getEnumStrValues());
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
      widData.setWidgetId(WidgetId.XComboWidget);
      widData.setSelectable(options);
      return this;
   }

   public XWidgetBuilder andXInteger(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XIntegerWidget);
      return this;
   }

   public XWidgetBuilder andXText(String displayName, AttributeTypeString attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setName(displayName);
      widData.setWidgetId(WidgetId.XTextWidget);
      return this;
   }

   public XWidgetBuilder andXText(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XTextWidget);
      return this;
   }

   public XWidgetBuilder andXDate(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XDateWidget);
      return this;
   }

   public XWidgetBuilder andXText(String displayName) {
      newXWidget();
      widData.setName(displayName);
      widData.setWidgetId(WidgetId.XTextWidget);
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
      widData.setDefaultValue(value);
      return this;
   }

   public XWidgetBuilder andCheckbox(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XCheckBoxWidget);
      andLabelAfter().andHorizLabel();
      return this;
   }

   public XWidgetBuilder andXCheckbox(String name) {
      newXWidget();
      widData.setName(name);
      widData.setWidgetId(WidgetId.XCheckBoxWidget);
      andLabelAfter().andHorizLabel();
      return this;
   }

   public XWidgetBuilder andXCheckBoxThreeState(AttributeTypeBoolean attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XCheckBoxWidget);
      andLabelAfter().andHorizLabel();
      return this;
   }

   public XWidgetBuilder andLabelAfter() {
      Conditions.assertNotNull(widData, "currItem");
      widData.add(XOption.LABEL_AFTER);
      return this;
   }

   public XWidgetBuilder andXRadioBooleanTriState(String name) {
      newXWidget();
      widData.setWidgetId(WidgetId.XRadioButtonsBooleanTriStateWidget);
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXButton(String name) {
      newXWidget();
      widData.setWidgetId(WidgetId.XButtonWidget);
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXButtonPush(String name) {
      newXWidget();
      widData.setWidgetId(WidgetId.XButtonPushWidget);
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andXLabel(String name) {
      newXWidget();
      widData.setWidgetId(WidgetId.XLabelWidget);
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
      widData.setWidgetId(WidgetId.XXStringsSelWidget);
      widData.setName(name);
      widData.setValues(Collections.castAll(values));
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnumAttr(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XXStringsSelWidget);
      widData.setName(attrType.getUnqualifiedName());
      return this;
   }

   public XWidgetBuilder andXBoolean(AttributeTypeToken attrType) {
      newXWidget();
      setAttrTypeSettings(attrType);
      widData.setWidgetId(WidgetId.XCheckBoxWidget);
      widData.setName(attrType.getUnqualifiedName());
      return this;
   }

   public XWidgetBuilder andXHyperLinkDate(String name) {
      newXWidget();
      widData.setName(name);
      widData.setWidgetId(WidgetId.XHyperlinkLabelDateWidget);
      return this;
   }

   public XWidgetBuilder andXHyperlinkTriStateBoolean(String name) {
      newXWidget();
      widData.setWidgetId(WidgetId.XHyperlinkTriStateBooleanWidget);
      widData.setName(name);
      return this;
   }

   public XWidgetBuilder andDisplayLabel(boolean b) {
      widData.add(XOption.NO_LABEL);
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

   public XWidgetBuilder andEnumeratedArtifactWidget(String displayName, WidgetId widgetId,
      ArtifactToken artifactToken) {
      andWidget(displayName, widgetId);
      widData.setEnumeratedArt(artifactToken);
      return this;
   }

   public XWidgetBuilder andXHyperLinkEnumumeratedArt(AttributeTypeString attrType, ArtifactToken enumeratedArtifact) {
      newXWidget();
      widData.setName(attrType.getUnqualifiedName());
      widData.setWidgetId(WidgetId.XHyperlinkArtEnumeratedWidget);
      widData.setAttributeType(attrType);
      widData.setEnumeratedArt(enumeratedArtifact);
      return this;
   }

   public XWidgetBuilder andSpace() {
      newXWidget();
      widData.setName("     ");
      widData.setWidgetId(WidgetId.XLabelWidget);
      return this;
   }

   public XWidgetBuilder andParameter(String key, Object obj) {
      widData.getParameters().put(key, obj);
      return this;
   }

   public XWidgetBuilder andSingleSelect() {
      widData.add(XOption.SINGLE_SELECT);
      return this;
   }

   public XWidgetBuilder andMultiSelect() {
      widData.add(XOption.MULTI_SELECT);
      return this;
   }

   public XWidgetBuilder andEnumeratedArt(ArtifactToken enumeratedArt) {
      widData.setEnumeratedArt(enumeratedArt);
      return this;
   }

   public BranchQueryBuilder andBranchQuery() {
      if (branchBuilder == null) {
         branchBuilder = new BranchQueryBuilder(widData, this);
      }
      return branchBuilder;
   }

   public XWidgetBuilder andWidget(AttributeTypeString attrType, String widgetName) {
      WidgetId widgetId = WidgetId.getByName(widgetName);
      if (widgetId.isInvalid()) {
         throw new OseeArgumentException("Can not find Widget for [%s]", widgetName);
      }
      return andWidget(attrType, widgetId);
   }

   public XWidgetBuilder andSelectable(Object... selectables) {
      for (Object selectable : selectables) {
         widData.getSelectable().add(selectable.toString());
      }
      return this;
   }

   public XWidgetBuilder andBranchSelWidget() {
      andBranchSelWidget("Branch");
      return this;
   }

   public XWidgetBuilder andBranchSelWidget(String label) {
      andWidget(label, WidgetId.XBranchSelectWidget);
      return this;
   }

   public XWidgetBuilder andArtifactTypeWidget() {
      andArtifactTypeWidget("Artifact Type");
      return this;
   }

   public XWidgetBuilder andArtifactTypeWidget(String label) {
      andWidget(label, WidgetId.XArtifactTypeSelectionWidget);
      return this;
   }

   public XWidgetBuilder andAttributeTypeWidget() {
      andAttributeTypeWidget("Attribute Type");
      return this;
   }

   public XWidgetBuilder andAttributeTypeWidget(String label) {
      andWidget(label, WidgetId.XAttributeTypeSelectionWidget);
      return this;
   }

   public XWidgetBuilder andXHyperlinkBranchSelWidget() {
      andXHyperlinkBranchSelWidget("Branch");
      return this;
   }

   public XWidgetBuilder andXHyperlinkBranchSelWidget(String label) {
      andWidget(label, WidgetId.XHyperlinkWfdBranchSelWidget);
      return this;
   }

   public XWidgetBuilder andXHyperlinkBranchViewSelWidget() {
      andWidget("Branch View", WidgetId.XHyperlinkWfdBranchViewSelWidget);
      return this;
   }

   public XWidgetBuilder andValues(Object... values) {
      for (Object value : values) {
         widData.getValues().add(value);
      }
      return this;
   }

   public XWidgetBuilder andXHyperlinkBranchAndViewSelWidget() {
      andWidget("Branch With View Selection", WidgetId.XHyperlinkWfdBranchAndViewSelWidget);
      return this;
   }

   public void andXHyperlinkBranchAndViewSelWidget(String label) {
      andWidget(label, WidgetId.XHyperlinkWfdBranchAndViewSelWidget);
   }

   public XWidgetBuilder andId(long id) {
      widData.setId(id);
      return this;
   }

   public XWidgetBuilder andNotEdit() {
      Conditions.assertNotNull(widData, "currItem");
      widData.add(XOption.NOT_EDITABLE);
      return this;
   }

   public XWidgetBuilder noClear() {
      widData.add(XOption.NOT_CLEARABLE);
      return this;
   }

}
