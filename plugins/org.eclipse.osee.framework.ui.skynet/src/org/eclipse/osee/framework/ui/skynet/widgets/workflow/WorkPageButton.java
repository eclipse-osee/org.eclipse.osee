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

/**
 * @author Donald G. Dunne
 */
public class WorkPageButton {

   private String name = "Unknown";
   private String attrName = "";
   private boolean required = false;
   private String xWidget = "Unknown";

   /**
    * <Name[:Attribute Name]>,<(Required|Optional)>,<XWidget> eg: Title:Name,Required,XText eg:
    * Description,Optional,XText
    */
   public WorkPageButton(String xml) {
      super();
      String values[] = xml.split(",");
      name = values[0];
      if (name.contains(":")) {
         String names[] = name.split(":");
         name = names[0];
         attrName = names[1];
      } else
         attrName = name;
      required = (values[1].equals("Required") ? true : false);
      xWidget = values[2];
   }

   public String getName() {
      return name;
   }

   /**
    * @return Returns the attrName.
    */
   public String getAttrName() {
      return attrName;
   }

   /**
    * @param attrName The attrName to set.
    */
   public void setAttrName(String attrName) {
      this.attrName = attrName;
   }

   /**
    * @return Returns the required.
    */
   public boolean isRequired() {
      return required;
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
   public String getXWidget() {
      return xWidget;
   }

   /**
    * @param widget The xWidget to set.
    */
   public void setXWidget(String widget) {
      xWidget = widget;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

}
