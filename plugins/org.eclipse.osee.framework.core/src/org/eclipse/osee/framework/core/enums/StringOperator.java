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
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public enum StringOperator {
   EQUALS(), // Exact Match as in Strings.equals
   NOT_EQUALS(), // inverse of exact match - !Strings.equals
   CONTAINS, // Exact Match as in String.contains
   TOKENIZED_ANY_ORDER, // tokenized on special chars, then matched in any order in string
   TOKENIZED_MATCH_ORDER; // tokenized on special chars, then matched in same order in string

   public boolean isTokenized() {
      return this != NOT_EQUALS;
   }

}
