/*********************************************************************
 * Copyright (c) 2010 Boeing
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
