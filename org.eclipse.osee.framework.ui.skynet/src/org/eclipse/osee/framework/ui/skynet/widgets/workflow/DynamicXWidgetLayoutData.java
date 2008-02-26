/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class DynamicXWidgetLayoutData {
   private static final XWidgetFactory xWidgetFactory = XWidgetFactory.getInstance();
   private static final int DEFAULT_HEIGHT = 9999;
   private String name = "Unknown";
   private String layoutName = "";
   private boolean required = false;
   private String xWidgetName = UNKNOWN;
   private static String UNKNOWN = "Unknown";
   private XWidget xWidget;
   public static enum Align {
      Left, Right, Center
   };
   private Align align = Align.Left;
   private boolean horizontalLabel = false;
   private boolean labelAfter = false;
   private int beginComposite = 0; // If >0, indicates new child composite with columns == value
   private boolean endComposite; // indicated end of child composite
   private int height = DEFAULT_HEIGHT;
   private String toolTip;

   public static enum Fill {
      None, Vertically, Horizontally
   };
   private Fill fill = Fill.None;
   private final DynamicXWidgetLayout dynamicXWidgetLayout;
   private String defaultValue;
   private String keyedBranchName;

   public DynamicXWidgetLayoutData(DynamicXWidgetLayout dynamicXWidgetLayout) {
      this.dynamicXWidgetLayout = dynamicXWidgetLayout;
   }

   public boolean isHeightSet() {
      return height != DEFAULT_HEIGHT;
   }

   public String toString() {
      return getName();
   }

   public String getName() {
      return name.replaceFirst("^.*?\\.", "");
   }

   /**
    * @return Returns the layoutName.
    */
   public String getLayoutName() {
      return layoutName;
   }

   /**
    * @param layoutName The layoutName to set.
    */
   public void setlayoutName(String layoutName) {
      this.layoutName = layoutName;
   }

   /**
    * @return Returns the required.
    */
   public boolean isRequired() {
      return required || dynamicXWidgetLayout.isOrRequired(layoutName) || dynamicXWidgetLayout.isXOrRequired(layoutName);
   }

   /**
    * @param required The required to set.
    */
   public void setRequired(boolean required) {
      this.required = required;
   }

   /**
    * @return Returns the xWidget.
    */
   public String getXWidgetName() {
      return xWidgetName;
   }

   /**
    * @param widget The xWidget to set.
    */
   public void setXWidgetName(String widget) {
      xWidgetName = widget;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   // TODO This method will need to be removed
   public XWidget getXWidget() {
      if (xWidget == null) {
         xWidget = xWidgetFactory.createXWidget(xWidgetName, name, labelAfter, this);
      }
      return xWidget;
   }

   /**
    * @return Returns the align.
    */
   public Align getAlign() {
      return align;
   }

   /**
    * @param align The align to set.
    */
   public void setAlign(Align align) {
      this.align = align;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   /**
    * @return Returns the fill.
    */
   public Fill getFill() {
      return fill;
   }

   /**
    * @param fill The fill to set.
    */
   public void setFill(Fill fill) {
      this.fill = fill;
   }

   /**
    * @return Returns the horizontalLabel.
    */
   public boolean isHorizontalLabel() {
      return horizontalLabel;
   }

   /**
    * @param horizontalLabel The horizontalLabel to set.
    */
   public void setHorizontalLabel(boolean horizontalLabel) {
      this.horizontalLabel = horizontalLabel;
   }

   /**
    * @return Returns the height.
    */
   public int getHeight() {
      return height;
   }

   /**
    * @param height The height to set.
    */
   public void setHeight(int height) {
      this.height = height;
   }

   /**
    * @return Returns the beginComposite.
    */
   public int getBeginComposite() {
      return beginComposite;
   }

   /**
    * @param beginComposite The beginComposite to set.
    */
   public void setBeginComposite(int beginComposite) {
      this.beginComposite = beginComposite;
   }

   /**
    * @return Returns the endComposite.
    */
   public boolean isEndComposite() {
      return endComposite;
   }

   /**
    * @param endComposite The endComposite to set.
    */
   public void setEndComposite(boolean endComposite) {
      this.endComposite = endComposite;
   }

   /**
    * @return the toolTip
    */
   public String getToolTip() {
      return toolTip;
   }

   /**
    * @param toolTip the toolTip to set
    */
   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   /**
    * @return the labelAfter
    */
   public boolean isLabelAfter() {
      return labelAfter;
   }

   /**
    * @param labelAfter the labelAfter to set
    */
   public void setLabelAfter(boolean labelAfter) {
      this.labelAfter = labelAfter;
   }

   /**
    * @return the dynamicXWidgetLayout
    */
   public DynamicXWidgetLayout getDynamicXWidgetLayout() {
      return dynamicXWidgetLayout;
   }

   /**
    * @return the defaultValue
    */
   public String getDefaultValue() {
      return defaultValue;
   }

   /**
    * 
    */
   public void setKeyedBranchName(String keyedBranchName) {
      this.keyedBranchName = keyedBranchName;
   }

   /**
    * @return the keyedBranchName
    */
   public String getKeyedBranchName() {
      return keyedBranchName;
   }
}