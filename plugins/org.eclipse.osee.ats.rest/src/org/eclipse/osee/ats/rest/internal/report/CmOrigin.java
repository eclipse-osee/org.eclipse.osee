/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.rest.internal.report;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum CmOrigin {
   ATS,
   BUGZILLA,
   UNKNOWN;

   public static CmOrigin fromString(String type) {
      CmOrigin toReturn = CmOrigin.UNKNOWN;
      if (Strings.isValid(type)) {
         String toMatch = type.toUpperCase();
         if (CmOrigin.ATS.name().equals(toMatch)) {
            toReturn = CmOrigin.ATS;
         } else if (toMatch.startsWith("B")) {
            toReturn = CmOrigin.BUGZILLA;
         }
      }
      return toReturn;
   }
}
