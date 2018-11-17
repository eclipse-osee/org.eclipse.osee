/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model.writer.reader;

import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */

public class OwRelationType extends OwBase {

   public OwRelationType() {
      // for jax-rs instantiation
      super(Id.SENTINEL, "");
   }

   public OwRelationType(Long id, String name) {
      super(id, name);
   }

   private boolean sideA;
   private String sideName;

   public boolean isSideA() {
      return sideA;
   }

   public void setSideA(boolean sideA) {
      this.sideA = sideA;
   }

   public String getSideName() {
      return sideName;
   }

   public void setSideName(String sideName) {
      this.sideName = sideName;
   }

   @Override
   public String toString() {
      return "OwRelationType [sideA=" + sideA + ", sideName=" + sideName + ", id=" + getId() + ", data=" + data + "]";
   }
}