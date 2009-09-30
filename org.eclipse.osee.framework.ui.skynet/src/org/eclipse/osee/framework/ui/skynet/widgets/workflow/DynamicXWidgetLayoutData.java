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

import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XOptionHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class DynamicXWidgetLayoutData implements Cloneable {
   private static final XWidgetFactory xWidgetFactory = XWidgetFactory.getInstance();
   private static final int DEFAULT_HEIGHT = 9999;
   private String name = "Unknown";
   private String id = "";
   private String storageName = "";
   private String xWidgetName = UNKNOWN;
   private static String UNKNOWN = "Unknown";
   private XWidget xWidget;
   private int beginComposite = 0; // If >0, indicates new child composite with columns == value
   private boolean endComposite; // indicated end of child composite
   private int height = DEFAULT_HEIGHT;
   private String toolTip;
   private DynamicXWidgetLayout dynamicXWidgetLayout;
   private String defaultValue;
   private String keyedBranchName;
   private final XOptionHandler xOptionHandler = new XOptionHandler();

   public DynamicXWidgetLayoutData(DynamicXWidgetLayout dynamicXWidgetLayout, XOption... xOption) {
      this.dynamicXWidgetLayout = dynamicXWidgetLayout;
      xOptionHandler.add(XOption.EDITABLE);
      xOptionHandler.add(XOption.ALIGN_LEFT);
      xOptionHandler.add(xOption);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public boolean isHeightSet() {
      return height != DEFAULT_HEIGHT;
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getName() {
      return name.replaceFirst("^.*?\\.", "");
   }

   /**
    * @return Returns the storageName.
    */
   public String getStorageName() {
      return storageName;
   }

   /**
    * @param storageName The storageName to set.
    */
   public void setStorageName(String storageName) {
      this.storageName = storageName;
   }

   /**
    * @return Returns the required.
    */
   public boolean isRequired() {
      return xOptionHandler.contains(XOption.REQUIRED) || dynamicXWidgetLayout.isOrRequired(storageName) || dynamicXWidgetLayout.isXOrRequired(storageName);
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
         xWidget = xWidgetFactory.createXWidget(this);
      }
      return xWidget;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
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
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_10)) return 10;
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_8)) return 8;
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_6)) return 6;
      if (xOptionHandler.contains(XOption.BEGIN_COMPOSITE_4)) return 4;
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

   /**
    * @param dynamicXWidgetLayout the dynamicXWidgetLayout to set
    */
   public void setDynamicXWidgetLayout(DynamicXWidgetLayout dynamicXWidgetLayout) {
      this.dynamicXWidgetLayout = dynamicXWidgetLayout;
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * @return the xOptionHandler
    */
   public XOptionHandler getXOptionHandler() {
      return xOptionHandler;
   }

}