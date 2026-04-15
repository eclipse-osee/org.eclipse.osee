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
public enum CommitType {
   FEATURE(1),
   REFINEMENT(2),
   BUG(3),
   REFACTOR(4),
   UNKNOWN(5);

   private final int weight;

   CommitType(int weight) {
      this.weight = weight;
   }

   public int getWeight() {
      return weight;
   }

   public static CommitType fromString(String type) {
      CommitType toReturn = CommitType.UNKNOWN;
      if (Strings.isValid(type)) {
         String toMatch = type.toUpperCase();
         for (CommitType commitType : CommitType.values()) {
            if (commitType.name().equals(toMatch)) {
               toReturn = commitType;
               break;
            }
         }
      }
      return toReturn;
   }
}
