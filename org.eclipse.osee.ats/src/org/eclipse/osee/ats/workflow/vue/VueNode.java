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
package org.eclipse.osee.ats.workflow.vue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.vue.DiagramNode.PageType;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class VueNode {

   private final String vueXml;
   private String vueId;
   private DiagramNode workPage;
   public static enum Shape {
      ellipse, rectangle, hexagon;
      public static Shape getShape(String shape) {
         for (Shape s : Shape.values()) {
            if (s.name().equals(shape)) return s;
         }
         return null;
      }
   };
   private Shape shape;

   public String toString() {
      return workPage.getPageType() + ": " + workPage.getName();
   }

   /**
    * @return Returns the workPage.
    */
   public DiagramNode getWorkPage() {
      return workPage;
   }

   /**
    * 
    */
   public VueNode(String vueXml) {
      super();
      this.vueXml = vueXml;
      workPage = new DiagramNode(ATSXWidgetOptionResolver.getInstance());
      processVueXml(vueXml);
   }

   public void processVueXml(String xml) {
      String noteXml = AXml.getTagData(xml, "notes");
      noteXml = noteXml.replaceAll("%nl;", "\r");
      noteXml = noteXml.replaceAll("%sp;", " ");
      workPage.setInstructionStr(noteXml);
      getDetails();
      if (getShape() == VueNode.Shape.ellipse)
         workPage.setPageType(PageType.Team);
      else if (getShape() == VueNode.Shape.rectangle) workPage.setPageType(PageType.ActionableItem);
   }

   public void getDetails() {
      Matcher m = Pattern.compile("<child.*? label=\"(.*?)\" ").matcher(vueXml);
      if (m.find())
         workPage.setName(m.group(1));
      else
         workPage.setName("Unknown");

      m = Pattern.compile("<shape xsi:type=\"(.*?)\"").matcher(vueXml);
      if (m.find())
         shape = Shape.getShape(m.group(1));
      else
         throw new IllegalArgumentException("Can't determine shape name");
      m = Pattern.compile("<child.*? ID=\"(.*?)\" ").matcher(vueXml);
      if (m.find())
         vueId = m.group(1);
      else
         vueId = "Unknown";
   }

   /**
    * @return Returns the shape.
    */
   public Shape getShape() {
      return shape;
   }

   /**
    * @return Returns the vueObjId.
    */
   public String getVueId() {
      return vueId;
   }

   /**
    * @param vueObjId The vueObjId to set.
    */
   public void setVueId(String vueObjId) {
      this.vueId = vueObjId;
   }

}
