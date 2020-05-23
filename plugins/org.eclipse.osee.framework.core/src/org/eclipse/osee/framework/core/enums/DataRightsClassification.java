/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
