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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetOptionHandler;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetOptionHandler;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WidgetDefinition extends LayoutItem implements IAtsWidgetDefinition {

   private final AttributeTypeToken attributeType;
   private final Map<String, Object> parameters = new HashMap<String, Object>();
   private final RelationTypeSide relationTypeSide;
   private final WidgetOptionHandler options = new WidgetOptionHandler();

   private String toolTip;
   private String description;
   private int height;
   private String xWidgetName;
   private String defaultValue;
   private Double min;
   private Double max;

   public WidgetDefinition(String name) {
      this(name, "");
   }

   public WidgetDefinition(String name, AttributeTypeToken attributeType, String xWidgetName, WidgetOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, attributeType, xWidgetName, widgetOptions);
   }

   public WidgetDefinition(String name, String xWidgetName, WidgetOption... widgetOptions) {
      this(name, RelationTypeSide.SENTINEL, AttributeTypeToken.SENTINEL, xWidgetName, widgetOptions);
   }

   public WidgetDefinition(String name, RelationTypeSide relationTypeSide, String xWidgetName, WidgetOption... widgetOptions) {
      this(name, relationTypeSide, AttributeTypeToken.SENTINEL, xWidgetName, widgetOptions);
   }

   public WidgetDefinition(String name, RelationTypeSide relationTypeSide, AttributeTypeToken attributeType, String xWidgetName, WidgetOption... widgetOptions) {
      super(name);
      this.relationTypeSide = relationTypeSide;
      Conditions.assertNotNull(attributeType, "attribute type can not be null for WidgetDefinition [%s]", name);
      this.attributeType = attributeType;
      this.xWidgetName = xWidgetName;
      for (WidgetOption opt : widgetOptions) {
         options.add(opt);
      }
   }

   public WidgetDefinition(AttributeTypeToken attrType, String xWidgetName, WidgetOption... widgetOptions) {
      this(attrType.getUnqualifiedName(), RelationTypeSide.SENTINEL, attrType, xWidgetName, widgetOptions);
   }

   @Override
   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   @Override
   public boolean is(WidgetOption widgetOption) {
      return options.contains(widgetOption);
   }

   @Override
   public void set(WidgetOption widgetOption) {
      options.add(widgetOption);
   }

   @Override
   public String getXWidgetName() {
      return xWidgetName;
   }

   @Override
   public void setXWidgetName(String xWidgetName) {
      this.xWidgetName = xWidgetName;
   }

   @Override
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

   @Override
   public int getHeight() {
      return height;
   }

   @Override
   public void setHeight(int height) {
      this.height = height;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(),
         getAttributeType().isValid() ? getAttributeType().toStringWithId() : "");
   }

   @Override
   public IAtsWidgetOptionHandler getOptions() {
      return options;
   }

   @Override
   public void setConstraint(double min, double max) {
      this.min = min;
      this.max = max;
   }

   @Override
   public Double getMin() {
      return min;
   }

   @Override
   public Double getMax() {
      return max;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public RelationTypeSide getRelationTypeSide() {
      return relationTypeSide;
   }

   @Override
   public void addParameter(String key, Object obj) {
      parameters.put(key, obj);
   }

   @Override
   public Object getParameter(String key) {
      return parameters.get(key);
   }

   @Override
   public Map<String, Object> getParameters() {
      return parameters;
   }
}
