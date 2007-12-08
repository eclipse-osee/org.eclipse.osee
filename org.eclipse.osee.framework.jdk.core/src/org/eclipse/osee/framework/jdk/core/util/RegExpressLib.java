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
 * @author Andy Jury
 */

public class RegExpressLib {

   public static final Pattern findRegExpressNotInComment(String str) {

      String notInComment = "(?:/\\*.*?\\*/)";

      return Pattern.compile(notInComment + "|" + str, Pattern.DOTALL);
   }

   public static final Pattern findRegExpressNotInQuotes(String str) {

      String notInQuotes = "(?:\".*?[^\\\\]\")";

      return Pattern.compile(notInQuotes + "|" + str);
   }

   public static final Pattern findCMathFunctionNotInQuotes(String str) {

      return findRegExpressNotInQuotes("[^.\\w](" + str + "\\s*?)\\(.*?");
   }

}
