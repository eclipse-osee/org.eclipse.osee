/*********************************************************************
 * Copyright (c) 2021, 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.builder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetService;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.widgets.Listener;

/**
 * Fluent builder for constructing XWidgetData lists used by Blams and other UI components.
 *
 * @author Donald G. Dunne
 */
public class XWidgetBuilder {

   boolean ended = false;
   private final List<XWidgetData> widDatas = new LinkedList<XWidgetData>();
   protected XWidgetData widData;
   private BranchQueryBuilder branchBuilder;

   // =========================================================================
   // Constructors
   // =========================================================================

   public XWidgetBuilder() {
   }

   // =========================================================================
   // Core andWidget constructors
   // =========================================================================

   public XWidgetBuilder andWidget(WidgetId widgetId) {
      XWidget.debugXWidgets.syserr("XWidgetBuilder:  WidgetId: [%s]\n", widgetId.getName());
      // Instantiation of XWidget will create widData
      // Construct while building so we can use widget constructor to set widData values first
      XWidget xWidget = XWidgetService.get().createXWidget(widgetId);
      // Get widData from xWidget which now has stuff from constructor
      widData = xWidget.getWidData();
      // Store off widget so don't re-create, now it just needs to be laid out
      widData.setXWidget(xWidget);
      if (Strings.isInvalid(widData.getLabel()) && Strings.isValid(xWidget.getLabel())) {
         widData.setLabel(xWidget.getLabel());
      }

      widDatas.add(widData);
      return this;
   }

   public XWidgetBuilder andWidget(String label, WidgetId widgetId) {
      andWidget(widgetId);
      widData.setLabel(label);
      return this;
   }

   public XWidgetBuilder andWidget(long id, String label, WidgetId widgetId) {
      this.andWidget(label, widgetId);
      widData.setId(id);
      return this;
   }

   public XWidgetBuilder andWidget(String label, AttributeTypeToken attrType, WidgetId widgetId) {
      andWidget(attrType, widgetId);
      widData.setLabel(label);
      return this;
   }

   public XWidgetBuilder andWidget(AttributeTypeToken attrType, WidgetId widgetId) {
      andWidget(widgetId);
      setAttrTypeSettings(attrType);
      return this;
   }

   public XWidgetBuilder endWidget() {
      widData = null;
      return this;
   }

   // =========================================================================
   // Text widgets
   // =========================================================================

   public XWidgetBuilder andTextWidget(String label, AttributeTypeString attrType) {
      andWidget(label, attrType, WidgetId.XXTextWidget);
      return this;
   }

   public XWidgetBuilder andTextWidget(String label) {
      andWidget(label, WidgetId.XXTextWidget);
      return this;
   }

   public XWidgetBuilder andTextWidget(AttributeTypeToken attrType) {
      andWidget(attrType, WidgetId.XXTextWidget);
      return this;
   }

   // =========================================================================
   // Numeric widgets
   // =========================================================================

   public XWidgetBuilder andIntegerWidget(AttributeTypeToken attrType) {
      andWidget(attrType, WidgetId.XXIntegerSTextWidget);
      return this;
   }

   public XWidgetBuilder andPercentCompleteWidget(AttributeTypeInteger attrType) {
      return andPercentCompleteWidget(attrType.getUnqualifiedName(), attrType);
   }

   public XWidgetBuilder andPercentCompleteWidget(String label, AttributeTypeInteger attrType) {
      andWidget(label, attrType, WidgetId.XXPercentSTextWidget);
      return this;
   }

   public XWidgetBuilder andDoubleWidget(AttributeTypeDouble attrType) {
      return andDoubleWidget(attrType.getUnqualifiedName(), attrType);
   }

   public XWidgetBuilder andDoubleWidget(String label, AttributeTypeDouble attrType) {
      andWidget(label, attrType, WidgetId.XXDoubleSTextWidget);
      return this;
   }

   // =========================================================================
   // Date widgets
   // =========================================================================

   public XWidgetBuilder andDateWidget(AttributeTypeToken attrType) {
      andWidget(attrType, WidgetId.XXDateSelWidget);
      return this;
   }

