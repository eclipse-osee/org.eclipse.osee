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

import java.util.regex.Pattern;

/**
 * @author John Misinco
 */
public class TokenDelimiterMatch implements QueryOption {
   public static final TokenDelimiterMatch EXACT = new TokenDelimiterMatch(Pattern.compile(""));
   public static final TokenDelimiterMatch WHITESPACE = new TokenDelimiterMatch(Pattern.compile("\\s+"));
   public static final TokenDelimiterMatch ANY = new TokenDelimiterMatch(Pattern.compile("(\\W|_)+"));

   private final Pattern pattern;

   private TokenDelimiterMatch(Pattern pattern) {
      this.pattern = pattern;
   }

   public Pattern getPattern() {
      return pattern;
   }

   @Override
   public void accept(OptionVisitor visitor) {
      visitor.asTokenDelimiterMatch(this);
   }

   public static TokenDelimiterMatch custom(String regex) {
      return new TokenDelimiterMatch(Pattern.compile(regex));
   }

}
