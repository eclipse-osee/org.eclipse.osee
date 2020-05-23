/*********************************************************************
 * Copyright (c) 2017 Boeing
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
