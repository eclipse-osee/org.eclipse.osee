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
package org.eclipse.osee.framework.jdk.core.util;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class Conditions {

   private Conditions() {
      // Utility Class
   }

   /**
    * @return true if any of the objects are equal to the equalTo object, otherwise returns false.
    */
   public static boolean in(Object equalTo, Object... objects) {
      for (Object object : objects) {
         if (equalTo.equals(object)) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return false if any of the parameters are null, otherwise returns true.
    */
   public static boolean notNull(Object... objects) {
      for (Object object : objects) {
         if (object == null) {
            return false;
         }
      }
      return true;
   }

   /**
    * @return true if any of the parameters are null, otherwise returns false.
    */
   public static boolean anyNull(Object... objects) {
      for (Object object : objects) {
         if (object == null) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return false if the parameter is null or empty, otherwise return true
    */
   public static boolean hasValues(Collection<?> toCheck) {
      return toCheck != null && !toCheck.isEmpty();
   }

   /**
    * @return false if the parameter is null or empty, otherwise return true
    */
   public static boolean hasValues(Object[] toCheck) {
      return toCheck != null && toCheck.length > 0;
   }

   /**
    * @return true if all of the parameters are null, otherwise returns false. Also returns true when objects is an
    * empty array
    */
   public static boolean allNull(Object... objects) {
      for (Object object : objects) {
         if (object != null) {
            return false;
         }
      }
      return true;
   }

   public static void checkNotNull(Object object, String objectName) throws OseeCoreException {
      if (object == null) {
         throw new OseeArgumentException("%s cannot be null", objectName);
      }
   }

   public static void checkNotNull(Object object, String objectName, String details, Object... data) throws OseeCoreException {
      if (object == null) {
         String message = String.format(details, data);
         throw new OseeArgumentException("%s cannot be null - %s", objectName, message);
      }
   }

   public static void checkNotNullOrEmpty(String object, String objectName) throws OseeCoreException {
      checkNotNull(object, objectName);
      if (object.length() == 0) {
         throw new OseeArgumentException("%s cannot be empty", objectName);
      }
   }

   public static void checkNotNullOrEmpty(Object[] array, String objectName) throws OseeCoreException {
      checkNotNull(array, objectName);
      if (array.length <= 0) {
         throw new OseeArgumentException("%s cannot be empty", objectName);
      }
   }

   public static void checkNotNullOrEmpty(Collection<? extends Object> collection, String objectName) throws OseeCoreException {
      checkNotNull(collection, objectName);
      if (collection.isEmpty()) {
         throw new OseeArgumentException("%s cannot be empty", objectName);
      }
   }

   public static void checkNotNullOrEmpty(String object, String objectName, String details, Object... data) throws OseeCoreException {
      checkNotNull(object, objectName, details, data);
      if (object.length() == 0) {
         String message = String.format(details, data);
         throw new OseeArgumentException("%s cannot be empty - %s", objectName, message);
      }
   }

   public static void checkExpressionFailOnTrue(boolean result, String message, Object... data) throws OseeCoreException {
      if (result) {
         throw new OseeArgumentException(message, data);
      }
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
               throw new OseeArgumentException(message, data);
            }
         }
      } else {
         throw new OseeArgumentException("object is not an array or a collection");
      }
   }

   public static void checkNotNullOrEmptyOrContainNull(Collection<? extends Object> collection, String objectName) throws OseeCoreException {
      checkNotNullOrEmpty(collection, objectName);
      for (Object object : collection) {
         checkNotNull(object, objectName);
      }
   }

   public static void checkNotNullOrContainNull(Collection<? extends Object> collection, String objectName) throws OseeCoreException {
      checkNotNull(collection, objectName);
      for (Object object : collection) {
         checkNotNull(object, objectName);
      }
   }

   public static void assertEquals(int value1, int value2, String message) {
      checkExpressionFailOnTrue(value1 != value2, message + " - Expected %d; Actual %d", value1, value2);
   }

   public static void assertEquals(int value1, int value2) {
      assertEquals(value1, value2, "");
   }

   public static void assertEquals(String expected, String actual) {
      assertTrue(expected.equals(actual), "Expected %1; Actual %2", expected, actual);
   }

   public static void assertTrue(boolean value, String message, Object... data) {
      if (!value) {
         throw new OseeArgumentException(message, data);
      }
   }

   public static void assertNotNull(Object obj, String message, Object... data) {
      if (obj == null) {
         throw new OseeArgumentException(message, data);
      }
   }
}
