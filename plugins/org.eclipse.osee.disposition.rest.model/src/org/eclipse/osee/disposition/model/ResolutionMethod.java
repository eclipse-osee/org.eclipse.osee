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

package org.eclipse.osee.disposition.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Angel Avila
 */
@XmlRootElement(name = "ResolutionMethod")
public class ResolutionMethod {

   private List<ResolutionMethod> resolutions;
   private String value;
   private String text;
   private boolean isDefault;

   public void setValue(String value) {
      this.value = value;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setIsDefault(boolean isDefault) {
      this.isDefault = isDefault;
   }

   public String getValue() {
      return value;
   }

   public String getText() {
      return text;
   }

   public boolean getIsDefault() {
      return isDefault;
   }

   public List<ResolutionMethod> getResolutions() {
      return resolutions;
   }

   public void setResolutions(List<ResolutionMethod> resolutions) {
      this.resolutions = resolutions;
   }
}
