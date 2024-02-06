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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.eclipse.jdt.annotation.NonNull;
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
   public static <T,K,V> Collector<T,?,MapSet<K,V>>
      toMapSet
         (
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
         ) {

      return
         Collector.of
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
   public static <T,K,V> Collector<T,?,ListMap<K,V>>
      toListMap
         (
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper
         ) {

      return
         Collector.of
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
   public static<T,A,B> @NonNull Collector<T, ?, Pair<List<A>,List<B>>>
      toListPair
         (
            Function<? super T, ? extends Optional<A>> listAValueExtractor,
            Function<? super T, ? extends Optional<B>> listBValueExtractor
         ) {

      Collector<T, ?, Pair<List<A>,List<B>>> collector =
         Collector.of
            (
               () -> new Pair<>(new LinkedList<>(),new LinkedList<>() ),
               ( listPair, t ) ->
               {
                  listAValueExtractor.apply( t ).ifPresent( listPair.getFirstNonNull()::add );
                  listBValueExtractor.apply( t ).ifPresent( listPair.getSecondNonNull()::add );
               },
               ( leftListPair, rightListPair ) ->
               {
                  leftListPair.getFirstNonNull().addAll( rightListPair.getFirst() );
                  leftListPair.getSecondNonNull().addAll( rightListPair.getSecond() );
                  return leftListPair;
               }
            );

      return Conditions.requireNonNull( collector );
   }
   //@formatter:off

   /**
    * Returns a {@link Collector} which performs a transform on both the key and value of the {@link Map.Entry} objects in a stream and
    * collects them into a {@link Map}.
    *
    * @param <IK> the type of key in the streamed {@link Map.Entry} objects.
    * @param <IV> the type of the value in the streamed {@link Map.Entry} objects.
    * @param <OK> the input keys are transformed into objects of this type before being added to the map.
    * @param <OV> the input values are transformed into objects of this type before being added to the map.
    * @param mapSupplier a {@link Supplier} that provides the {@link Map} that will be added to by the collector.
    * @param keyMapper a {@link Function} that transforms the keys in the streamed {@link Map.Entry} objects into the keys that will be stored in the map.
    * @param valueMapper a {@link Function} that transforms the values in the streamed {@link Map.Entry} objects into the values that will be store in the map.
    * @return the {@link Collector}.
    */

   //@formatter:off
   public static <IK,IV,OK,OV> @NonNull Collector<Map.Entry<IK,IV>,?,Map<OK,OV>>
      toMap
         (
            @NonNull Supplier<Map<OK,OV>> mapSupplier,
            @NonNull Function<IK,OK> keyMapper,
            @NonNull Function<IV,OV> valueMapper
         ) {

      Objects.requireNonNull( mapSupplier );
      Objects.requireNonNull( keyMapper );
      Objects.requireNonNull( valueMapper );

      Collector<Map.Entry<IK,IV>,?,Map<OK,OV>> collector =
         java.util.stream.Collector.of
                (
                   mapSupplier,
                   ( map, mapEntry ) -> map.put
                                           (
                                              keyMapper.apply( mapEntry.getKey() ),
                                              valueMapper.apply( mapEntry.getValue() )
                                           ),
                   ( a, b) -> { a.putAll( b ); return a; }
                );

      return Conditions.requireNonNull( collector );
   }
   //@formatter:on

   /**
    * Returns a {@link Collector} which performs a transform on both the key and value of the {@link Map.Entry} objects
    * in a stream, applies a filter to each transformed key/value pair, and collects the key/value pairs passing the
    * filter into a {@link Map}.
    *
    * @param <IK> the type of key in the streamed {@link Map.Entry} objects.
    * @param <IV> the type of the value in the streamed {@link Map.Entry} objects.
    * @param <OK> the input keys are transformed into objects of this type before being added to the map.
    * @param <OV> the input values are transformed into objects of this type before being added to the map.
    * @param mapSupplier a {@link Supplier} that provides the {@link Map} that will be added to by the collector.
    * @param keyMapper a {@link Function} that transforms the keys in the streamed {@link Map.Entry} objects into the
    * keys that will be stored in the map.
    * @param valueMapper a {@link Function} that transforms the values in the streamed {@link Map.Entry} objects into
    * the values that will be store in the map.
    * @param outputFilter a {@link BiFunction} applied to each transformed key/value pairs that returns
    * <code>true</code> for the key/value pairs to be collected.
    * @return the {@link Collector}.
    */

   //@formatter:off
   public static <IK,IV,OK,OV> @NonNull Collector<Map.Entry<IK,IV>,?,Map<OK,OV>>
      toMap
         (
            @NonNull Supplier<Map<OK,OV>> mapSupplier,
            @NonNull Function<IK,OK> keyMapper,
            @NonNull Function<IV,OV> valueMapper,
            @NonNull BiPredicate<OK,OV> outputFilter
         ) {

      Objects.requireNonNull( mapSupplier );
      Objects.requireNonNull( keyMapper );
      Objects.requireNonNull( valueMapper );

      Collector<Map.Entry<IK,IV>,?,Map<OK,OV>> collector =
         java.util.stream.Collector.of
                (
                   mapSupplier,
                   ( map, mapEntry ) ->
                   {
                      var outputKey   = keyMapper.apply( mapEntry.getKey() );
                      var outputValue = valueMapper.apply( mapEntry.getValue() );

                      if( outputFilter.test( outputKey, outputValue ) ) {
                         map.put( outputKey, outputValue );
                      }
                   },
                   ( a, b) -> { a.putAll( b ); return a; }
                );

      return Conditions.requireNonNull( collector );
   }
   //@formatter:on
}

/* EOF */
