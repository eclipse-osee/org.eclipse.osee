/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsScriptDslConstants {

   private OrcsScriptDslConstants() {
      // constants
   }

   public static final String FORMATTING__INDENT_STRING = "   ";

   public static final String TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm:ss a";
   public static final String TIMEZONE_ID = "UTC";

   public static final String CONVERSION_ERROR__NON_UTC_TIMESTAMP__MSG = "Invalid timestamp format";
   public static final String CONVERSION_ERROR__NON_UTC_TIMESTAMP__CODE = "invalid_timestamp__non_utc";
   public static final String CONVERSION_ERROR__BAD_TIMESTAMP_FORMAT__CODE = "invalid_timestamp__bad_format";

   public static final String COVERSION_ERROR__BAD_FORMAT_TEMPLATE__MSG =
      "Invalid timestamp format - format should be [%s] or [%s]";
   public static final String COVERSION_ERROR__DEFAULT_LOCALE_FORMAT__MSG = "Default format for the locale";

   public static final String VALIDATION_ERROR__TIMESTAMP_RANGE_INVALID__CODE = "invalid_timestamp__range";
   public static final String VALIDATION_ERROR__TIMESTAMP_RANGE_TEMPLATE__MSG =
      "Invalid timestamp range - start date [%s] is after end date [%s]";

   public static final String VALIDATION_ERROR__VARIABLE_NAME__CODE = "invalid_variable__name";
   public static final String VALIDATION_ERROR__VARIABLE_NAME__MSG = "Variable name should start with lowercase";

   public static final String VALIDATION_ERROR__AMBIGUOUS_ALIAS__CODE = "invalid_collect_stmt__ambiguous_alias";
   public static final String VALIDATION_ERROR__AMBIGUOUS_ALIAS__MSG =
      "Invalid collect statement - ambiguous name(s) detected %s.";

   public static final String VALIDATION_ERROR__INVALID_FIELD__CODE = "invalid_collect_stmt__invalid_field";
   public static final String VALIDATION_ERROR__INVALID_FIELD__MSG =
      "Invalid collect statement - invalid field(s) detected %s. The following fields are allowed: %s";

}
