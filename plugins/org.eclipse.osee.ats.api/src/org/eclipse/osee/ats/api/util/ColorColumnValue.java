/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

/**
 * @author Donald G. Dunne
 */
public class ColorColumnValue {

   private String value;
   private String fgHexColor;
   private String bgHexColor;

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String getFgHexColor() {
      return fgHexColor;
   }

   public void setFgHexColor(String fgHexColor) {
      this.fgHexColor = fgHexColor;
   }

   public String getBgHexColor() {
      return bgHexColor;
   }

   public void setBgHexColor(String bgHexColor) {
      this.bgHexColor = bgHexColor;
   }

}
