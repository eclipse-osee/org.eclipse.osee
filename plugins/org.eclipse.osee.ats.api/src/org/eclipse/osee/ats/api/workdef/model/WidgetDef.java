/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristic;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WidgetDef extends LayoutItem {

   /**
    * XWidgets have their own XWidgetData. They should be applied first. WidgetDef shouldn't reset values, but only set
    * things that should be overridden from the XWidget chosen
    */
   protected XWidgetData overrideWidData = new XWidgetData();

   public WidgetDef(String name) {
      this(name, WidgetId.SENTINEL);
   }

   public WidgetDef(WidgetId widgetId) {
      this("", widgetId);
   }

   public WidgetDef(String name, WidgetId widgetId, XOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, widgetId, widgetOptions);
   }

   public WidgetDef(RelationTypeSide relationTypeSide, WidgetId widgetId, //
      XOption... widgetOptions) {
      this(relationTypeSide.getName(), relationTypeSide, widgetId, widgetOptions);
   }

   /**
    * @param xOptions - Only the XOption(s) that need to be overridden from the widget that was chosen
    */
   public WidgetDef(String name, RelationTypeSide relationTypeSide, WidgetId widgetId, //
      XOption... xOptions) {
      this(name, relationTypeSide, null, widgetId, xOptions);
   }

   //   /**
   //    * @param xOptions - Only the XOption(s) that need to be overridden from the widget that was chosen
   //    */
   //   public WidgetDef(String name, AttributeTypeToken attributeType, //
   //      ComputedCharacteristic<?> computedCharacteristic, WidgetId widgetId, XOption... xOptions) {
   //      this(name, attributeType, RelationTypeSide.SENTINEL, computedCharacteristic, widgetId, xOptions);
   //   }

   /**
    * @param xOptions - Only the XOption(s) that need to be overridden from the widget that was chosen
    */
   private WidgetDef(String name, RelationTypeSide relationTypeSide, //
      ComputedCharacteristic<?> computedCharacteristic, WidgetId widgetId, //
      XOption... xOptions) {
      super(name);

      setupXOptions(xOptions);

      overrideWidData.setLabel(name);
      //      if (!(this instanceof SpaceWidgetDef)) {
      //         Conditions.assertNotNull(attributeType, "attribute type can not be null for WidgetDefinition [%s]", name);
      //         overrideWidData.setAttributeType(attributeType);
      //      }
      overrideWidData.setComputedCharacteristic(computedCharacteristic);
      overrideWidData.setWidgetId(widgetId);
      // Store the relation so relation-backed widgets (e.g. XXListRelationWidget) can resolve/display related
      // artifacts. Was previously dropped here, leaving getRelationTypeSide() SENTINEL and the widget empty.
      if (relationTypeSide != null) {
         overrideWidData.setRelationTypeSide(relationTypeSide);
      }
   }

   private void setupXOptions(XOption... xOptions) {
      /**
       * Clear options defaults cause WidgetDef is only going to add what needs to be overridden from XWidget defaults
       */
      overrideWidData.getXOptionHandler().getXOptions().clear();
      /**
       * Now add override options, these will be applied on top of selected XWidget's defaults; In most cases WidgetDef
       * should not need to specify any XOptions
       */
      for (XOption opt : xOptions) {
         set(opt);
      }
   }

   //   public WidgetDef(AttributeTypeToken attrType, WidgetId widgetId, List<ConditionalRule> conditions, XOption... widgetOptions) {
   //      this(attrType, widgetId, widgetOptions);
   //      setConditions(conditions);
   //   }

   //   public WidgetDef(AttributeTypeToken attrType, WidgetId widgetId, XOption... widgetOptions) {
   //      this(attrType.getUnqualifiedName(), attrType, ComputedCharacteristicToken.SENTINEL, widgetId, widgetOptions);
   //   }

   //   public WidgetDef(ComputedCharacteristic<?> computedCharacteristic, WidgetId widgetId, XOption... widgetOptions) {
   //      this(computedCharacteristic.getName(), AttributeTypeToken.SENTINEL, computedCharacteristic, widgetId,
   //         widgetOptions);
   //   }

   //   public WidgetDef(String name, AttributeTypeToken attrType, WidgetId widgetId, List<ConditionalRule> conditions, XOption... widgetOptions) {
   //      this(name, attrType, widgetId, widgetOptions);
   //      setConditions(conditions);
   //   }

   public WidgetDef(WidgetId widgetId, XOption... options) {
      this(widgetId);
      set(options);
   }

   public String getToolTip() {
      return overrideWidData.getToolTip();
   }

   public WidgetDef andToolTip(String toolTip) {
      return this.setToolTip(toolTip);
   }

   public WidgetDef setToolTip(String toolTip) {
      overrideWidData.setToolTip(toolTip);
      return this;
   }

   public Object getDefaultValue() {
      return overrideWidData.getDefaultValue();
   }

   public WidgetDef andDefaultValue(String defaultValue) {
      overrideWidData.setDefaultValue(defaultValue);
      return this;
   }

   public WidgetDef setDefaultValue(String defaultValue) {
      overrideWidData.setDefaultValue(defaultValue);
      return this;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]\n\n%s", getName(),
         getAttributeType().isValid() ? getAttributeType().toStringWithId() : "", overrideWidData);
   }

   public void setConstraint(double min, double max) {
      overrideWidData.setMin(min);
      overrideWidData.setMax(max);
   }

   public Double getMin() {
      return overrideWidData.getMin();
   }

   public Double getMax() {
      return overrideWidData.getMax();
   }

   public AttributeTypeToken getAttributeType() {
      return overrideWidData.getAttributeType();
   }

   public ComputedCharacteristicToken<?> getComputedCharacteristic() {
      return overrideWidData.getComputedCharacteristic();
   }

   public RelationTypeSide getRelationTypeSide() {
      return overrideWidData.getRelationTypeSide();
   }

   public void addParameter(String key, Object obj) {
      overrideWidData.addParameter(key, obj);
   }

   public Object getParameter(String key) {
      return overrideWidData.getParameters().get(key);
   }

   public Map<String, Object> getParameters() {
      return overrideWidData.getParameters();
   }

   public List<ConditionalRule> getConditions() {
      return overrideWidData.getConditions();
   }

   public void setConditions(List<ConditionalRule> conditions) {
      overrideWidData.setConditions(conditions);
   }

   /**
    * @param Enumerated Artifact containing selectable options from attributes
    */
   public WidgetDef andEnumArt(ArtifactToken enumeratedArt) {
      Conditions.requireNonNull(enumeratedArt, "Enumerated Art");
      Conditions.assertTrue(overrideWidData.getWidgetId().isValid(),
         "Enum artifact widgets require an explicit widget ID; use EnumArtWidgetDef or a widget that supports enum artifacts");
      overrideWidData.setEnumeratedArt(enumeratedArt);
      return this;
   }

   public ArtifactToken getEnumeratedArt() {
      return overrideWidData.getEnumeratedArt();
   }

   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      andEnumArt(enumeratedArt);
   }

   public OseeImage getOseeImage() {
      return overrideWidData.getOseeImage();
   }

   public WidgetDef setOseeImage(OseeImage oseeImage) {
      overrideWidData.setOseeImage(oseeImage);
      return this;
   }

   public AttributeTypeToken getAttributeType2() {
      return overrideWidData.getAttributeType2();
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException("InValid for WidgetDef, use AttrWidgetDef");
   }

   public void setAttributeType2(AttributeTypeToken attributeType2) {
      throw new UnsupportedOperationException("InValid for WidgetDef, use AttrWidgetDef");
   }

   public IUserGroupArtifactToken getUserGroup() {
      return overrideWidData.getUserGroup();
   }

   public WidgetDef setUserGroup(IUserGroupArtifactToken userGroup) {
      overrideWidData.setUserGroup(userGroup);
      return this;
   }

   public LayoutItem andCondition(ConditionalRule... rules) {
      for (ConditionalRule rule : rules) {
         overrideWidData.getConditions().add(rule);
      }
      return this;
   }

   public LayoutItem andUserGroup(IUserGroupArtifactToken userGroup) {
      setUserGroup(userGroup);
      return this;
   }

   public WidgetId getWidgetId() {
      return overrideWidData.getWidgetId();
   }

   public void setWidgetId(WidgetId widgetId) {
      overrideWidData.setWidgetId(widgetId);
   }

   public XWidgetData getWidData() {
      return overrideWidData;
   }

   public Set<XOption> getOptions() {
      return overrideWidData.getXOptionHandler().getXOptions();
   }

   public boolean is(XOption... options) {
      return overrideWidData.is(options);
   }

   public boolean isNot(XOption... options) {
      return !is(options);
   }

   public WidgetDef set(XOption... options) {
      overrideWidData.set(options);
      return this;
   }

   public WidgetDef andImage(OseeImage image) {
      overrideWidData.setOseeImage(image);
      return this;
   }
}
