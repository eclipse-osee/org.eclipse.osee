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
public enum TokenDelimiterMatch implements QueryOption {
   EXACT,
   WHITESPACE,
   ANY;

   @Override
   public void accept(OptionVisitor visitor) {
      visitor.asTokenDelimiterMatch(this);
   }

   public static TokenDelimiterMatch fromString(String delimiter) {
      TokenDelimiterMatch toReturn = ANY;
      for (TokenDelimiterMatch value : values()) {
         if (value.name().equals(delimiter)) {
            toReturn = value;
            break;
         }
      }
      return toReturn;
   }

}
