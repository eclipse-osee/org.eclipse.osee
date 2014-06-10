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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsConfiguration {

   private boolean isDefault;
   private String name;
   private int uuid;
   private long branchUuid;

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

   public int getUuid() {
      return uuid;
   }

   public void setUuid(int uuid) {
      this.uuid = uuid;
   }

   public long getBranchUuid() {
      return branchUuid;
   }

   public void setBranchUuid(long branchUuid) {
      this.branchUuid = branchUuid;
   }

   @Override
   public String toString() {
      return name;
   }
}
