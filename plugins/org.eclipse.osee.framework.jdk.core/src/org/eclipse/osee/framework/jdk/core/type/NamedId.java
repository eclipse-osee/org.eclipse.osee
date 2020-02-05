/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan D. Brooks
 */
public interface NamedId extends Named, Id {

   public static NamedId SENTINEL = new NamedIdBase(-1L, Named.SENTINEL);
   public static Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");

   default String toStringWithId() {
      return toStringWithId(this);
   }

   /**
    * @param value as [name]-[id]
    */
   public static NamedId getFromStringWithid(String value) {
      NamedId token = NamedId.SENTINEL;
      Matcher matcher = nameIdPattern.matcher(value);
      if (matcher.find()) {
         Long id = Long.valueOf(matcher.group(2));
         token = new NamedIdBase(id, matcher.group(1));
      }
      return token;
   }

   /**
    * @return as [name]-[id]
    */
   public static String toStringWithId(NamedId namedId) {
      return String.format("[%s]-[%s]", namedId.getName(), namedId.getIdString());
   }

}
