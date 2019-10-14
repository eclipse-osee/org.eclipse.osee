/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
 * @author Dominic A. Guss
 */
public class MetadataElement {
   private String type;
   private String format;
   private String label;

   public MetadataElement() {
      this.type = "";
      this.format = "";
      this.label = "";
   }

   public void setElements(String type, String format, String label) {
      this.type = type;
      this.format = format;
      this.label = label;
   }

   public String getType() {
      return type;
   }

   public String getFormat() {
      return format;
   }

   public String getLabel() {
      return label;
   }
}
