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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author David Diepenbrock
 */
public class Collections {
   public static Object[] EMPTY_ARRAY = new Object[0];

   public static Collection<String> fromString(String string, String seperator) {
      return Arrays.asList(string.split(seperator));
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
   public static String toString(Collection<?> items, String prefix, String separator, String suffix) {
      StringBuilder strB = new StringBuilder();

      if (prefix != null) {
         strB.append(prefix);
      }

      boolean first = true;
      for (Object item : items) {
         if (first) {
            first = false;
         } else {
            strB.append(separator);
         }
         strB.append(String.valueOf(item));
      }

      if (suffix != null) {
         strB.append(suffix);
      }

      return strB.toString();
   }

   public static String toString(String separator, Object... items) {
      return toString(separator, Arrays.asList(items));
   }

   public static String toString(String separator, Collection<?> c) {
      return toString(c, null, separator, null);
   }

   public static <A> List<Collection<A>> subDivide(List<A> collection, int size) {
      List<Collection<A>> result = new ArrayList<Collection<A>>();
      for (int i = 0; i < collection.size() / size + 1; i++) {
         int maxLength;
         if (i * size + size > collection.size()) {
            maxLength = collection.size();
         } else {
            maxLength = i * size + size;
         }
         List<A> sublist = new ArrayList<A>();
         for (int j = i * size; j < maxLength; j++) {
            sublist.add(collection.get(j));
         }
         result.add(sublist);
      }
      return result;
   }

   public static <A> Collection<A> unique(Collection<A> collection) {
      Set<A> result = new HashSet<A>();
      result.addAll(collection);
      return result;
   }

   /**
    * The resultant set is those elements in superSet which are not in the subSet
    * 
    * @return Return complement list reference
    */
   public static <T> List<T> setComplement(Collection<T> superSet, Collection<T> subList) {
      ArrayList<T> complement = new ArrayList<T>(superSet.size());
      for (T obj : superSet) {
         if (!subList.contains(obj)) {
            complement.add(obj);
         }
      }
      return complement;
   }

   /**
    * @return The intersection of two sets A and B is the set of elements common to A and B
    */
   public static <T> ArrayList<T> setIntersection(Collection<T> listA, Collection<T> listB) {
      ArrayList<T> intersection = new ArrayList<T>(listA.size());

      for (T obj : listA) {
         if (listB.contains(obj)) {
            intersection.add(obj);
         }
      }
      return intersection;
   }

   /**
    * Returns the unique union of the given lists
    */
   public static <T> Set<T> setUnion(Collection<T>... lists) {
      Set<T> union = new HashSet<T>(lists[0].size() * 2);

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
         set = new LinkedHashSet<T>();
         set.addAll(collection);
      }
      return set;
   }

   /**
    * Convert an aggregate list of objects into a List
    */
   public static <T> List<T> getAggregate(T... objects) {
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
         return new ArrayList<Object>(items);
      }
   }

   private static ArrayList<Object> recursiveAggregateTree(List<Object> items, int maxPerList) {
      if (items.size() > maxPerList) {
         ArrayList<Object> aggregateList = new ArrayList<Object>(maxPerList);
         ArrayList<Object> childList = null;

         for (Object item : items) {
            if (childList == null || childList.size() == maxPerList) {
               childList = new ArrayList<Object>(maxPerList);
               aggregateList.add(childList);
            }
            childList.add(item);
         }
         childList.trimToSize();

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
    * Cast objects to clazz
    * 
    * @param castOption if ALL, cast all and throw exception if cast fails; if MATCHING, only cast those of type clazz
    */
   @SuppressWarnings("unchecked")
   private static <A extends Object> List<A> cast(Class<A> clazz, Collection<? extends Object> objects, CastOption castOption) {
      List<A> results = new ArrayList<A>(objects.size());
      for (Object object : objects) {
         if (castOption == CastOption.ALL || castOption == CastOption.MATCHING && clazz.isAssignableFrom(object.getClass())) {
            results.add((A) object);
         }
      }
      return results;
   }

   /**
    * Cast objects to clazz
    */
   @SuppressWarnings("unchecked")
   public static <A> List<A> castAll(Collection<?> objects) {
      List<A> results = new ArrayList<A>(objects.size());
      for (Object object : objects) {
         results.add((A) object);
      }
      return results;
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

   public static <T extends Object> Collection<T> asCollection(T arg) {
      Collection<T> ret = new ArrayList<T>();
      ret.add(arg);
      return ret;
   }

   public static <A extends Object> boolean moveItem(List<A> currentOrder, A itemToAdd, A targetItem, boolean insertAfter) {
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
}