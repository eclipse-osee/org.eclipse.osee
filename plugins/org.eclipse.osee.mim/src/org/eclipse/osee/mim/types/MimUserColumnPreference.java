/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.types;

/**
 * @author Luciano T. Vaglienti
 */
public class MimUserColumnPreference {

   private boolean isEnabled;
   private String name;
   public MimUserColumnPreference() {
   }

   public MimUserColumnPreference(String name, boolean isEnabled) {
      this.setName(name);
      this.setEnabled(isEnabled);
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the isEnabled
    */
   public boolean isEnabled() {
      return isEnabled;
   }

   /**
    * @param isEnabled the isEnabled to set
    */
   public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
   }

}
