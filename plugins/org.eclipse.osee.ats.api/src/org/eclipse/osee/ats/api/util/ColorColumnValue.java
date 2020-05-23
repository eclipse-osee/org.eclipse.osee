/*********************************************************************
 * Copyright (c) 2015 Boeing
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
