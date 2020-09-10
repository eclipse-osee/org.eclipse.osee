/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.define.api.report;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author David W. Miller
 */
public class ReportFilter {
   private final String filterRegex;

   public ReportFilter(String filterRegex) {
      this.filterRegex = filterRegex;
   }

   public Boolean filterMatches(String toMatch) {
      if (Strings.isValid(toMatch)) {
         if (toMatch.matches(filterRegex)) {
            return true;
         }
      }
      return false;
   }
}
