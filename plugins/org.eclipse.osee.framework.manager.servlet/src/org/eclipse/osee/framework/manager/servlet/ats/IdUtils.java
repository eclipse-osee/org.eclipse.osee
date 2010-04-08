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
package org.eclipse.osee.framework.manager.servlet.ats;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Roberto E. Escobar
 */
public final class IdUtils {

   private static final Pattern legacyIDPattern = Pattern.compile("\\d+");
   private static final Matcher legacyIDMatcher = legacyIDPattern.matcher("");

   private IdUtils() {
   }

   public static boolean isValidHRID(String hrid) {
      return HumanReadableId.isValid(hrid);
   }

   public static boolean isValidGUID(String guid) {
      return GUID.isValid(guid);
   }

   public static boolean isValidLegacyId(String legacyId) {
      legacyIDMatcher.reset(legacyId);
      return legacyIDMatcher.matches();
   }
}
