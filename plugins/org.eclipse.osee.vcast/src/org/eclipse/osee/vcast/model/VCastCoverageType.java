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
