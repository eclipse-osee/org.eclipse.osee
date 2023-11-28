/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * A collection of {@link Collector} implementations for various containers in the
 * {@link org.eclipse.osee.framework.jdk.core} project.
 *
 * @author Loren K. Ashley
 */

public final class Collectors {

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private Collectors() {
   }

   /**
    * Returns a {@link Collector} which collects the value extracted with <code>valueMapper</code> and stores it into a
    * {@link MapSet} using the key extracted with <code>keyMapper</code> for each element being collected.
    *
    * @param <T> the type of the elements being collected.
    * @param <K> the type for the returned {@link MapSet} keys.
    * @param <V> the type for the returned {@Link MapSet} values.
    * @param keyMapper a {@link Function} that takes an object of &lt;T&gt; and extracts a value of type &lt;K&gt; to be
    * used as the {@link MapSet} key to store the associated value.
    * @param valueMapper a {@link Function} that takes an object of &lt;T&gt; and extracts a value of type &lt;V&gt; to
    * be stored in the {@link MapSet} with the associated key.
    * @return a {@link Collector} which collects the value extracted with <code>valueMapper</code> and stores it into a
    * {@link MapSet} using the key extracted with <code>keyMapper</code> for each element being collected.
    */

   //@formatter:off
   public static <T,K,V> java.util.stream.Collector<T,?,MapSet<K,V>>
      toMapSet
         (
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
         ) {

      return
         java.util.stream.Collector.of
                (
                   HashMapHashSet::new,
                   ( mapSet, t ) -> mapSet.putValue( keyMapper.apply( t ), valueMapper.apply( t ) ),
                   ( a, b) -> { a.putAll( b ); return a; }
                );
   }
   //@formatter:on

   /**
    * Returns a {@link Collector} which collects the value extracted with <code>valueMapper</code> and stores it into a
    * {@link ListMap} using the key extracted with <code>keyMapper</code> for each element being collected.
    *
    * @param <T> the type of the elements being collected.
    * @param <K> the type for the returned {@link ListMap} keys.
    * @param <V> the type for the returned {@Link ListMap} values.
    * @param keyMapper a {@link Function} that takes an object of &lt;T&gt; and extracts a value of type &lt;K&gt; to be
    * used as the {@link ListMap} key to store the associated value.
    * @param valueMapper a {@link Function} that takes an object of &lt;T&gt; and extracts a value of type &lt;V&gt; to
    * be stored in the {@link ListMap} with the associated key.
    * @return a {@link Collector} which collects the value extracted with <code>valueMapper</code> and stores it into a
    * {@link ListMap} using the key extracted with <code>keyMapper</code> for each element being collected.
    */

   //@formatter:off
   public static <T,K,V> java.util.stream.Collector<T,?,ListMap<K,V>>
      toListMap
         (
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
         ) {

      return
         java.util.stream.Collector.of
                (
                   ListMap::new,
                   ( listMap, t ) -> listMap.put( keyMapper.apply( t ), valueMapper.apply( t ) ),
                   ( a, b) -> { a.putAll( b ); return a; }
                );
   }
   //@formatter:on

   /**
    * Returns a {@link Collector} which collects the values extracted from the elements being collected into a pair of
    * lists. The value extractor {@link Function}s return an {@link Optional} containing the value extracted from the
    * element being collected. The extraction {@link Function}s may return an empty {@link Optional} when the element
    * being collected does not contain a value for the list associated with the value extraction {@link Function}. When
    * a value returned from the value extraction {@link Function} is an empty {@link Optional}, no value is accumulated
    * into the list associated with the value extraction function.
    *
    * @param <T> the type of the elements being collected.
    * @param <A> the type of the elements stored into list A.
    * @param <B> the type of the elements stored into list B.
    * @param listAValueExtractor a {@link Function} that takes an object of type &lt;T&gt; and extracts a value of type
    * &lt;A&gt; to be stored in the list A.
    * @param listBValueExtractor a {@link Function} that takes an object of type &lt;T&gt; and extracts a value of type
    * &lt;B&gt; to be stored in the list B.
    * @return a {@link Collector} which collects the values extracted by the value extractors into a pair of lists.
    */

   //@formatter:off
   public static<T,A,B> java.util.stream.Collector<T, ?, Pair<List<A>,List<B>>>
      toListPair
         (
            Function<? super T, ? extends Optional<A>> listAValueExtractor,
            Function<? super T, ? extends Optional<B>> listBValueExtractor
         ) {

      return
         java.util.stream.Collector.of
            (
               () -> new Pair<>(new LinkedList<>(),new LinkedList<>() ),
               ( listPair, t ) ->
               {
                  listAValueExtractor.apply( t ).ifPresent( listPair.getFirst()::add );
                  listBValueExtractor.apply( t ).ifPresent( listPair.getSecond()::add );
               },
               ( leftListPair, rightListPair ) ->
               {
                  leftListPair.getFirst().addAll( rightListPair.getFirst() );
                  leftListPair.getSecond().addAll( rightListPair.getSecond() );
                  return leftListPair;
               }
            );
   }
   //@formatter:off

}

/* EOF */
