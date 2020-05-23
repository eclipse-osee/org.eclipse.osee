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

package org.eclipse.osee.ote.ui.define.utilities;

/**
 * @author Roberto E. Escobar
 */
public class HtmlStringUtils {
   private HtmlStringUtils() {

   }

   public static String addSingleQuotes(String value) {
      return "'" + value + "'";
   }

   public static String escapeString(String value) {
      return value.replaceAll("'", "\\'");
   }
}
