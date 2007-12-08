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
package org.eclipse.osee.ats.util.widgets.dialog;

import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.swt.SWT;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Donald G. Dunne
 */
public class TaskResOptionDefinition {

   private String name;
   private String desc;
   private boolean completeable;
   private String color;
   private String percent;
   public static String ATS_TASK_OPTION_TAG = "AtsTaskOption";

   public TaskResOptionDefinition() {
      name = "";
      desc = "";
      percent = "";
      completeable = false;
   }

   /**
    * @param name
    * @param desc
    * @param completeable true/false of whether option allows task to be transitioned to complete
    */
   public TaskResOptionDefinition(String name, String desc, String completeable, String color, String defaultPercent) {
      this(name, desc, completeable.equals("true"), color, defaultPercent);
   }

   public TaskResOptionDefinition(String name, String desc, boolean completeable, String defaultPercent) {
      this(name, desc, completeable, "", defaultPercent);
   }

   public void setFromElement(Element element) {
      for (int x = 0; x < element.getAttributes().getLength(); x++) {
         Node node = element.getAttributes().item(x);
         String nodeName = node.getNodeName();
         if (nodeName.equals(Field.name.name()))
            name = node.getNodeValue();
         else if (nodeName.equals(Field.desc.name()))
            desc = node.getNodeValue();
         else if (nodeName.equals(Field.complete.name()))
            completeable = Boolean.parseBoolean(node.getNodeValue());
         else if (nodeName.equals(Field.color.name()))
            color = node.getNodeValue();
         else if (nodeName.equals(Field.percent.name()))
            percent = node.getNodeValue();
         else
            throw new IllegalArgumentException("Unknow Task Resolution Option Attribute \"" + nodeName + "\"");
      }
   }

   /**
    * @param name
    * @param desc
    * @param completeable true if option allows task to be transitioned to complete
    * @param color BLUE, RED, etc...; "" for black
    */
   public TaskResOptionDefinition(String name, String desc, boolean completeable, String color, String defaultPercent) {
      this.name = name;
      this.desc = desc;
      this.completeable = completeable;
      this.color = color;
      this.percent = defaultPercent;
   }

   public int getColorInt() {
      if (color == null || color.equals("")) return SWT.COLOR_BLACK;
      if (color.equals("WHITE")) return SWT.COLOR_WHITE;
      if (color.equals("BLACK")) return SWT.COLOR_BLACK;
      if (color.equals("RED")) return SWT.COLOR_RED;
      if (color.equals("DARK_RED")) return SWT.COLOR_DARK_RED;
      if (color.equals("GREEN")) return SWT.COLOR_GREEN;
      if (color.equals("DARK_GREEN")) return SWT.COLOR_DARK_GREEN;
      if (color.equals("YELLOW")) return SWT.COLOR_YELLOW;
      if (color.equals("DARK_YELLOW")) return SWT.COLOR_DARK_YELLOW;
      if (color.equals("BLUE")) return SWT.COLOR_BLUE;
      if (color.equals("DARK_BLUE")) return SWT.COLOR_DARK_BLUE;
      if (color.equals("MAGENTA")) return SWT.COLOR_MAGENTA;
      if (color.equals("DARK_MAGENTA")) return SWT.COLOR_DARK_MAGENTA;
      if (color.equals("CYAN")) return SWT.COLOR_CYAN;
      if (color.equals("DARK_CYAN")) return SWT.COLOR_DARK_CYAN;
      if (color.equals("GRAY")) return SWT.COLOR_GRAY;
      if (color.equals("DARK_GRAY")) return SWT.COLOR_DARK_GRAY;
      return SWT.COLOR_BLACK;
   }

   /**
    * @return true if resolution option allows task to be transitioned to complete
    */
   public boolean isCompleteable() {
      return completeable;
   }

   public String getDesc() {
      return desc;
   }

   public String getName() {
      return name;
   }

   private enum Field {
      name, desc, complete, percent, color
   };

   public void setFromXml(String xml) {
      for (Field field : Field.values()) {
         String data = AXml.getTagData(xml, field.name());
         if (field == Field.name)
            setName(data);
         else if (field == Field.color)
            setColor(data);
         else if (field == Field.desc)
            setDesc(data);
         else if (field == Field.percent)
            setPercent(data);
         else if (field == Field.complete)
            setComplete(data.equals("true"));
         else
            throw new IllegalArgumentException("Unexpected field");
      }
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer("<" + ATS_TASK_OPTION_TAG + ">");
      for (Field field : Field.values()) {
         String str = "";
         if (field == Field.name)
            str = getName();
         else if (field == Field.color)
            str = getColor();
         else if (field == Field.desc)
            str = getDesc();
         else if (field == Field.percent)
            str = getPercent();
         else if (field == Field.complete)
            str = (isCompleteable() ? "true" : "false");
         else
            throw new IllegalArgumentException("Unexpected field");
         sb.append(AXml.addTagData(field.name(), str));
      }
      sb.append("</" + ATS_TASK_OPTION_TAG + ">");
      return sb.toString();
   }

   public void setComplete(boolean complete) {
      this.completeable = complete;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getColor() {
      return color;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public String getPercent() {
      return percent;
   }

   public void setPercent(String defaultPercent) {
      this.percent = defaultPercent;
   }
}
