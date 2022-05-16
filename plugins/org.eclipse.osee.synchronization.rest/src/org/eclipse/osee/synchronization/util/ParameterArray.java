/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.synchronization.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class contains various static methods for validating arrays.
 *
 * @author Loren K. Ashley
 */

public class ParameterArray {

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private ParameterArray() {
   }

   /**
    * Validates the array is non-null, has a length within the inclusive bounds, and that all elements of the array are
    * non-null.
    *
    * @param objects the array to validate.
    * @param minSize the minimum number of array elements allowed.
    * @param maxSize the maximum number of array elements allowed.
    * @return <code>true</code> when validation is successful; otherwise, <code>false</code>.
    */

   public static boolean validateNonNullAndSize(Object[] objects, int minSize, int maxSize) {

      /*
       * Assert check limits are sane
       */

      assert (minSize >= 0) && (maxSize >= minSize);

      /*
       * Validate array is non-null and within size limits
       */

      if (Objects.isNull(objects) || (objects.length < minSize) || (objects.length > maxSize)) {
         return false;
      }

      /*
       * Validate members are non-null
       */

      for (int i = 0; i < objects.length; i++) {
         if (Objects.isNull(objects[i])) {
            return false;
         }
      }

      /*
       * Array is OK
       */

      return true;
   }

   /**
    * Validates the array is non-null, has a length greater than or equal to the minimum size, and that all elements of
    * the array are non-null.
    *
    * @param objects the array to validate.
    * @param minSize the minimum number of array elements allowed.
    * @return <code>true</code> when validation is successful; otherwise, <code>false</code>.
    */

   public static boolean validateNonNullAndSize(Object[] objects, int minSize) {

      /*
       * Assert check limits are sane
       */

      assert (minSize >= 0);

      /*
       * Validate array is non-null and within size limits
       */

      if (Objects.isNull(objects) || (objects.length < minSize)) {
         return false;
      }

      /*
       * Validate members are non-null
       */

      for (int i = 0; i < objects.length; i++) {
         if (Objects.isNull(objects[i])) {
            return false;
         }
      }

      /*
       * Array is OK
       */

      return true;
   }

   /**
    * Validates the array according to the following rules:
    * <ul>
    * <li>the array cannot be <code>null</code>,</li>
    * <li>the array length is within the inclusive bounds,</li>
    * <li>each element is non-null, and</li>
    * <li>each element passes validation with the <code>elementValidator</code> from the corresponding index.</li>
    * </ul>
    *
    * @param objects the array to validate.
    * @param minSize the minimum number of array elements allowed.
    * @param maxSize the maximum number of array elements allowed.
    * @param elementValidator an array of predicates used to validate the array elements.
    * @return <code>true</code> when validation is successful; otherwise, <code>false</code>.
    */

   public static boolean validateNonNullSizeAndElements(Object[] objects, int minSize, int maxSize, Function<Object, Boolean>[] elementValidator) {

      /*
       * Assert check limits are sane
       */

      //@formatter:off
      assert
            (minSize >= 0)
         && (maxSize >= minSize)
         && ParameterArray.validateNonNullAndSize
               (
                  elementValidator,
                  maxSize              /* minimum size of elementValidator array */
               );
      //@formatter:on

      /*
       * Validate array is non-null and within size limits
       */

      if (Objects.isNull(objects) || (objects.length < minSize) || (objects.length > maxSize)) {
         return false;
      }

      /*
       * Validate members
       */

      for (int i = 0; i < objects.length; i++) {

         if (!elementValidator[i].apply(objects[i])) {
            return false;
         }
      }

      /*
       * Array is OK
       */

      return true;
   }

   /**
    * Validates the array according to the following rules:
    * <ul>
    * <li>when the minimum size is greater than zero, the array cannot be <code>null</code> or empty,</li>
    * <li>the array length is within the inclusive bounds,</li>
    * <li>each element is non-null, and</li>
    * <li>each element passes validation with the <code>elementValidator</code> from the corresponding index.</li>
    * </ul>
    *
    * @param objects the array to validate.
    * @param minSize the minimum number of array elements allowed.
    * @param maxSize the maximum number of array elements allowed.
    * @param elementValidator an array of predicates used to validate the array elements.
    * @return <code>true</code> when validation is successful; otherwise, <code>false</code>.
    */

   public static boolean validateSizeAndElements(Object[] objects, int minSize, int maxSize, Function<Object, Boolean>[] elementValidator) {

      /*
       * Assert check limits are sane
       */

      //@formatter:off
      assert
            (minSize >= 0)
         && (maxSize >= minSize)
         && ParameterArray.validateNonNullAndSize
               (
                  elementValidator,
                  maxSize             /* minimum size of elementValidator array */
               );
      //@formatter:on

      /*
       * When minSize is zero, a null or empty array is OK
       */

      if ((minSize == 0) && (Objects.isNull(objects) || (objects.length == 0))) {
         return true;
      }

      /*
       * When minSize is non-zero, validate array is non-null and within size limits
       */

      if (Objects.isNull(objects) || (objects.length < minSize) || (objects.length > maxSize)) {
         return false;
      }

      /*
       * Validate members
       */

      for (int i = 0; i < objects.length; i++) {

         if (!elementValidator[i].apply(objects[i])) {
            return false;
         }
      }

      /*
       * Array is OK
       */

      return true;
   }

   /**
    * Validates the array according to the following rules:
    * <ul>
    * <li>when the minimum size is greater than zero, the array cannot be <code>null</code> or empty,</li>
    * <li>the array length is within the inclusive bounds,</li>
    * <li>each element is non-null, and
    * <li>
    * <li>each element is of the specified type.</li>
    * </ul>
    *
    * @param objects the array to validate.
    * @param minSize the minimum number of array elements allowed.
    * @param maxSize the maximum number of array elements allowed.
    * @param type the object in each array element is expected to be an instance of this type.
    * @return <code>true</code> when validation is successful; otherwise, <code>false</code>.
    */

   public static boolean validateSizeAndType(Object[] objects, int minSize, int maxSize, Class<?> type) {

      /*
       * Assert check limits are sane
       */

      //@formatter:off
      assert
            (minSize >= 0)
         && (maxSize >= minSize)
         && Objects.nonNull( type );
      //@formatter:on

      /*
       * When minSize is zero, a null or empty array is OK
       */

      if ((minSize == 0) && (Objects.isNull(objects) || (objects.length == 0))) {
         return true;
      }

      /*
       * When minSize is non-zero, validate array is non-null and within size limits
       */

      if (Objects.isNull(objects) || (objects.length < minSize) || (objects.length > maxSize)) {
         return false;
      }

      /*
       * Validate members are non-null and the correct type
       */

      for (int i = 0; i < objects.length; i++) {

         if (Objects.isNull(objects[i]) || !type.isInstance(objects[i])) {
            return false;
         }
      }

      /*
       * Array is OK
       */

      return true;
   }

}

/* EOF */
