/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
 * Represents a single SOURCE block found in the <dir>.wrk/vcast.vcp file.
 * 
 * @author Donald G. Dunne
 */
public class VcpSourceFile {

   private String filename;
   private String unitNumber;
   private final VCastVcp vCastVcp;

   public VcpSourceFile(VCastVcp vCastVcp) {
      this.vCastVcp = vCastVcp;
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public String getUnitNumber() {
      return unitNumber;
   }

   public void setUnitNumber(String unitNumber) {
      this.unitNumber = unitNumber;
   }

   public VCastVcp getvCastVcp() {
      return vCastVcp;
   }

   public void cleanup() {
      unitNumber = null;
   }

   @Override
   public String toString() {
      return filename;
   }

}
