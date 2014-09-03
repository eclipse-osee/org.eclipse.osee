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
package org.eclipse.osee.framework.core.enums;

/**
 * @author John Misinco
 */
public enum QueryOption {
   CASE__MATCH,
   CASE__IGNORE,

   // these are used for attribute and relation existence
   EXISTANCE__EXISTS,
   EXISTANCE__NOT_EXISTS,

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

}
