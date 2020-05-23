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

package org.eclipse.osee.framework.core.enums;

/**
 * @author John Misinco
 */
public enum QueryOption {
   CASE__MATCH,
   CASE__IGNORE,

   TOKEN_COUNT__MATCH,
   TOKEN_COUNT__IGNORE,

   // how to tokenize the search
   TOKEN_DELIMITER__EXACT,
   TOKEN_DELIMITER__WHITESPACE,
   TOKEN_DELIMITER__ANY,

   // matching the token order
   TOKEN_MATCH_ORDER__ANY,
   TOKEN_MATCH_ORDER__MATCH;

   public static QueryOption getTokenOrderType(boolean matchOrder) {
      return matchOrder ? TOKEN_MATCH_ORDER__MATCH : TOKEN_MATCH_ORDER__ANY;
   }

   public static QueryOption getCaseType(boolean isCaseSensitive) {
      return isCaseSensitive ? CASE__MATCH : CASE__IGNORE;
   }

   public static final QueryOption[] CONTAINS_MATCH_OPTIONS = {
      QueryOption.CASE__IGNORE,
      QueryOption.TOKEN_MATCH_ORDER__MATCH,
      QueryOption.TOKEN_DELIMITER__ANY,
      QueryOption.TOKEN_COUNT__IGNORE};

   public static final QueryOption[] EXACT_MATCH_OPTIONS =
      {QueryOption.TOKEN_COUNT__MATCH, QueryOption.TOKEN_DELIMITER__EXACT, QueryOption.TOKEN_MATCH_ORDER__MATCH};

}
