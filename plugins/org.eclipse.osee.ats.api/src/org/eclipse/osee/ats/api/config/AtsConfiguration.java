/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

/**
 * @author Donald G. Dunne
 */
public class AtsConfiguration {

   private boolean isDefault;
   private String name;
   private long id;
   private long branchId;

   public boolean isDefault() {
      return isDefault;
   }

   public void setIsDefault(boolean isDefault) {
      this.isDefault = isDefault;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id= id;
   }

   public long getBranchId() {
      return branchId;
   }

   public void setBranchId(long branchId) {
      this.branchId = branchId;
   }

   @Override
   public String toString() {
      return name;
   }
}
