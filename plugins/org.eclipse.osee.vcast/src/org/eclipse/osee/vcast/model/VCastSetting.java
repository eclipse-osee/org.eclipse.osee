/*********************************************************************
 * Copyright (c) 2012 Boeing
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
