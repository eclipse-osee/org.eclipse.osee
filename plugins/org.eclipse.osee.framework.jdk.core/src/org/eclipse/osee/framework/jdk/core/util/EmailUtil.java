/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class EmailUtil {

   private static Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");

   public static boolean isEmailValid(String email) {
      return addressPattern.matcher(email).matches();
   }

}