   public XWidgetBuilder andDateWidget(String label) {
      andWidget(WidgetId.XXDateSelWidget);
      andLabel(label);
      return this;
   }

   // =========================================================================
   // File / Dir Selection widgets
   // =========================================================================

   public XWidgetBuilder andFileSelWidget(String label) {
      andWidget(label, WidgetId.XXFileSelWidget);
      return this;
   }

   public XWidgetBuilder andFileSelWidget() {
      andWidget(XWidgetLabels.SELECT_FILE, WidgetId.XXFileSelWidget);
      return this;
   }

   public XWidgetBuilder andDirSelWidget(String label) {
      andWidget(label, WidgetId.XXDirSelWidget);
      return this;
   }

   public XWidgetBuilder andDirSelWidget() {
      andWidget(XWidgetLabels.SELECT_DIR, WidgetId.XXDirSelWidget);
      return this;
   }

   // =========================================================================
   // List Drop widgets
   // =========================================================================

   public XWidgetBuilder andListDropWidget(String label) {
      andWidget(label, WidgetId.XXListArtifactDropWidget);
      return this;
   }

   // =========================================================================
   // Checkbox / Boolean widgets
   // =========================================================================

   public XWidgetBuilder andCheckBoxWidget(String label) {
      andWidget(label, WidgetId.XXCheckBoxWidget);
      return this;
   }

   public XWidgetBuilder andCheckBoxWidget(AttributeTypeToken attrType) {
      andWidget(attrType, WidgetId.XXCheckBoxWidget);
      return this;
   }

   /**
    * Three state Yes, No, UnSet
    */
   public XWidgetBuilder andBooleanWidget(AttributeTypeToken attrType) {
      andWidget(attrType, WidgetId.XXBooleanWidget);
      return this;
   }

   /**
    * Three state Yes, No, UnSet
    */
   public XWidgetBuilder andBooleanWidget(String label) {
      andWidget(label, WidgetId.XXBooleanWidget);
      return this;
   }

   // =========================================================================
   // String / Enum selection widgets
   // =========================================================================

   public XWidgetBuilder andStringsSelWidget(String label, AttributeTypeToken attrType) {
      andWidget(label, attrType, WidgetId.XXStringsWidget);
      widData.setSelectable(attrType.toEnum().getEnumStrValues());
      return this;
   }

   public XWidgetBuilder andStringsSelWidget(String label, Collection<String> selectable) {
      andWidget(label, WidgetId.XXStringsWidget);
      widData.setSelectable(Collections.castAll(selectable));
      return this;
   }

   public XWidgetBuilder andStringsSelWidget(String label) {
      andWidget(label, WidgetId.XXStringsWidget);
      return this;
   }

   public XWidgetBuilder andEnumAttrWidget(AttributeTypeToken attrType) {
      andWidget(attrType, WidgetId.XXStringsWidget);
      return this;
   }

   public XWidgetBuilder andEnumArtWidget(String label, WidgetId widgetId, ArtifactToken artifactToken) {
      andWidget(label, widgetId);
      widData.setEnumeratedArt(artifactToken);
      return this;
   }

   public XWidgetBuilder andEnumArtWidget(AttributeTypeString attrType, ArtifactToken enumeratedArtifact) {
      andWidget(attrType, WidgetId.XXStringsEnumArtWidget);
      widData.setEnumeratedArt(enumeratedArtifact);
      return this;
   }

   // =========================================================================
   // Label / Space widgets
   // =========================================================================

   /**
    * Label-only widget (XXLabelWidget). For a label + value use {@link #andLabelValueWidget(String, String)}.
    */
   public XWidgetBuilder andLabelWidget(String label) {
      andWidget(label, WidgetId.XXLabelWidget);
      return this;
   }

   /**
    * Label + value widget (XXLabelValueWidget): bold label with an unbold value.
    */
   public XWidgetBuilder andLabelValueWidget(String label, String value) {
      return andLabelValueWidget(Lib.generateId(), label, value);
   }

   public XWidgetBuilder andLabelValueWidget(long id, String label, String value) {
      andWidget(label, WidgetId.XXLabelValueWidget);
      widData.setDefaultValue(value);
      widData.setId(id);
      widData.set(XOption.READ);
      return this;
   }

