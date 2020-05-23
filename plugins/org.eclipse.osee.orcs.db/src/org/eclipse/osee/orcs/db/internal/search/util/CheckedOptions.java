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

package org.eclipse.osee.orcs.db.internal.search.util;

import org.eclipse.osee.framework.core.enums.QueryOption;

/**
 * @author John Misinco
 */
public class CheckedOptions {

   private QueryOption orderType;
   private QueryOption caseType;
   private QueryOption countType;
   private QueryOption delimiter;
   private QueryOption exists;

   public CheckedOptions(QueryOption... options) {
      initialize(options);
   }

   private void initialize(QueryOption... options) {
      orderType = QueryOption.TOKEN_MATCH_ORDER__ANY;
      caseType = QueryOption.CASE__IGNORE;
      countType = QueryOption.TOKEN_COUNT__IGNORE;
      delimiter = QueryOption.TOKEN_DELIMITER__ANY;

      for (QueryOption option : options) {
         switch (option) {
            case CASE__MATCH:
            case CASE__IGNORE:
               caseType = option;
               break;
            case TOKEN_COUNT__MATCH:
            case TOKEN_COUNT__IGNORE:
               countType = option;
               break;
            case TOKEN_DELIMITER__EXACT:
            case TOKEN_DELIMITER__WHITESPACE:
            case TOKEN_DELIMITER__ANY:
               delimiter = option;
               break;
            case TOKEN_MATCH_ORDER__ANY:
            case TOKEN_MATCH_ORDER__MATCH:
               orderType = option;
               break;
            default:

         }
      }
   }

   public QueryOption getOrderType() {
      return orderType;
   }

   public QueryOption getCaseType() {
      return caseType;
   }

   public QueryOption getCountType() {
      return countType;
   }

   public QueryOption getDelimiter() {
      return delimiter;
   }

   public QueryOption getExists() {
      return exists;
   }

};