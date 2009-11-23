/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Roberto E. Escobar
 */
public final class Conditions {

   private Conditions() {
   }

   public static void checkNotNull(Object object, String message) throws OseeCoreException {
      if (object == null) {
         throw new OseeArgumentException(String.format("%s cannot be null", message));
      }
   }

   public static void checkNotNullOrEmpty(String object, String message) throws OseeCoreException {
      checkNotNull(object, message);
      if (object.length() == 0) {
         throw new OseeArgumentException(String.format("%s cannot be empty", message));
      }
   }

   public static void checkExpressionFailOnTrue(boolean result, String message, Object... data) throws OseeCoreException {
      if (result) {
         throw new OseeArgumentException(String.format(message, data));
      }
   }

   public static String checkGuidCreateIfNeeded(String guid) {
      String toReturn = guid;
      if (guid == null) {
         toReturn = GUID.create();
      }
      return toReturn;
   }

   public static void checkDoesNotContainNulls(Object object, String message, Object... data) throws OseeCoreException {
      checkNotNull(object, message);
      Collection<?> toCheck = null;
      if (object instanceof Collection<?>) {
         toCheck = (Collection<?>) object;
      } else if (object instanceof Object[]) {
         toCheck = Arrays.asList((Object[]) object);
      }
      if (toCheck != null) {
         for (Object item : toCheck) {
            if (item == null) {
               throw new OseeArgumentException(String.format(message, data));
            }
         }
      } else {
         throw new OseeArgumentException("object is not an array or a collection");
      }
   }
}
