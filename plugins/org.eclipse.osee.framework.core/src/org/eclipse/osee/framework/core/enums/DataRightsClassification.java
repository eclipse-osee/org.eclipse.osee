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

/**
 * @author David W. Miller
 */
public enum DataRightsClassification {

   governmentPurposeRights("Government Purpose Rights"),
   limitedRights("Limited Rights"),
   proprietary("Proprietary"),
   restrictedRights("Restricted Rights"),
   exportControlledItar("Export Controlled ITAR"),
   Unspecified("Unspecified"),
   noOverride("No Override");

   String dataRightsClassification;

   DataRightsClassification(String dataRightsClassification) {
      this.dataRightsClassification = dataRightsClassification;
   }

   public String getDataRightsClassification() {
      return dataRightsClassification;
   }

   public static boolean isValid(DataRightsClassification check) {
      for (DataRightsClassification classification : DataRightsClassification.values()) {
         if (!classification.equals(noOverride) && classification.equals(check)) {
            return true;
         }
      }
      return false;
   }
}
