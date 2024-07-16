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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author David Diepenbrock
 * @author Ryan D. Brooks
 */
public class Collections {
   public static Object[] EMPTY_ARRAY = new Object[0];

   public static List<String> fromString(String rawValue, String seperator) {
      return fromString(rawValue, seperator, Function.identity());
   }

   public static <R> List<R> fromString(String rawValue, Function<String, R> function) {
      return fromString(rawValue, ",", function);
   }

   public static <R> List<R> fromString(String rawValue, String seperator, Function<String, R> function) {
      List<R> toReturn;
      if (Strings.isValid(rawValue)) {
         String[] entries = rawValue.split(seperator);
         toReturn = new ArrayList<>(entries.length);
         for (String entry : entries) {
            String token = entry.trim();
            if (Strings.isValid(token)) {
               toReturn.add(function.apply(token));
            }
         }
      } else {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   /**
    * A flexible alternative for converting a Collection to a String.
    *
    * @param items The Collection to convert to a String
    * @param prefix The String to place at the beginning of the returned String
    * @param separator The String to place in between elements of the Collection c.
    * @param suffix The String to place at the end of the returned String
    * @return A String which starts with 'start', followed by the elements in the Collection c separated by 'separator',
    * ending with 'end'.
    */
   public static <T> String toString(Iterable<T> items, String prefix, String separator, String suffix) {
      return toString(items, prefix, separator, suffix, String::valueOf);
   }

   public static <T> String toString(Iterable<T> items, String separator, Function<T, String> function) {
      return toString(items, null, separator, null, function);
   }

   public static <T> String toString(Iterable<T> items, String prefix, String separator, String suffix,
      Function<T, String> function) {
      StringBuilder strB = new StringBuilder();
      appendToBuilder(items, prefix, separator, suffix, function, strB);
      return strB.toString();
   }

   public static <T> void appendToBuilder(Iterable<T> items, String separator, StringBuilder strB) {
      appendToBuilder(items, null, separator, null, String::valueOf, strB);
   }

   public static <T> void appendToBuilder(Iterable<T> items, String prefix, String separator, String suffix,
      Function<T, String> function, StringBuilder strB) {
      if (items == null) {
         return;
      }

      if (prefix != null) {
         strB.append(prefix);
      }

      boolean first = true;
      for (T item : items) {
         if (first) {
            first = false;
         } else {
            strB.append(separator);
         }
         strB.append(function.apply(item));
      }

      if (suffix != null) {
         strB.append(suffix);
      }
   }

   public static String toString(String[] items, String separator) {
      return toString(items, null, separator, null);
   }

   public static String toString(String[] items, String prefix, String separator, String suffix) {
      if (items == null) {
         return "";
      }
      StringBuilder strB = new StringBuilder();

      if (prefix != null) {
         strB.append(prefix);
      }

      boolean first = true;
      for (String item : items) {
         if (first) {
            first = false;
         } else {
            strB.append(separator);
         }
         strB.append(item);
      }

      if (suffix != null) {
         strB.append(suffix);
      }

      return strB.toString();
   }

   public static String toString(String separator, Object... items) {
      return toString(separator, Arrays.asList(items));
   }

   public static String toString(String separator, Iterable<?> c) {
      return toString(c, null, separator, null);
   }

   public static <A> List<Collection<A>> subDivide(List<A> collection, int size) {
      List<Collection<A>> result = new ArrayList<>();
      for (int i = 0; i < collection.size() / size + 1; i++) {
         int maxLength;
         if (i * size + size > collection.size()) {
            maxLength = collection.size();
         } else {
            maxLength = i * size + size;
         }
         List<A> sublist = new ArrayList<>();
         for (int j = i * size; j < maxLength; j++) {
            sublist.add(collection.get(j));
         }
         result.add(sublist);
      }
      return result;
   }

   public static <A> Collection<A> unique(Collection<A> collection) {
      Set<A> result = new HashSet<>();
      result.addAll(collection);
      return result;
   }

   /**
    * <p>
    * <p>
    * <br/>
    * Noted:<br/>
    * A - B
    * </p>
    * <p>
    * <br/>
    * Examples:<br/>
    * { 1, 2, 3 } - { 1, 4, 3 } = { 2 } <br/>
    * <br/>
    * { 1, 4, 3 } - { 1, 2, 3 } = { 4 }
    * </p>
    * Meaning:
    * <ul>
    * <li>elements outside of B</li>
    * <li>all elements unique to A</li>
    * </ul>
    * </p>
    *
    * @return relative set complement of B in A.
    */
   public static <T> List<T> setComplement(Collection<? extends T> set_A, Collection<? extends T> set_B) {
      ArrayList<T> complement = new ArrayList<>(set_A.size());
      for (T obj : set_A) {
         if (!set_B.contains(obj)) {
            complement.add(obj);
         }
      }
      return complement;
   }

   /**
    * @return The intersection of two sets A and B is the set of elements common to A and B
    */
   public static <T> ArrayList<T> setIntersection(Collection<T> listA, Collection<T> listB) {
      ArrayList<T> intersection = new ArrayList<>(listA.size());

      for (T obj : listA) {
         if (listB.contains(obj)) {
            intersection.add(obj);
         }
      }
      return intersection;
   }

   /**
    * @return union of unique elements from the given lists
    */
   public static <T> Set<T> setUnion(Collection<T>... lists) {
      Set<T> union = new HashSet<>(lists[0].size() * 2);

      for (int x = 0; x < lists.length; x++) {
         union.addAll(lists[x]);
      }
      return union;
   }

   /**
    * Return true if same objects exist in listA and listB
    */
   public static <T> boolean isEqual(Collection<T> listA, Collection<T> listB) {
      if (listA.size() != listB.size()) {
         return false;
      }
      return listA.size() == setIntersection(listA, listB).size();
   }

   public static <T> Set<T> toSet(Collection<T> collection) {
      Set<T> set = null;
      if (collection instanceof Set) {
         set = (Set<T>) collection;
      } else {
         set = new LinkedHashSet<>();
         set.addAll(collection);
      }
      return set;
   }

   /**
    * Convert objects to HashSet
    */
   public static <T> Set<T> asHashSet(T... objects) {
      Set<T> objs = new HashSet<>();
      if (objects != null) {
         for (T obj : objects) {
            objs.add(obj);
         }
      }
      return objs;
   }

   public static <T> List<T> asList(T... objects) {
      List<T> objs = new ArrayList<T>();
      if (objects != null) {
         for (T obj : objects) {
            objs.add(obj);
         }
      }
      return objs;
   }

   public static List<Object> getAggregateTree(List<Object> items, int maxPerList) {
      if (items == null) {
         throw new IllegalArgumentException("items can not be null");
      }
      if (maxPerList < 2) {
         throw new IllegalArgumentException("maxPerList can not be less than 2");
      }

      if (items.size() > maxPerList) {
         return recursiveAggregateTree(items, maxPerList);
      } else {
         return new ArrayList<>(items);
      }
   }

   private static ArrayList<Object> recursiveAggregateTree(List<Object> items, int maxPerList) {
      if (items.size() > maxPerList) {
         ArrayList<Object> aggregateList = new ArrayList<>(maxPerList);
         ArrayList<Object> childList = null;

         for (Object item : items) {
            if (childList == null || childList.size() == maxPerList) {
               childList = new ArrayList<>(maxPerList);
               aggregateList.add(childList);
            }
            childList.add(item);
         }
         if (childList != null) {
            childList.trimToSize();
         }

         aggregateList = recursiveAggregateTree(aggregateList, maxPerList);

         aggregateList.trimToSize();

         return aggregateList;
      } else {
         // This is a safe blind cast since only subsequent calls of this method will end up here
         // and this method always uses ArrayList<Object>
         return (ArrayList<Object>) items;
      }
   }

   public static enum CastOption {
      MATCHING,
      ALL
   };

   /**
    * @param castOption if ALL, cast all and throw exception if cast fails; if MATCHING, only cast those of type clazz
    */
   @SuppressWarnings("unchecked")
   private static <A extends Object> List<A> cast(Class<A> clazz, Collection<? extends Object> objects,
      CastOption castOption) {
      List<A> results = new ArrayList<>(objects.size());
      for (Object object : objects) {
         if (object != null) {
            if (castOption == CastOption.ALL || castOption == CastOption.MATCHING && clazz.isAssignableFrom(
               object.getClass())) {
               results.add((A) object);
            }
         }
      }
      return results;
   }

   /**
    * @Deprecated Inherently not type safe. Use cast(Collection<F> from) or castMatching() instead
    */
   public static <A> List<A> castAll(Collection<?> objects) {
      List<A> results = new ArrayList<>(objects.size());
      for (Object object : objects) {
         results.add((A) object);
      }
      return results;
   }

   /**
    * Type safe method for creating a list of elements of a given type from a collection of elements of a subtype
    *
    * @param from
    * @return
    */
   public static <F extends T, T> List<T> cast(Collection<F> from) {
      return transform(from, r -> r);
   }

   /**
    * Unchecked cast objects to clazz; CastClassException will occur when object sent in does not match clazz<br>
    * <br>
    * Use when all objects are expected to be of type class and exception is desired if not
    */
   public static <A extends Object> List<A> castAll(Class<A> clazz, Collection<? extends Object> objects) {
      return cast(clazz, objects, CastOption.ALL);
   }

   /**
    * Cast objects matching class, ignore rest; no ClassCastException will occur<br>
    * <br>
    * Use when objects may contain classes that are not desired
    */
   public static <A extends Object> List<A> castMatching(Class<A> clazz, Collection<? extends Object> objects) {
      return cast(clazz, objects, CastOption.MATCHING);
   }

   public static <A extends Object> boolean moveItem(List<A> currentOrder, A itemToAdd, A targetItem,
      boolean insertAfter) {
      int newIndex = currentOrder.indexOf(targetItem);
      if (newIndex < 0 || newIndex > currentOrder.size() - 1) {
         return false;
      }
      int oldIndex = currentOrder.indexOf(itemToAdd);
      if (oldIndex < 0 || oldIndex > currentOrder.size() - 1) {
         return false;
      }

      currentOrder.remove(itemToAdd);
      if (insertAfter) {
         newIndex = newIndex > oldIndex ? newIndex : newIndex + 1;
      } else {
         newIndex = newIndex > oldIndex ? newIndex - 1 : newIndex;
      }
      if (newIndex > currentOrder.size()) {
         currentOrder.add(itemToAdd);
      } else {
         currentOrder.add(newIndex, itemToAdd);
      }
      return true;
   }

   /**
    * Iterates over a collectionInput, pulls out individual elements or instances of Collection, and puts them into a
    * flattenedOutput list.
    * <p>
    * Example: <br/>
    * Collection<String> input = [[A], B, C, [D , E, [F]]]; <br/>
    * List<String> output = [A, B, C, D, E, F];
    * </p>
    */
   @SuppressWarnings("unchecked")
   public static <T> void flatten(Collection<T> input, List<T> flattenedOutput) {
      for (T item : input) {
         if (item instanceof Collection<?>) {
            flatten((Collection<T>) item, flattenedOutput);
         } else {
            flattenedOutput.add(item);
         }
      }
   }

   public static <T> Set<T> hashSet(T... items) {
      Set<T> result = new HashSet<>();
      for (T item : items) {
         result.add(item);
      }
      return result;
   }

   public static <F, T> List<T> transform(Collection<F> from, Function<F, T> function) {
      if (from.isEmpty()) {
         return java.util.Collections.emptyList();
      } else {
         List<T> toReturn = new ArrayList<>(from.size());
         for (F item : from) {
            toReturn.add(function.apply(item));
         }
         return toReturn;
      }
   }

   public static <T> T exactlyOne(Collection<T> items) {
      if (items.size() > 1) {
         throw new MultipleItemsExist("Expected exactly 1, but found %s", items.size());
      }
      if (items.size() == 0) {
         throw new OseeCoreException("Expected exactly 1, but found none");
      }
      if (items instanceof List) {
         return ((List<T>) items).get(0);
      }
      return items.iterator().next();
   }

   public static <T> T oneOrSentinel(Collection<T> items, T sentinel) {
      if (items.size() > 1) {
         throw new MultipleItemsExist("Expected at most 1, but found %s", items.size());
      } else if (items.size() == 1) {
         if (items instanceof List) {
            return ((List<T>) items).get(0);
         }
         return items.iterator().next();
      }
      return sentinel;
   }

}