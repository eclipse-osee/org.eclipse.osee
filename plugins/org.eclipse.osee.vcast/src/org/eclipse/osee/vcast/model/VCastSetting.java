/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

/**
 * @author Shawn F. Cook
 */
public class VCastSetting {

   private final String setting;
   private final String value;

   public String getSetting() {
      return setting;
   }

   public String getValue() {
      return value;
   }

   public VCastSetting(String setting, String value) {
      super();
      this.setting = setting;
      this.value = value;
   }
}
