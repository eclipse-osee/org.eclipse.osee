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
package org.eclipse.osee.disposition.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

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
