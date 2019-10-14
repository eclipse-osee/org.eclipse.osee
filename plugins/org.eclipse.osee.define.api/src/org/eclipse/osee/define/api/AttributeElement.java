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
package org.eclipse.osee.define.api;

/**
 * @author Jeff C. Phillips
 */
public class AttributeElement {
   private String label;
   private String attributeType;
   private String format;
   private String formatPre;
   private String formatPost;

   public AttributeElement() {
      this.label = "";
      this.attributeType = "";
      this.formatPre = "";
      this.formatPost = "";
      this.format = "";
   }

   public void setElements(String attributeType, String label, String formatPre, String formatPost) {
      this.label = label;
      this.attributeType = attributeType;
      this.format = formatPre;
      this.formatPre = formatPre;
      this.formatPost = formatPost;
   }

   public String getAttributeName() {
      return attributeType;
   }

   public String getFormat() {
      return format;
   }

   public String getFormatPre() {
      return formatPre;
   }

   public String getFormatPost() {
      return formatPost;
   }

   public String getLabel() {
      return label;
   }
}
