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
public enum VCastCoverageType {
   STATEMENT(1);

   private final int intValue;

   private VCastCoverageType(int intValue) {
      this.intValue = intValue;
   }

   public static VCastCoverageType valueOf(int intValue) {
      VCastCoverageType ret = null;
      for (VCastCoverageType cvgType : VCastCoverageType.values()) {
         if (cvgType.intValue == intValue) {
            ret = cvgType;
            break;
         }
      }
      return ret;
   }

}
