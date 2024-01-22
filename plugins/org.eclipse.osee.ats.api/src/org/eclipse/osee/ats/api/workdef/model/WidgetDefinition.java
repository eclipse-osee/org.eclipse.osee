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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetOptionHandler;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetOptionHandler;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristic;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinition extends LayoutItem {

   private final AttributeTypeToken attributeType;
   private AttributeTypeToken attributeType2;
   private final ComputedCharacteristicToken<?> computedCharacteristic;
   private final Map<String, Object> parameters = new HashMap<String, Object>();
   private final RelationTypeSide relationTypeSide;
   private final WidgetOptionHandler options = new WidgetOptionHandler();
   private List<ConditionalRule> conditions = new ArrayList<>();

   private String toolTip;
   private String description;
   private int height;
   private String xWidgetName;
   private String defaultValue;
   private Double min;
   private Double max;
   private ArtifactToken enumeratedArt;
   private OseeImage oseeImage;

   public WidgetDefinition(String name) {
      this(name, "");
   }

   public WidgetDefinition(String name, AttributeTypeToken attributeType, String xWidgetName, WidgetOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, attributeType, ComputedCharacteristicToken.SENTINEL, xWidgetName,
         widgetOptions);
   }

   public WidgetDefinition(String name, String xWidgetName, WidgetOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, AttributeTypeToken.SENTINEL, ComputedCharacteristicToken.SENTINEL,
         xWidgetName, widgetOptions);
   }

   public WidgetDefinition(String name, RelationTypeSide relationTypeSide, String xWidgetName, WidgetOption... widgetOptions) {
      this(name, relationTypeSide, AttributeTypeToken.SENTINEL, ComputedCharacteristicToken.SENTINEL, xWidgetName,
         widgetOptions);
   }

   public WidgetDefinition(String name, RelationTypeSide relationTypeSide, AttributeTypeToken attributeType, ComputedCharacteristic<?> computedCharacteristic, String xWidgetName, WidgetOption... widgetOptions) {
      super(name);
      this.relationTypeSide = relationTypeSide;
      Conditions.assertNotNull(attributeType, "attribute type can not be null for WidgetDefinition [%s]", name);
      this.attributeType = attributeType;
      this.computedCharacteristic = computedCharacteristic;
      this.xWidgetName = xWidgetName;
      for (WidgetOption opt : widgetOptions) {
         options.add(opt);
      }
   }

   public WidgetDefinition(AttributeTypeToken attrType, String xWidgetName, List<ConditionalRule> conditions, WidgetOption... widgetOptions) {
      this(attrType, xWidgetName, widgetOptions);
      this.conditions = conditions;
   }

   public WidgetDefinition(AttributeTypeToken attrType, String xWidgetName, WidgetOption... widgetOptions) {
      this(attrType.getUnqualifiedName(), RelationTypeSide.SENTINEL, attrType, ComputedCharacteristicToken.SENTINEL,
         xWidgetName, widgetOptions);
   }

   public WidgetDefinition(ComputedCharacteristic<?> computedCharacteristic, String xWidgetName, WidgetOption... widgetOptions) {
      this(computedCharacteristic.getName(), RelationTypeSide.SENTINEL, AttributeTypeToken.SENTINEL,
         computedCharacteristic, xWidgetName, widgetOptions);
   }

   public WidgetDefinition(String name, AttributeTypeToken attrType, String xWidgetName, List<ConditionalRule> conditions, WidgetOption... widgetOptions) {
      this(name, attrType, xWidgetName, widgetOptions);
      this.conditions = conditions;
   }

   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   public boolean is(WidgetOption widgetOption) {
      return options.contains(widgetOption);
   }

   public void set(WidgetOption widgetOption) {
      options.add(widgetOption);
   }

   public String getXWidgetName() {
      return xWidgetName;
   }

   public void setXWidgetName(String xWidgetName) {
      this.xWidgetName = xWidgetName;
   }

   public String getDefaultValue() {
      return defaultValue;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public void setDescription(String description) {
      this.description = description;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(),
         getAttributeType().isValid() ? getAttributeType().toStringWithId() : "");
   }

   public IAtsWidgetOptionHandler getOptions() {
      return options;
   }

   public void setConstraint(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public Double getMin() {
      return min;
   }

   public Double getMax() {
      return max;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public ComputedCharacteristicToken<?> getComputedCharacteristic() {
      return computedCharacteristic;
   }

   public RelationTypeSide getRelationTypeSide() {
      return relationTypeSide;
   }

   public void addParameter(String key, Object obj) {
      parameters.put(key, obj);
   }

   public Object getParameter(String key) {
      return parameters.get(key);
   }

   public Map<String, Object> getParameters() {
      return parameters;
   }

   public List<ConditionalRule> getConditions() {
      return conditions;
   }

   public void setConditions(List<ConditionalRule> conditions) {
      this.conditions = conditions;
   }

   public WidgetDefinition andEnumeratedArt(ArtifactToken enumeratedArt) {
      this.enumeratedArt = enumeratedArt;
      return this;
   }

   public ArtifactToken getEnumeratedArt() {
      return enumeratedArt;
   }

   public void setEnumeratedArt(ArtifactToken enumeratedArt) {
      this.enumeratedArt = enumeratedArt;
   }

   public OseeImage getOseeImage() {
      return oseeImage;
   }

   public void setOseeImage(OseeImage oseeImage) {
      this.oseeImage = oseeImage;
   }

   public AttributeTypeToken getAttributeType2() {
      return attributeType2;
   }

   public void setAttributeType2(AttributeTypeToken attributeType2) {
      this.attributeType2 = attributeType2;
   }

}