   public XWidgetBuilder andSpaceWidget(int length) {
      andWidget(Lib.getSpace(length), WidgetId.XXLabelWidget);
      return this;
   }

   public XWidgetBuilder andSpaceWidget() {
      andWidget("    ", WidgetId.XXLabelWidget);
      return this;
   }

   public XWidgetBuilder andNewLineWidget() {
      andLabelWidget("  ");
      return this;
   }

   // =========================================================================
   // Button widgets
   // =========================================================================

   public XWidgetBuilder andButtonPushWidget(String label) {
      andWidget(label, WidgetId.XXButtonPushWidget);
      return this;
   }

   public XWidgetBuilder andCopyIdHyperlinkWidget() {
      andWidget("ID", WidgetId.XXCopyIdHyperlinkWidget);
      return this;
   }

   // =========================================================================
   // Branch widgets
   // =========================================================================

   public XWidgetBuilder andBranchSelWidget() {
      return andBranchSelWidget(XWidgetLabels.BRANCH);
   }

   public XWidgetBuilder andBranchSelWidget(String label) {
      andWidget(label, WidgetId.XXBranchSelWidget);
      return this;
   }

   public XWidgetBuilder andBranchViewSelWidget() {
      andWidget(XWidgetLabels.BRANCH_VIEW, WidgetId.XXBranchViewWidget);
      return this;
   }

   public XWidgetBuilder andBranchAndViewSelWidget() {
      andWidget("Branch With View Selection", WidgetId.XXBranchAndViewSelWidget);
      return this;
   }

   public void andBranchAndViewSelWidget(String label) {
      andWidget(label, WidgetId.XXBranchAndViewSelWidget);
   }

   // =========================================================================
   // Type selection widgets
   // =========================================================================

   public XWidgetBuilder andArtifactTypeWidget() {
      andArtifactTypeWidget("Artifact Type");
      return this;
   }

   public XWidgetBuilder andArtifactTypeWidget(String label) {
      andWidget(label, WidgetId.XXArtifactTypeWidget);
      return this;
   }

   public XWidgetBuilder andAttributeTypeWidget() {
      andAttributeTypeWidget("Attribute Type");
      return this;
   }

   public XWidgetBuilder andAttributeTypeWidget(String label) {
      andWidget(label, WidgetId.XXAttributeTypeWidget);
      return this;
   }

   public XWidgetBuilder andAttributeTypesWidget() {
      andAttributeTypeWidget("Attribute Type(s)");
      return this;
   }

   public XWidgetBuilder andAttributeTypesWidget(String label) {
      andWidget(label, WidgetId.XXAttributeTypeWidget);
      and(XOption.VAL_MULTI_SEL);
      return this;
   }

   // =========================================================================
   // Attribute type settings
   // =========================================================================

   private void setAttrTypeSettings(AttributeTypeToken attrType) {
      widData.setLabel(attrType.getUnqualifiedName());
      widData.setAttributeType(attrType);
      if (Strings.isValid(attrType.getDescription())) {
         widData.setToolTip(attrType.getDescription());
      }
   }

   private void andLabel(String label) {
      widData.setLabel(label);
   }

   // =========================================================================
   // Widget options (layout, display, behavior)
   // =========================================================================

   public XWidgetBuilder andRequired() {
      Conditions.assertNotNull(widData, "currItem");
      widData.set(XOption.REQUIRED);
      return this;
   }

