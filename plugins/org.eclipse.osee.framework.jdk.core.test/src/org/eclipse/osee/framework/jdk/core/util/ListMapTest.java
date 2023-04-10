/*
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
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for implementation of the {@link ListMap}.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
public class ListMapTest {

   private static class element {
      private final String key;
      private final String value;

      element(String key, String value) {
         this.key = key;
         this.value = value;
      }

      String getKey() {
         return this.key;
      }

      String getValue() {
         return this.value;
      }
   }
   private static int LARGE_SIZE = 16 * 1024;

   private static int SMALL_SIZE = 8;

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      return
         List.of
            (
               (Object[]) new Supplier[] { () -> new ListMap<String,element>() },
               (Object[]) new Supplier[] { () -> new ListMap<String,element>(32, 0.5f, 1 * 1024 ) },
               (Object[]) new Supplier[] { () -> new ListMap<String,element>(32, 0.5f, 4 * 1024, element::getKey ) }
            );
      //@formatter:on
   }

   private boolean hasKeyExtractor;
   private ListMap<String, element> listMap;
   private final Supplier<ListMap<String, element>> listMapSupplier;

   public ListMapTest(Supplier<ListMap<String, element>> listMapSupplier) {
      this.listMapSupplier = listMapSupplier;
   }

   @Test
   public void backwardsConcurentMod() {
      var testSize = ListMapTest.LARGE_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      try {
         //@formatter:off
         @SuppressWarnings("unused")
         var resultList =
            this.listMap
               .backwardsStream( true )
               .peek
                  (
                     (element)->
                     {
                        if( element.getPosition() == 3 * 1024 ) {
                           this.listMap.put( "bomb", element.getValue() );
                        }
                     }
                  )
               .collect( Collectors.toList() );
          //@formatter:on
         Assert.assertTrue(false);
      } catch (Exception e) {
         Assert.assertTrue(true);
      }
   }

   @Test
   public void backwardsStreamTest() {
      int[] testSizeArray = {ListMapTest.SMALL_SIZE, ListMapTest.LARGE_SIZE};
      boolean[] testModeArray = {false, true};
      for (var j = 0; j < 2; j++) {
         var testSize = testSizeArray[j];
         var testMode = testModeArray[j];

         this.loadMap(testSize);
         var actualSize = this.listMap.size();
         Assert.assertEquals(testSize, actualSize);

         var resultList = this.listMap.backwardsStream(testMode).collect(Collectors.toList());

         for (var i = 0; i < testSize; i++) {
            var mapElement = resultList.get(i);
            var mapElementKey = mapElement.getKey();
            var position = mapElement.getPosition();
            var element = mapElement.getValue();
            var key = element.getKey();
            var value = element.getValue();
            var numString = Integer.toString(testSize - 1 - i);
            var testKey = "key-".concat(numString);
            var testValue = "value-".concat(numString);
            Assert.assertEquals(testKey, mapElementKey);
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            Assert.assertEquals(testSize - 1 - i, position);
         }
      }
   }

   @Test
   public void backwardsStreamTestByIndex() {
      int[] testSizeArray = {ListMapTest.SMALL_SIZE, ListMapTest.LARGE_SIZE};
      boolean[] testModeArray = {false, true};
      for (var j = 0; j < 2; j++) {
         var testSize = testSizeArray[j];
         var testMode = testModeArray[j];

         this.loadMap(testSize);
         var actualSize = this.listMap.size();
         Assert.assertEquals(testSize, actualSize);

         var streamSize = testSize >>> 1;
         var resultList = this.listMap.backwardsStream(streamSize - 1, testMode).collect(Collectors.toList());
         Assert.assertEquals(streamSize, resultList.size());

         for (var i = 0; i < streamSize; i++) {
            var mapElement = resultList.get(i);
            var mapElementKey = mapElement.getKey();
            var position = mapElement.getPosition();
            var element = mapElement.getValue();
            var key = element.getKey();
            var value = element.getValue();
            var numString = Integer.toString(streamSize - 1 - i);
            var testKey = "key-".concat(numString);
            var testValue = "value-".concat(numString);
            Assert.assertEquals(testKey, mapElementKey);
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            Assert.assertEquals(streamSize - 1 - i, position);
         }
      }
   }

   @Test
   public void backwardsStreamTestByKey() {
      int[] testSizeArray = {ListMapTest.SMALL_SIZE, ListMapTest.LARGE_SIZE};
      boolean[] testModeArray = {false, true};
      for (var j = 0; j < 2; j++) {
         var testSize = testSizeArray[j];
         var testMode = testModeArray[j];

         this.loadMap(testSize);
         var actualSize = this.listMap.size();
         Assert.assertEquals(testSize, actualSize);

         var streamSize = testSize >>> 1;
         var streamKey = "key-".concat(Integer.toString(streamSize - 1));
         var resultList = this.listMap.backwardsStream(streamKey, testMode).collect(Collectors.toList());
         Assert.assertEquals(streamSize, resultList.size());

         for (var i = 0; i < streamSize; i++) {
            var mapElement = resultList.get(i);
            var mapElementKey = mapElement.getKey();
            var position = mapElement.getPosition();
            var element = mapElement.getValue();
            var key = element.getKey();
            var value = element.getValue();
            var numString = Integer.toString(streamSize - 1 - i);
            var testKey = "key-".concat(numString);
            var testValue = "value-".concat(numString);
            Assert.assertEquals(testKey, mapElementKey);
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            Assert.assertEquals(streamSize - 1 - i, position);
         }
      }
   }

   @Test
   public void forwardConcurentMod() {
      var testSize = ListMapTest.LARGE_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      try {
         //@formatter:off
         @SuppressWarnings("unused")
         var resultList =
            this.listMap
               .forwardStream( true )
               .peek
                  (
                     (element)->
                     {
                        if( element.getPosition() == 3 * 1024 ) {
                           this.listMap.put( "bomb", element.getValue() );
                        }
                     }
                  )
               .collect( Collectors.toList() );
          //@formatter:on
         Assert.assertTrue(false);
      } catch (Exception e) {
         Assert.assertTrue(true);
      }
   }

   @Test
   public void forwardStreamTest() {
      int[] testSizeArray = {ListMapTest.SMALL_SIZE, ListMapTest.LARGE_SIZE};
      boolean[] testModeArray = {false, true};
      for (var j = 0; j < 2; j++) {
         var testSize = testSizeArray[j];
         var testMode = testModeArray[j];

         this.loadMap(testSize);
         var actualSize = this.listMap.size();
         Assert.assertEquals(testSize, actualSize);

         var resultList = this.listMap.forwardStream(testMode).collect(Collectors.toList());
         Assert.assertEquals(testSize, resultList.size());

         for (var i = 0; i < testSize; i++) {
            var mapElement = resultList.get(i);
            var mapElementKey = mapElement.getKey();
            var position = mapElement.getPosition();
            var element = mapElement.getValue();
            var key = element.getKey();
            var value = element.getValue();
            var numString = Integer.toString(i);
            var testKey = "key-".concat(numString);
            var testValue = "value-".concat(numString);
            Assert.assertEquals(testKey, mapElementKey);
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            Assert.assertEquals(i, position);
         }
      }
   }

   @Test
   public void forwardStreamTestByIndex() {
      int[] testSizeArray = {ListMapTest.SMALL_SIZE, ListMapTest.LARGE_SIZE};
      boolean[] testModeArray = {false, true};
      for (var j = 0; j < 2; j++) {
         var testSize = testSizeArray[j];
         var testMode = testModeArray[j];

         this.loadMap(testSize);
         var actualSize = this.listMap.size();
         Assert.assertEquals(testSize, actualSize);

         var streamSize = testSize >>> 1;
         var resultList = this.listMap.forwardStream(streamSize, testMode).collect(Collectors.toList());
         Assert.assertEquals(streamSize, resultList.size());

         for (var i = 0; i < streamSize; i++) {
            var mapElement = resultList.get(i);
            var mapElementKey = mapElement.getKey();
            var position = mapElement.getPosition();
            var element = mapElement.getValue();
            var key = element.getKey();
            var value = element.getValue();
            var numString = Integer.toString(i + streamSize);
            var testKey = "key-".concat(numString);
            var testValue = "value-".concat(numString);
            Assert.assertEquals(testKey, mapElementKey);
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            Assert.assertEquals(i + streamSize, position);
         }
      }
   }

   @Test
   public void forwardStreamTestByKey() {
      int[] testSizeArray = {ListMapTest.SMALL_SIZE, ListMapTest.LARGE_SIZE};
      boolean[] testModeArray = {false, true};
      for (var j = 0; j < 2; j++) {
         var testSize = testSizeArray[j];
         var testMode = testModeArray[j];

         this.loadMap(testSize);
         var actualSize = this.listMap.size();
         Assert.assertEquals(testSize, actualSize);

         var streamSize = testSize >>> 1;
         var streamKey = "key-".concat(Integer.toString(streamSize));
         var resultList = this.listMap.forwardStream(streamKey, testMode).collect(Collectors.toList());
         Assert.assertEquals(streamSize, resultList.size());

         for (var i = 0; i < streamSize; i++) {
            var mapElement = resultList.get(i);
            var mapElementKey = mapElement.getKey();
            var position = mapElement.getPosition();
            var element = mapElement.getValue();
            var key = element.getKey();
            var value = element.getValue();
            var numString = Integer.toString(i + streamSize);
            var testKey = "key-".concat(numString);
            var testValue = "value-".concat(numString);
            Assert.assertEquals(testKey, mapElementKey);
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            Assert.assertEquals(i + streamSize, position);
         }
      }
   }

   @Test
   public void getByIndexTest() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);
      for (int i = 0; i < testSize; i++) {
         var iFinal = i;
         var iString = Integer.toString(i);
         var testKey = "key-".concat(iString);
         var testValue = "value-".concat(iString);

         var elementOptional = this.listMap.get(i);

         Assert.assertTrue(elementOptional.isPresent());

         elementOptional.ifPresent((element) -> {
            var key = element.getKey();
            var value = element.getValue();
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, currentPosition);
         });

         var elementCurrentOptional = this.listMap.getCurrent();

         Assert.assertTrue(elementCurrentOptional.isPresent());

         elementCurrentOptional.ifPresent((element) -> {
            var key = element.getKey();
            var value = element.getValue();
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, currentPosition);
         });

         var testKeyCurrentOptional = this.listMap.getCurrentKey();

         Assert.assertTrue(testKeyCurrentOptional.isPresent());

         testKeyCurrentOptional.ifPresent((key) -> {
            Assert.assertEquals(testKey, key);
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, currentPosition);
         });

         var elementPreviousOptional = this.listMap.getPrevious();

         if (i != 0) {
            Assert.assertTrue(elementPreviousOptional.isPresent());

            elementPreviousOptional.ifPresent((element) -> {
               var key = element.getKey();
               var value = element.getValue();
               var previousTestKey = "key-".concat(Integer.toString(iFinal - 1));
               var previousTestValue = "value-".concat(Integer.toString(iFinal - 1));
               Assert.assertEquals(previousTestKey, key);
               Assert.assertEquals(previousTestValue, value);
               var currentPosition = this.listMap.getCurrentPosition();
               Assert.assertEquals(iFinal - 1, currentPosition);

               var elementNextOptional = this.listMap.getNext();

               Assert.assertTrue(elementNextOptional.isPresent());

               elementNextOptional.ifPresent((nextElement) -> {
                  var nextKey = nextElement.getKey();
                  var nextValue = nextElement.getValue();
                  Assert.assertEquals(testKey, nextKey);
                  Assert.assertEquals(testValue, nextValue);
                  var secondCurrentPosition = this.listMap.getCurrentPosition();
                  Assert.assertEquals(iFinal, secondCurrentPosition);
               });

            });
         } else {
            Assert.assertTrue(elementPreviousOptional.isEmpty());

            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(-1, currentPosition);

            var recoverElement = this.listMap.get(iFinal);
            Assert.assertTrue(recoverElement.isPresent());
            var recoverPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, recoverPosition);
         }

         var elementNextOptional = this.listMap.getNext();

         if (i != (testSize - 1)) {
            Assert.assertTrue(elementNextOptional.isPresent());

            elementNextOptional.ifPresent((element) -> {
               var key = element.getKey();
               var value = element.getValue();
               var nextTestKey = "key-".concat(Integer.toString(iFinal + 1));
               var nextTestValue = "value-".concat(Integer.toString(iFinal + 1));
               Assert.assertEquals(nextTestKey, key);
               Assert.assertEquals(nextTestValue, value);
               var currentPosition = this.listMap.getCurrentPosition();
               Assert.assertEquals(currentPosition, iFinal + 1);

               var elementPreviousOptional2 = this.listMap.getPrevious();

               Assert.assertTrue(elementPreviousOptional2.isPresent());

               elementPreviousOptional2.ifPresent((previousElement) -> {
                  var previousKey = previousElement.getKey();
                  var previousValue = previousElement.getValue();
                  Assert.assertEquals(testKey, previousKey);
                  Assert.assertEquals(testValue, previousValue);
                  var secondCurrentPosition = this.listMap.getCurrentPosition();
                  Assert.assertEquals(iFinal, secondCurrentPosition);
               });
            });
         } else {
            Assert.assertTrue(elementNextOptional.isEmpty());
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(-1, currentPosition);
         }
      }
   }

   @Test
   public void getByKeyTest() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);
      for (int i = 0; i < testSize; i++) {
         var iFinal = i;
         var iString = Integer.toString(i);
         var testKey = "key-".concat(iString);
         var testValue = "value-".concat(iString);

         var elementOptional = this.listMap.get(testKey);

         Assert.assertTrue(elementOptional.isPresent());

         elementOptional.ifPresent((element) -> {
            var key = element.getKey();
            var value = element.getValue();
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, currentPosition);
         });

         var elementCurrentOptional = this.listMap.getCurrent();

         Assert.assertTrue(elementCurrentOptional.isPresent());

         elementCurrentOptional.ifPresent((element) -> {
            var key = element.getKey();
            var value = element.getValue();
            Assert.assertEquals(testKey, key);
            Assert.assertEquals(testValue, value);
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, currentPosition);
         });

         var testKeyCurrentOptional = this.listMap.getCurrentKey();

         Assert.assertTrue(testKeyCurrentOptional.isPresent());

         testKeyCurrentOptional.ifPresent((key) -> {
            Assert.assertEquals(testKey, key);
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, currentPosition);
         });

         var elementPreviousOptional = this.listMap.getPrevious();

         if (i != 0) {
            Assert.assertTrue(elementPreviousOptional.isPresent());

            elementPreviousOptional.ifPresent((element) -> {
               var key = element.getKey();
               var value = element.getValue();
               var previousTestKey = "key-".concat(Integer.toString(iFinal - 1));
               var previousTestValue = "value-".concat(Integer.toString(iFinal - 1));
               Assert.assertEquals(previousTestKey, key);
               Assert.assertEquals(previousTestValue, value);
               var currentPosition = this.listMap.getCurrentPosition();
               Assert.assertEquals(iFinal - 1, currentPosition);

               var elementNextOptional = this.listMap.getNext();

               Assert.assertTrue(elementNextOptional.isPresent());

               elementNextOptional.ifPresent((nextElement) -> {
                  var nextKey = nextElement.getKey();
                  var nextValue = nextElement.getValue();
                  Assert.assertEquals(testKey, nextKey);
                  Assert.assertEquals(testValue, nextValue);
                  var secondCurrentPosition = this.listMap.getCurrentPosition();
                  Assert.assertEquals(iFinal, secondCurrentPosition);
               });

            });
         } else {
            Assert.assertTrue(elementPreviousOptional.isEmpty());

            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(-1, currentPosition);

            var recoverElement = this.listMap.get(testKey);
            Assert.assertTrue(recoverElement.isPresent());
            var recoverPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(iFinal, recoverPosition);
         }

         var elementNextOptional = this.listMap.getNext();

         if (i != (testSize - 1)) {
            Assert.assertTrue(elementNextOptional.isPresent());

            elementNextOptional.ifPresent((element) -> {
               var key = element.getKey();
               var value = element.getValue();
               var nextTestKey = "key-".concat(Integer.toString(iFinal + 1));
               var nextTestValue = "value-".concat(Integer.toString(iFinal + 1));
               Assert.assertEquals(nextTestKey, key);
               Assert.assertEquals(nextTestValue, value);
               var currentPosition = this.listMap.getCurrentPosition();
               Assert.assertEquals(iFinal + 1, currentPosition);

               var elementPreviousOptional2 = this.listMap.getPrevious();

               Assert.assertTrue(elementPreviousOptional2.isPresent());

               elementPreviousOptional2.ifPresent((previousElement) -> {
                  var previousKey = previousElement.getKey();
                  var previousValue = previousElement.getValue();
                  Assert.assertEquals(testKey, previousKey);
                  Assert.assertEquals(testValue, previousValue);
                  var secondCurrentPosition = this.listMap.getCurrentPosition();
                  Assert.assertEquals(iFinal, secondCurrentPosition);
               });
            });
         } else {
            Assert.assertTrue(elementNextOptional.isEmpty());
            var currentPosition = this.listMap.getCurrentPosition();
            Assert.assertEquals(-1, currentPosition);
         }

      }
   }

   @Test
   public void getFirstTest() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);
      var count = 0;
      //@formatter:off
      for( var elementOptional = this.listMap.getFirst();
               elementOptional.isPresent();
               elementOptional = this.listMap.getNext()

         )
      //@formatter:on
      {
         var element = elementOptional.get();
         var key = element.getKey();
         var value = element.getValue();
         var numString = Integer.toString(count++);
         var testKey = "key-".concat(numString);
         var testValue = "value-".concat(numString);
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
      }
      Assert.assertEquals(testSize, count);
   }

   @Test
   public void getLastTest() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);
      var count = testSize - 1;
      //@formatter:off
      for( var elementOptional = this.listMap.getLast();
               elementOptional.isPresent();
               elementOptional = this.listMap.getPrevious()

         )
      //@formatter:on
      {
         var element = elementOptional.get();
         var key = element.getKey();
         var value = element.getValue();
         var numString = Integer.toString(count--);
         var testKey = "key-".concat(numString);
         var testValue = "value-".concat(numString);
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
      }
      Assert.assertEquals(-1, count);
   }

   @Test
   public void getNextByIndex() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      var elementOptional = this.listMap.getNext(testSize - 1);

      Assert.assertTrue(elementOptional.isEmpty());
      var currentPosition = this.listMap.getCurrentPosition();
      Assert.assertEquals(-1, currentPosition);

      elementOptional = this.listMap.getNext(testSize - 2);

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-".concat(Integer.toString(testSize - 1));
         var testValue = "value-".concat(Integer.toString(testSize - 1));
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(testSize - 1, currentPosition2);
      });

      elementOptional = this.listMap.getNext(0);

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-1";
         var testValue = "value-1";
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(1, currentPosition2);
      });

   }

   @Test
   public void getNextByKey() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      var elementOptional = this.listMap.getNext("key-".concat(Integer.toString(testSize - 1)));

      Assert.assertTrue(elementOptional.isEmpty());
      var currentPosition = this.listMap.getCurrentPosition();
      Assert.assertEquals(-1, currentPosition);

      elementOptional = this.listMap.getNext("key-".concat(Integer.toString(testSize - 2)));

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-".concat(Integer.toString(testSize - 1));
         var testValue = "value-".concat(Integer.toString(testSize - 1));
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(testSize - 1, currentPosition2);
      });

      elementOptional = this.listMap.getNext("key-0");

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-1";
         var testValue = "value-1";
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(1, currentPosition2);
      });

   }

   @Test
   public void getPreviousByIndex() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      var elementOptional = this.listMap.getPrevious(0);

      Assert.assertTrue(elementOptional.isEmpty());
      var currentPosition = this.listMap.getCurrentPosition();
      Assert.assertEquals(-1, currentPosition);

      elementOptional = this.listMap.getPrevious(1);

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-0";
         var testValue = "value-0";
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(0, currentPosition2);
      });

      elementOptional = this.listMap.getPrevious(testSize - 1);

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-".concat(Integer.toString(testSize - 2));
         var testValue = "value-".concat(Integer.toString(testSize - 2));
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(testSize - 2, currentPosition2);
      });

   }

   @Test
   public void getPreviousByKey() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      var elementOptional = this.listMap.getPrevious("key-0");

      Assert.assertTrue(elementOptional.isEmpty());
      var currentPosition = this.listMap.getCurrentPosition();
      Assert.assertEquals(-1, currentPosition);

      elementOptional = this.listMap.getPrevious("key-1");

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-0";
         var testValue = "value-0";
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(0, currentPosition2);
      });

      elementOptional = this.listMap.getPrevious("key-".concat(Integer.toString(testSize - 1)));

      Assert.assertTrue(elementOptional.isPresent());

      elementOptional.ifPresent((element) -> {
         var key = element.getKey();
         var value = element.getValue();
         var testKey = "key-".concat(Integer.toString(testSize - 2));
         var testValue = "value-".concat(Integer.toString(testSize - 2));
         Assert.assertEquals(testKey, key);
         Assert.assertEquals(testValue, value);
         var currentPosition2 = this.listMap.getCurrentPosition();
         Assert.assertEquals(testSize - 2, currentPosition2);
      });

   }

   private void loadMap(int size) {
      for (var i = 0; i < size; i++) {
         var indexString = Integer.toString(i);
         var key = "key-".concat(indexString);
         var value = "value-".concat(indexString);
         var element = new element(key, value);
         if (this.hasKeyExtractor) {
            this.listMap.put(element);
         } else {
            this.listMap.put(key, element);
         }
      }
   }

   @Test
   public void outOfBounds() {
      var testSize = ListMapTest.SMALL_SIZE;
      this.loadMap(testSize);
      var actualSize = this.listMap.size();
      Assert.assertEquals(testSize, actualSize);

      Assert.assertTrue(this.listMap.backwardsStream(-1, false).findAny().isEmpty());
      Assert.assertTrue(this.listMap.backwardsStream(testSize, false).findAny().isEmpty());
      Assert.assertTrue(this.listMap.backwardsStream("not-here", false).findAny().isEmpty());
      Assert.assertTrue(this.listMap.forwardStream(-1, false).findAny().isEmpty());
      Assert.assertTrue(this.listMap.forwardStream(testSize, false).findAny().isEmpty());
      Assert.assertTrue(this.listMap.forwardStream("not-here", false).findAny().isEmpty());
      Assert.assertTrue(this.listMap.get(-1).isEmpty());
      Assert.assertTrue(this.listMap.get(testSize).isEmpty());
      Assert.assertTrue(this.listMap.getCurrent().isEmpty());
      Assert.assertTrue(this.listMap.getCurrentKey().isEmpty());
      Assert.assertTrue(this.listMap.getNext().isEmpty());
      Assert.assertTrue(this.listMap.getPrevious().isEmpty());
      Assert.assertTrue(this.listMap.getBackwardsSpliterator(-1).isEmpty());
      Assert.assertTrue(this.listMap.getBackwardsSpliterator(testSize).isEmpty());
      Assert.assertTrue(this.listMap.getBackwardsSpliterator("not-here").isEmpty());
      Assert.assertTrue(this.listMap.getForwardSpliterator(-1).isEmpty());
      Assert.assertTrue(this.listMap.getForwardSpliterator(testSize).isEmpty());
      Assert.assertTrue(this.listMap.getForwardSpliterator("not-here").isEmpty());
   }

   @Before
   public void testSetup() {
      this.listMap = this.listMapSupplier.get();
      var element = new element("key-0", "value-0");
      try {
         this.listMap.put(element);
         this.hasKeyExtractor = true;
      } catch (Exception e) {
         this.hasKeyExtractor = false;
      }
   }

}

/* EOF */
