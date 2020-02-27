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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class EmailUtil {

   private static Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");

   public static boolean isEmailValid(String email) {
      return email != null && addressPattern.matcher(email).matches();
   }

}