   public XWidgetBuilder andHeight(int height) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setFillVertically(true);
      widData.setHeight(height);
      return this;
   }

   public XWidgetBuilder fillVert() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setFillVertically(true);
      return this;
   }

   public XWidgetBuilder fillHoriz() {
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

   public XWidgetBuilder andHorizLabel() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setHorizontalLabel(true);
      widData.setFillVertically(false);
      return this;
   }

   public XWidgetBuilder andLabelAfter() {
      Conditions.assertNotNull(widData, "currItem");
      widData.set(XOption.LABEL_AFTER);
      return this;
   }

   public XWidgetBuilder andDefault(Object value) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setDefaultValue(value);
      return this;
   }

   public XWidgetBuilder noLabel() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.NO_LABEL);
      return this;
   }

   /**
    * Render label-only: do not create a value control. For widgets that are sometimes label-only and sometimes label +
    * value (e.g. XXRelatedWfeWidget navigation entries).
    */
   public XWidgetBuilder noValue() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.NO_VALUE);
      return this;
   }

   public XWidgetBuilder noColon() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.NO_COLON);
      return this;
   }

   public XWidgetBuilder noEdit() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.READ);
      widData.set(XOption.NO_CLEAR);
      return this;
   }

   /**
    * Disable the in-line edit through the StyledText widget. Set when you want to auto-save as the hyperlink will open
    * an edit dialog so you know when to validate and save.
    */
   public XWidgetBuilder noSTextEdit() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.NO_INLINE_EDIT);
      return this;
   }

   public XWidgetBuilder sTextBorder() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.BORDER);
      return this;
   }

   public XWidgetBuilder noClear() {
      Conditions.assertNotNull(widData, "widData");
      widData.set(XOption.NO_CLEAR);
      return this;
   }

   public XWidgetBuilder andValueSingleSelect() {
      widData.set(XOption.VAL_SINGLE_SEL);
      return this;
   }

   public XWidgetBuilder andValueMultiSelect() {
      widData.set(XOption.VAL_MULTI_SEL);
      return this;
   }

   public XWidgetBuilder fgLabelColor(int swtColor) {
      Conditions.assertNotNull(widData, "widData");
      widData.setFgLabelColor(swtColor);
      return this;
   }

   public XWidgetBuilder and(XOption... options) {
      widData.set(options);
      return this;
   }

   // =========================================================================
   // Composite / Layout
   // =========================================================================

   public XWidgetBuilder startComp(int columns) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setBeginComposite(columns);
      return this;
   }

   public XWidgetBuilder startComp(int columns, boolean border) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setBeginComposite(columns, border);
      return this;
   }

   public XWidgetBuilder beginComp(int cols) {
      Conditions.assertNotNull(widData, "currItem");
      widData.setBeginComposite(cols);
      return this;
   }

   public XWidgetBuilder endComp() {
      Conditions.assertNotNull(widData, "currItem");
      widData.setEndComposite(true);
      return this;
   }

   // =========================================================================
   // Data / Value configuration
   // =========================================================================

   public XWidgetBuilder andValueProvider(ISelectableValueProvider provider) {
      widData.setValueProvider(provider);
      return this;
   }

   public XWidgetBuilder andTeamId(ArtifactId id) {
      widData.setTeamId(id);
      return this;
   }

   public XWidgetBuilder andOseeImage(OseeImage image) {
      widData.setOseeImage(image);
      return this;
   }

   public XWidgetBuilder andParameter(String key, Object obj) {
      widData.getParameters().put(key, obj);
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

   public XWidgetBuilder andAttrType(AttributeTypeToken attrType) {
      widData.setAttributeType(attrType);
      return this;
   }

   public XWidgetBuilder andSelectable(Object... selectables) {
      List<Object> selList = new LinkedList<>();
      Collections.flatten(Collections.asList(selectables), selList);
      widData.setSelectable(selList);
      return this;
   }

   public XWidgetBuilder andValues(Object... values) {
      List<Object> valList = Collections.asList(values);
      widData.setValues(valList);
      return this;
   }

   /**
    * @param id - random id so Widget can be retrieved later
    */
   public XWidgetBuilder andId(long id) {
      Conditions.assertNotNull(widData, "widData");
      widData.setId(id);
      return this;
   }

   // =========================================================================
   // Listeners
   // =========================================================================

   /**
    * @param swtOp like SWT.MouseDoubleClick
    * @param listener = org.eclipse.swt.widgets.Listener
    */
   public XWidgetBuilder valueListener(int swtOp, Listener listener) {
      Conditions.assertNotNull(widData, "widData");
      widData.addSwtValueListener(swtOp, listener);
      return this;
   }

   public XWidgetBuilder andListener(XModifiedListener listener) {
      widData.addXModListener(listener);
      return this;
   }

   // =========================================================================
   // Get Results
   // =========================================================================

   public List<XWidgetData> getXWidgetDatas() {
      return widDatas;
   }

}
