/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.framework.core.data.BranchId;

public class AtsAttachment {

   private String name;
   private BranchId branch;
   private String location;

   public AtsAttachment() {
      // for jax-rs
   }

   public AtsAttachment(String name, String location, BranchId branch) {
      this.name = name;
      this.branch = branch;
      this.location = location;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getlocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   @Override
   public String toString() {
      return "Attachment [name=" + name + ", location=" + location + "branch=" + branch + "]";
   }

}
