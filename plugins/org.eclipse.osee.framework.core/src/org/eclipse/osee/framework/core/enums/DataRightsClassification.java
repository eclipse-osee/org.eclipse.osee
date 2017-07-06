/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

public enum DataRightsClassification {

      governmentPurposeRights("Government Purpose Rights"),
      limitedRights("Limited Rights"),
      proprietary("Proprietary"),
      restrictedRights("Restricted Rights"),
      Unspecified("Unspecified");

      String dataRightsClassification;

   DataRightsClassification(String dataRightsClassification) {
      this.dataRightsClassification = dataRightsClassification;
   }

   public String getDataRightsClassification() {
      return dataRightsClassification;
   }

   public static boolean isValid(String check) {
      for (DataRightsClassification classification : DataRightsClassification.values()) {
         if (classification.getDataRightsClassification().equals(check)) {
            return true;
         }
      }
      return false;
   }
}
