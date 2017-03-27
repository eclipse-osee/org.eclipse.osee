/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.mocks;

import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetOptionHandler;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.WidgetOptionHandler;

/**
 * @author Donald G. Dunne
 */
public class MockWidgetDefinition implements IAtsWidgetDefinition {

   private String attributeName;
   private String toolTip;
   private String description;
   private int height;
   private String xWidgetName;
   private String defaultValue;
   private final WidgetOptionHandler options = new WidgetOptionHandler();
   private Double min;
   private Double max;
   private String name;

   public MockWidgetDefinition(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getAtrributeName() {
      return attributeName;
   }

   public void setAttributeName(String storeName) {
      this.attributeName = storeName;
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
      return String.format("[%s][%s]", getName(), getAtrributeName());
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

}
