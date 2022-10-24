/*
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
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.synchronization.forest.morphology;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.Direction;
import org.eclipse.osee.define.operations.synchronization.LinkType;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierFactory;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link StoreRankN} class.
 *
 * @author Loren K. Ashley
 */

public class StoreTest {

   private class KeySetTester {

      int hash;
      int size;
      Object[] keys;

      public KeySetTester(Object... keys) {
         if ((keys == null) || (keys.length == 0)) {
            this.keys = null;
            this.size = 0;
            this.hash = -1;
         } else {
            this.keys = keys;
            this.size = keys.length;
            this.hash = 0;
            for (int i = 0; i < this.size; i++) {
               if (Objects.isNull(keys[i])) {
                  this.keys = null;
                  this.size = 0;
                  this.hash = -1;
                  return;
               }

               this.hash = this.hash ^ (9007 * keys[i].hashCode());
            }
         }
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object other) {
         //@formatter:off
         if(
                ( other == null                            )
             || ( !(other instanceof KeySetTester)         )
             || ( this.hash != ((KeySetTester) other).hash )
             || ( this.size != ((KeySetTester) other).size )
           )
         {
            return false;
         }

         for( int i = 0; i < this.size; i++ )
         {
            if( !this.keys[i].equals( ((KeySetTester) other).keys[i] ) )
            {
               return false;
            }
         }

         return true;
         //@formatter:on
      }

      @Override
      public String toString() {
         return (Objects.nonNull(this.keys)) ? Arrays.stream(this.keys).map((key) -> (String) key).collect(
            Collectors.joining(", ", "[ ", " ]")) : "(not initialized)";
      }
   }

   private class GroveThingTester implements GroveThing {

      private final int rank;
      private final int nativeRank;
      private final Identifier identifier;
      private Object foreignThing;
      private final Object nativeThing;
      private final Object[] primaryKeys;
      private final Object[] nativeKeys;
      private final String name;

      public GroveThingTester(Object[] primaryKeys, Object[] nativeKeys, String name) {
         this.primaryKeys = primaryKeys;
         this.rank = primaryKeys.length;
         this.nativeKeys = nativeKeys;
         this.nativeRank = nativeKeys.length;
         this.foreignThing = "Foreign Thing";
         this.nativeThing = "NativeThing";
         this.identifier =
            new IdentifierFactory(Direction.IMPORT.getIdentifierFactoryType()).createIdentifier(IdentifierType.HEADER);
         this.name = name;
      }

      @Override
      public Message toMessage(int indent, Message message) {
         return new Message().title("toMessage");
      }

      @Override
      public Object getForeignHierarchy() {
         return null;
      }

      @Override
      public Object getForeignThing() {
         return this.foreignThing;
      }

      @Override
      public Identifier getIdentifier() {
         return this.identifier;
      }

      @Override
      public Optional<Object[]> getPrimaryKeys() {
         return Optional.of(this.primaryKeys);
      }

      @Override
      public Optional<Object[]> getNativeKeys() {
         return Optional.of(this.nativeKeys);
      }

      @Override
      public Object getNativeThing() {
         return this.nativeThing;
      }

      @Override
      public int nativeRank() {
         return this.nativeRank;
      }

      @Override
      public int rank() {
         return this.rank;
      }

      @Override
      public void setForeignHierarchy(Object foreignHierarchy) {
         return;
      }

      @Override
      public GroveThing setForeignThing(Object foreignThing) {
         this.foreignThing = foreignThing;
         return this;
      }

      @Override
      public GroveThing setNativeThings(Object... nativeThings) {
         return this;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public Optional<GroveThing> getLinkScalar(LinkType linkType) {
         return null;
      }

      @Override
      public Optional<Collection<GroveThing>> getLinkVector(LinkType linkType) {
         return null;
      }

      @Override
      public Optional<GroveThing> getParent(int selector) {
         return null;
      }

      @Override
      public IdentifierType getType() {
         return null;
      }

      @Override
      public boolean hasNativeKeys() {
         return false;
      }

      @Override
      public boolean isInGroup(IdentifierTypeGroup identifierTypeGroup) {
         return false;
      }

      @Override
      public boolean isType(LinkType identifierType) {
         return false;
      }

      @Override
      public boolean mayProvideNativeKeys() {
         return false;
      }

      @Override
      public void setLinkScalar(LinkType linkType, GroveThing linkedGroveThing) {
         return;
      }

      @Override
      public void setLinkVectorElement(LinkType linkType, GroveThing linkedGroveThing) {
         return;
      }

      @Override
      public Stream<GroveThing> streamLinks(LinkType linkType) {
         return null;
      }

      @Override
      public Optional<GroveThing> getLinkVectorElement(LinkType linkType, int index) {
         return null;
      }

   }
   private static boolean stringKeyValidator(Object key) {
      //@formatter:off
      return
            Objects.nonNull( key )
         && ( key instanceof String )
         && !((String)key).isEmpty();
      //@formatter:on
   }

   //@formatter:off
   @SuppressWarnings({"unchecked"})
   private static Predicate<Object>[] keyValidators1 =
      new Predicate[]
      {
        StoreTest::stringKeyValidator
      };
   //@formatter:on

   //@formatter:off
   @SuppressWarnings({"unchecked"})
   private static Predicate<Object>[] keyValidators2 =
      new Predicate[]
      {
        StoreTest::stringKeyValidator,
        StoreTest::stringKeyValidator
      };
   //@formatter:on

   //@formatter:off
   @SuppressWarnings({"unchecked"})
   private static Predicate<Object>[] keyValidators3 =
      new Predicate[]
      {
        StoreTest::stringKeyValidator,
        StoreTest::stringKeyValidator,
        StoreTest::stringKeyValidator
      };
   //@formatter:on

   public StoreTest() {
   }

   @Test
   public void testContainsR1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      var gt1 = new GroveThingTester( new Object[] { "A" }, new Object[] { "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B" }, new Object[] { "Z" }, "gt2" );

      store.add( gt1 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "B" } ) );

      store.add( gt2 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B" } ) );

      //@formatter:on
   }

   @Test
   public void testContainsR2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A" }, new Object[] { "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A" }, new Object[] { "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B" }, new Object[] { "Z", "Z" }, "gt3" );

      store.add( gt1 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "B" } ) );

      store.add( gt2 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "B" } ) );

      store.add( gt3 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "A", "B" } ) );

      //@formatter:on
   }

   @Test
   public void testContainsR3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt3" );
      var gt4 = new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt4" );

      store.add( gt1 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "B", "A", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "A", "B" } ) );

      store.add( gt2 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "A", "B" } ) );

      store.add( gt3 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "A", "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "A", "B", "A" } ) );
      Assert.assertFalse ( store.contains( new Object[] { "A", "A", "B" } ) );

      store.add( gt4 );

      Assert.assertTrue  ( store.contains( new Object[] { "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "A", "B" } ) );

      Assert.assertTrue  ( store.contains( new Object[] { "A", "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "B", "A", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "A", "B", "A" } ) );
      Assert.assertTrue  ( store.contains( new Object[] { "A", "A", "B" } ) );

      //@formatter:on
   }

   @Test
   public void testGetR1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      var gt1 = new GroveThingTester( new Object[] { "A" }, new Object[] { "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B" }, new Object[] { "Z" }, "gt2" );

      store.add( gt1 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      store.add( gt2 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt2",  store.get( new Object[] { "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      //@formatter:on
   }

   @Test
   public void testGetR2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A" }, new Object[] { "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A" }, new Object[] { "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B" }, new Object[] { "Z", "Z" }, "gt3" );

      store.add( gt1 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      store.add( gt2 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt2",  store.get( new Object[] { "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      store.add( gt3 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt2",  store.get( new Object[] { "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt3",  store.get( new Object[] { "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      //@formatter:on
   }

   @Test
   public void testGetR3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt3" );
      var gt4 = new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt4" );

      store.add( gt1 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "B", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      store.add( gt2 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt2",  store.get( new Object[] { "B", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      store.add( gt3 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt2",  store.get( new Object[] { "B", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt3",  store.get( new Object[] { "A", "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "FAIL", store.get( new Object[] { "A", "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      store.add( gt4 );

      Assert.assertEquals( "gt1",  store.get( new Object[] { "A", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt2",  store.get( new Object[] { "B", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt3",  store.get( new Object[] { "A", "B", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );
      Assert.assertEquals( "gt4",  store.get( new Object[] { "A", "A", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).orElse( "FAIL" ) );

      //@formatter:on
   }

   @Test
   public void testGetType() {
      //@formatter:off
      var storePrimary = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );
      var storeNative  = new StoreRankN( StoreType.NATIVE,  3, StoreTest.keyValidators3 );

      Assert.assertEquals( StoreType.PRIMARY, storePrimary.getType() );
      Assert.assertEquals( StoreType.NATIVE,  storeNative.getType()  );
      //@formatter:on
   }

   @Test
   public void testRank() {
      //@formatter:off
      var storeRank1 = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );
      var storeRank2 = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );
      var storeRank3 = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      Assert.assertEquals( 1, storeRank1.rank() );
      Assert.assertEquals( 2, storeRank2.rank() );
      Assert.assertEquals( 3, storeRank3.rank() );
      //@formatter:on
   }

   @Test
   public void testSize() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt3" );
      var gt4 = new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt4" );

      Assert.assertEquals( 0,  store.size() );

      store.add( gt1 );

      Assert.assertEquals( 1,  store.size() );

      store.add( gt2 );

      Assert.assertEquals( 2,  store.size() );

      store.add( gt3 );

      Assert.assertEquals( 3,  store.size() );

      store.add( gt4 );

      Assert.assertEquals( 4,  store.size() );

      //@formatter:on
   }

   @Test
   public void testStreamDeepR1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      var gt1 = new GroveThingTester( new Object[] { "A" }, new Object[] { "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B" }, new Object[] { "Z" }, "gt2" );

      store.add( gt1 );

      var set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertFalse ( set.contains( "gt2" ) );

      store.add( gt2 );

      set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertTrue  ( set.contains( "gt2" ) );

      //@formatter:on
   }

   @Test
   public void testStreamDeepR2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A" }, new Object[] { "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A" }, new Object[] { "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B" }, new Object[] { "Z", "Z" }, "gt3" );

      store.add( gt1 );

      var set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertFalse ( set.contains( "gt2" ) );
      Assert.assertFalse ( set.contains( "gt3" ) );

      store.add( gt2 );

      set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertTrue  ( set.contains( "gt2" ) );
      Assert.assertFalse ( set.contains( "gt3" ) );

      store.add( gt3 );

      set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertTrue  ( set.contains( "gt2" ) );
      Assert.assertTrue  ( set.contains( "gt3" ) );

      //@formatter:on
   }

   @Test
   public void testStreamDeepR3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt3" );
      var gt4 = new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt4" );

      store.add( gt1 );

      var set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertFalse ( set.contains( "gt2" ) );
      Assert.assertFalse ( set.contains( "gt3" ) );
      Assert.assertFalse ( set.contains( "gt4" ) );

      store.add( gt2 );

      set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertTrue  ( set.contains( "gt2" ) );
      Assert.assertFalse ( set.contains( "gt3" ) );
      Assert.assertFalse ( set.contains( "gt4" ) );

      store.add( gt3 );

      set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertTrue  ( set.contains( "gt2" ) );
      Assert.assertTrue  ( set.contains( "gt3" ) );
      Assert.assertFalse ( set.contains( "gt4" ) );

      store.add( gt4 );

      set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt1" ) );
      Assert.assertTrue  ( set.contains( "gt2" ) );
      Assert.assertTrue  ( set.contains( "gt3" ) );
      Assert.assertTrue  ( set.contains( "gt4" ) );

      //@formatter:on
   }

   @Test
   public void testStreamR1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      store.add( new GroveThingTester( new Object[] { "A" }, new Object[] { "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "B" }, new Object[] { "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "C" }, new Object[] { "Z" }, "gt03" ) );

      var set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );

      set = store.stream( new Object[] { "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );

      set = store.stream( new Object[] { "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );

      set = store.stream( new Object[] { "C" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );

      //@formatter:on
   }

   @Test
   public void testStreamR2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      store.add( new GroveThingTester( new Object[] { "A", "A" }, new Object[] { "Z", "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "A", "B" }, new Object[] { "Z", "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "A", "C" }, new Object[] { "Z", "Z" }, "gt03" ) );
      store.add( new GroveThingTester( new Object[] { "B", "A" }, new Object[] { "Z", "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "B", "B" }, new Object[] { "Z", "Z" }, "gt05" ) );
      store.add( new GroveThingTester( new Object[] { "B", "C" }, new Object[] { "Z", "Z" }, "gt06" ) );
      store.add( new GroveThingTester( new Object[] { "C", "A" }, new Object[] { "Z", "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "C", "B" }, new Object[] { "Z", "Z" }, "gt08" ) );
      store.add( new GroveThingTester( new Object[] { "C", "C" }, new Object[] { "Z", "Z" }, "gt09" ) );

      var set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );
      Assert.assertTrue  ( set.contains( "gt04" ) );
      Assert.assertTrue  ( set.contains( "gt05" ) );
      Assert.assertTrue  ( set.contains( "gt06" ) );
      Assert.assertTrue  ( set.contains( "gt07" ) );
      Assert.assertTrue  ( set.contains( "gt08" ) );
      Assert.assertTrue  ( set.contains( "gt09" ) );

      set = store.stream( new Object[] { "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );

      set = store.stream( new Object[] { "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertTrue  ( set.contains( "gt04" ) );
      Assert.assertTrue  ( set.contains( "gt05" ) );
      Assert.assertTrue  ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );

      set = store.stream( new Object[] { "C" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertTrue  ( set.contains( "gt07" ) );
      Assert.assertTrue  ( set.contains( "gt08" ) );
      Assert.assertTrue  ( set.contains( "gt09" ) );

      set = store.stream( new Object[] { "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );

      set = store.stream( new Object[] { "B", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertTrue  ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );

      set = store.stream( new Object[] { "C", "C" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertTrue  ( set.contains( "gt09" ) );


      //@formatter:on
   }

   @Test
   public void testStreamR3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      store.add( new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "A", "A", "C" }, new Object[] { "Z", "Z", "Z" }, "gt03" ) );
      store.add( new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "B", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt05" ) );
      store.add( new GroveThingTester( new Object[] { "B", "A", "C" }, new Object[] { "Z", "Z", "Z" }, "gt06" ) );
      store.add( new GroveThingTester( new Object[] { "C", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "C", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt08" ) );
      store.add( new GroveThingTester( new Object[] { "C", "A", "C" }, new Object[] { "Z", "Z", "Z" }, "gt09" ) );
      store.add( new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt11" ) );
      store.add( new GroveThingTester( new Object[] { "A", "B", "B" }, new Object[] { "Z", "Z", "Z" }, "gt12" ) );
      store.add( new GroveThingTester( new Object[] { "A", "B", "C" }, new Object[] { "Z", "Z", "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "B", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt14" ) );
      store.add( new GroveThingTester( new Object[] { "B", "B", "B" }, new Object[] { "Z", "Z", "Z" }, "gt15" ) );
      store.add( new GroveThingTester( new Object[] { "B", "B", "C" }, new Object[] { "Z", "Z", "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "C", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt17" ) );
      store.add( new GroveThingTester( new Object[] { "C", "B", "B" }, new Object[] { "Z", "Z", "Z" }, "gt18" ) );
      store.add( new GroveThingTester( new Object[] { "C", "B", "C" }, new Object[] { "Z", "Z", "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "A", "C", "A" }, new Object[] { "Z", "Z", "Z" }, "gt21" ) );
      store.add( new GroveThingTester( new Object[] { "A", "C", "B" }, new Object[] { "Z", "Z", "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "A", "C", "C" }, new Object[] { "Z", "Z", "Z" }, "gt23" ) );
      store.add( new GroveThingTester( new Object[] { "B", "C", "A" }, new Object[] { "Z", "Z", "Z" }, "gt24" ) );
      store.add( new GroveThingTester( new Object[] { "B", "C", "B" }, new Object[] { "Z", "Z", "Z" }, "gt25" ) );
      store.add( new GroveThingTester( new Object[] { "B", "C", "C" }, new Object[] { "Z", "Z", "Z" }, "gt26" ) );
      store.add( new GroveThingTester( new Object[] { "C", "C", "A" }, new Object[] { "Z", "Z", "Z" }, "gt27" ) );
      store.add( new GroveThingTester( new Object[] { "C", "C", "B" }, new Object[] { "Z", "Z", "Z" }, "gt28" ) );
      store.add( new GroveThingTester( new Object[] { "C", "C", "C" }, new Object[] { "Z", "Z", "Z" }, "gt29" ) );

      var set = store.stream().map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );
      Assert.assertTrue  ( set.contains( "gt04" ) );
      Assert.assertTrue  ( set.contains( "gt05" ) );
      Assert.assertTrue  ( set.contains( "gt06" ) );
      Assert.assertTrue  ( set.contains( "gt07" ) );
      Assert.assertTrue  ( set.contains( "gt08" ) );
      Assert.assertTrue  ( set.contains( "gt09" ) );
      Assert.assertTrue  ( set.contains( "gt11" ) );
      Assert.assertTrue  ( set.contains( "gt12" ) );
      Assert.assertTrue  ( set.contains( "gt13" ) );
      Assert.assertTrue  ( set.contains( "gt14" ) );
      Assert.assertTrue  ( set.contains( "gt15" ) );
      Assert.assertTrue  ( set.contains( "gt16" ) );
      Assert.assertTrue  ( set.contains( "gt17" ) );
      Assert.assertTrue  ( set.contains( "gt18" ) );
      Assert.assertTrue  ( set.contains( "gt19" ) );
      Assert.assertTrue  ( set.contains( "gt21" ) );
      Assert.assertTrue  ( set.contains( "gt22" ) );
      Assert.assertTrue  ( set.contains( "gt23" ) );
      Assert.assertTrue  ( set.contains( "gt24" ) );
      Assert.assertTrue  ( set.contains( "gt25" ) );
      Assert.assertTrue  ( set.contains( "gt26" ) );
      Assert.assertTrue  ( set.contains( "gt27" ) );
      Assert.assertTrue  ( set.contains( "gt28" ) );
      Assert.assertTrue  ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertTrue  ( set.contains( "gt11" ) );
      Assert.assertTrue  ( set.contains( "gt12" ) );
      Assert.assertTrue  ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertFalse ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertTrue  ( set.contains( "gt21" ) );
      Assert.assertTrue  ( set.contains( "gt22" ) );
      Assert.assertTrue  ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertFalse ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertTrue  ( set.contains( "gt04" ) );
      Assert.assertTrue  ( set.contains( "gt05" ) );
      Assert.assertTrue  ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertTrue  ( set.contains( "gt14" ) );
      Assert.assertTrue  ( set.contains( "gt15" ) );
      Assert.assertTrue  ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertTrue  ( set.contains( "gt24" ) );
      Assert.assertTrue  ( set.contains( "gt25" ) );
      Assert.assertTrue  ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertFalse ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "C" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertTrue  ( set.contains( "gt07" ) );
      Assert.assertTrue  ( set.contains( "gt08" ) );
      Assert.assertTrue  ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertFalse ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertTrue  ( set.contains( "gt17" ) );
      Assert.assertTrue  ( set.contains( "gt18" ) );
      Assert.assertTrue  ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertTrue  ( set.contains( "gt27" ) );
      Assert.assertTrue  ( set.contains( "gt28" ) );
      Assert.assertTrue  ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertTrue  ( set.contains( "gt02" ) );
      Assert.assertTrue  ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertFalse ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertFalse ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "B", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertTrue  ( set.contains( "gt14" ) );
      Assert.assertTrue  ( set.contains( "gt15" ) );
      Assert.assertTrue  ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertFalse ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "C", "C" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertFalse ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertTrue  ( set.contains( "gt27" ) );
      Assert.assertTrue  ( set.contains( "gt28" ) );
      Assert.assertTrue  ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "A", "A", "A" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertFalse ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertFalse ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "B", "B", "B" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertTrue  ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertFalse ( set.contains( "gt29" ) );

      set = store.stream( new Object[] { "C", "C", "C" } ).map( gt -> ((GroveThingTester) gt).getName() ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( "gt01" ) );
      Assert.assertFalse ( set.contains( "gt02" ) );
      Assert.assertFalse ( set.contains( "gt03" ) );
      Assert.assertFalse ( set.contains( "gt04" ) );
      Assert.assertFalse ( set.contains( "gt05" ) );
      Assert.assertFalse ( set.contains( "gt06" ) );
      Assert.assertFalse ( set.contains( "gt07" ) );
      Assert.assertFalse ( set.contains( "gt08" ) );
      Assert.assertFalse ( set.contains( "gt09" ) );
      Assert.assertFalse ( set.contains( "gt11" ) );
      Assert.assertFalse ( set.contains( "gt12" ) );
      Assert.assertFalse ( set.contains( "gt13" ) );
      Assert.assertFalse ( set.contains( "gt14" ) );
      Assert.assertFalse ( set.contains( "gt15" ) );
      Assert.assertFalse ( set.contains( "gt16" ) );
      Assert.assertFalse ( set.contains( "gt17" ) );
      Assert.assertFalse ( set.contains( "gt18" ) );
      Assert.assertFalse ( set.contains( "gt19" ) );
      Assert.assertFalse ( set.contains( "gt21" ) );
      Assert.assertFalse ( set.contains( "gt22" ) );
      Assert.assertFalse ( set.contains( "gt23" ) );
      Assert.assertFalse ( set.contains( "gt24" ) );
      Assert.assertFalse ( set.contains( "gt25" ) );
      Assert.assertFalse ( set.contains( "gt26" ) );
      Assert.assertFalse ( set.contains( "gt27" ) );
      Assert.assertFalse ( set.contains( "gt28" ) );
      Assert.assertTrue  ( set.contains( "gt29" ) );

      //@formatter:on
   }

   @Test
   public void teststreamKeysAtAndBelowR1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      store.add( new GroveThingTester( new Object[] { "A0" }, new Object[] { "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "B0" }, new Object[] { "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "C0" }, new Object[] { "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "D0" }, new Object[] { "Z" }, "gt10" ) );
      store.add( new GroveThingTester( new Object[] { "E0" }, new Object[] { "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "F0" }, new Object[] { "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "G0" }, new Object[] { "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "H0" }, new Object[] { "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "I0" }, new Object[] { "Z" }, "gt25" ) );

      var set = store.streamKeysAtAndBelow().collect( Collectors.toSet() );

      Assert.assertEquals( 9, set.size() );

      Assert.assertTrue( set.contains( "A0" ) );
      Assert.assertTrue( set.contains( "B0" ) );
      Assert.assertTrue( set.contains( "C0" ) );
      Assert.assertTrue( set.contains( "D0" ) );
      Assert.assertTrue( set.contains( "E0" ) );
      Assert.assertTrue( set.contains( "F0" ) );
      Assert.assertTrue( set.contains( "G0" ) );
      Assert.assertTrue( set.contains( "H0" ) );
      Assert.assertTrue( set.contains( "I0" ) );

   }

   @Test
   public void teststreamKeysAtAndBelowR2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      store.add( new GroveThingTester( new Object[] { "A0", "A1" }, new Object[] { "Z", "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "B1" }, new Object[] { "Z", "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "C1" }, new Object[] { "Z", "Z" }, "gt03" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "D1" }, new Object[] { "Z", "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "E1" }, new Object[] { "Z", "Z" }, "gt05" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "F1" }, new Object[] { "Z", "Z" }, "gt06" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "G1" }, new Object[] { "Z", "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "H1" }, new Object[] { "Z", "Z" }, "gt08" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "I1" }, new Object[] { "Z", "Z" }, "gt09" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "J1" }, new Object[] { "Z", "Z" }, "gt10" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "K1" }, new Object[] { "Z", "Z" }, "gt11" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "L1" }, new Object[] { "Z", "Z" }, "gt12" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "M1" }, new Object[] { "Z", "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "N1" }, new Object[] { "Z", "Z" }, "gt14" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "O1" }, new Object[] { "Z", "Z" }, "gt15" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "P1" }, new Object[] { "Z", "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "Q1" }, new Object[] { "Z", "Z" }, "gt17" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "R1" }, new Object[] { "Z", "Z" }, "gt18" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "S1" }, new Object[] { "Z", "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "T1" }, new Object[] { "Z", "Z" }, "gt20" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "U1" }, new Object[] { "Z", "Z" }, "gt21" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "V1" }, new Object[] { "Z", "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "W1" }, new Object[] { "Z", "Z" }, "gt23" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "X1" }, new Object[] { "Z", "Z" }, "gt24" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Y1" }, new Object[] { "Z", "Z" }, "gt25" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Z1" }, new Object[] { "Z", "Z" }, "gt26" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "a1" }, new Object[] { "Z", "Z" }, "gt27" ) );

      var set = store.streamKeysAtAndBelow().collect( Collectors.toSet() );

      Assert.assertEquals( 36, set.size() );

      Assert.assertTrue( set.contains( "A0" ) );
      Assert.assertTrue( set.contains( "B0" ) );
      Assert.assertTrue( set.contains( "C0" ) );
      Assert.assertTrue( set.contains( "D0" ) );
      Assert.assertTrue( set.contains( "E0" ) );
      Assert.assertTrue( set.contains( "F0" ) );
      Assert.assertTrue( set.contains( "G0" ) );
      Assert.assertTrue( set.contains( "H0" ) );
      Assert.assertTrue( set.contains( "I0" ) );

      Assert.assertTrue( set.contains( "A1" ) );
      Assert.assertTrue( set.contains( "B1" ) );
      Assert.assertTrue( set.contains( "C1" ) );
      Assert.assertTrue( set.contains( "D1" ) );
      Assert.assertTrue( set.contains( "E1" ) );
      Assert.assertTrue( set.contains( "F1" ) );
      Assert.assertTrue( set.contains( "G1" ) );
      Assert.assertTrue( set.contains( "H1" ) );
      Assert.assertTrue( set.contains( "I1" ) );
      Assert.assertTrue( set.contains( "J1" ) );
      Assert.assertTrue( set.contains( "K1" ) );
      Assert.assertTrue( set.contains( "L1" ) );
      Assert.assertTrue( set.contains( "M1" ) );
      Assert.assertTrue( set.contains( "N1" ) );
      Assert.assertTrue( set.contains( "O1" ) );
      Assert.assertTrue( set.contains( "P1" ) );
      Assert.assertTrue( set.contains( "Q1" ) );
      Assert.assertTrue( set.contains( "R1" ) );
      Assert.assertTrue( set.contains( "S1" ) );
      Assert.assertTrue( set.contains( "T1" ) );
      Assert.assertTrue( set.contains( "U1" ) );
      Assert.assertTrue( set.contains( "V1" ) );
      Assert.assertTrue( set.contains( "W1" ) );
      Assert.assertTrue( set.contains( "X1" ) );
      Assert.assertTrue( set.contains( "Y1" ) );
      Assert.assertTrue( set.contains( "Z1" ) );
      Assert.assertTrue( set.contains( "a1" ) );

      set = store.streamKeysAtAndBelow( new Object[] { "A0" } ).collect( Collectors.toSet() );

      Assert.assertEquals( 3, set.size() );

      Assert.assertTrue( set.contains( "A1" ) );
      Assert.assertTrue( set.contains( "B1" ) );
      Assert.assertTrue( set.contains( "C1" ) );

   }

   @Test
   public void teststreamKeysAtAndBelowR3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      store.add( new GroveThingTester( new Object[] { "A0", "A1", "A2" }, new Object[] { "Z", "Z", "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "B1", "B2" }, new Object[] { "Z", "Z", "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "C1", "C2" }, new Object[] { "Z", "Z", "Z" }, "gt03" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "D1", "D2" }, new Object[] { "Z", "Z", "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "E1", "E2" }, new Object[] { "Z", "Z", "Z" }, "gt05" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "F1", "F2" }, new Object[] { "Z", "Z", "Z" }, "gt06" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "G1", "G2" }, new Object[] { "Z", "Z", "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "H1", "H2" }, new Object[] { "Z", "Z", "Z" }, "gt08" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "I1", "I2" }, new Object[] { "Z", "Z", "Z" }, "gt09" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "J1", "J2" }, new Object[] { "Z", "Z", "Z" }, "gt10" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "K1", "K2" }, new Object[] { "Z", "Z", "Z" }, "gt11" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "L1", "L2" }, new Object[] { "Z", "Z", "Z" }, "gt12" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "M1", "M2" }, new Object[] { "Z", "Z", "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "N1", "N2" }, new Object[] { "Z", "Z", "Z" }, "gt14" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "O1", "O2" }, new Object[] { "Z", "Z", "Z" }, "gt15" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "P1", "P2" }, new Object[] { "Z", "Z", "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "Q1", "Q2" }, new Object[] { "Z", "Z", "Z" }, "gt17" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "R1", "R2" }, new Object[] { "Z", "Z", "Z" }, "gt18" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "S1", "S2" }, new Object[] { "Z", "Z", "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "T1", "T2" }, new Object[] { "Z", "Z", "Z" }, "gt20" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "U1", "U2" }, new Object[] { "Z", "Z", "Z" }, "gt21" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "V1", "V2" }, new Object[] { "Z", "Z", "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "W1", "W2" }, new Object[] { "Z", "Z", "Z" }, "gt23" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "X1", "X2" }, new Object[] { "Z", "Z", "Z" }, "gt24" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Y1", "Y2" }, new Object[] { "Z", "Z", "Z" }, "gt25" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Z1", "Z2" }, new Object[] { "Z", "Z", "Z" }, "gt26" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "a1", "a2" }, new Object[] { "Z", "Z", "Z" }, "gt27" ) );

      var set = store.streamKeysAtAndBelow().collect( Collectors.toSet() );

      Assert.assertEquals( 63, set.size() );

      Assert.assertTrue( set.contains( "A0" ) );
      Assert.assertTrue( set.contains( "B0" ) );
      Assert.assertTrue( set.contains( "C0" ) );
      Assert.assertTrue( set.contains( "D0" ) );
      Assert.assertTrue( set.contains( "E0" ) );
      Assert.assertTrue( set.contains( "F0" ) );
      Assert.assertTrue( set.contains( "G0" ) );
      Assert.assertTrue( set.contains( "H0" ) );
      Assert.assertTrue( set.contains( "I0" ) );

      Assert.assertTrue( set.contains( "A1" ) );
      Assert.assertTrue( set.contains( "B1" ) );
      Assert.assertTrue( set.contains( "C1" ) );
      Assert.assertTrue( set.contains( "D1" ) );
      Assert.assertTrue( set.contains( "E1" ) );
      Assert.assertTrue( set.contains( "F1" ) );
      Assert.assertTrue( set.contains( "G1" ) );
      Assert.assertTrue( set.contains( "H1" ) );
      Assert.assertTrue( set.contains( "I1" ) );
      Assert.assertTrue( set.contains( "J1" ) );
      Assert.assertTrue( set.contains( "K1" ) );
      Assert.assertTrue( set.contains( "L1" ) );
      Assert.assertTrue( set.contains( "M1" ) );
      Assert.assertTrue( set.contains( "N1" ) );
      Assert.assertTrue( set.contains( "O1" ) );
      Assert.assertTrue( set.contains( "P1" ) );
      Assert.assertTrue( set.contains( "Q1" ) );
      Assert.assertTrue( set.contains( "R1" ) );
      Assert.assertTrue( set.contains( "S1" ) );
      Assert.assertTrue( set.contains( "T1" ) );
      Assert.assertTrue( set.contains( "U1" ) );
      Assert.assertTrue( set.contains( "V1" ) );
      Assert.assertTrue( set.contains( "W1" ) );
      Assert.assertTrue( set.contains( "X1" ) );
      Assert.assertTrue( set.contains( "Y1" ) );
      Assert.assertTrue( set.contains( "Z1" ) );
      Assert.assertTrue( set.contains( "a1" ) );

      Assert.assertTrue( set.contains( "A2" ) );
      Assert.assertTrue( set.contains( "B2" ) );
      Assert.assertTrue( set.contains( "C2" ) );
      Assert.assertTrue( set.contains( "D2" ) );
      Assert.assertTrue( set.contains( "E2" ) );
      Assert.assertTrue( set.contains( "F2" ) );
      Assert.assertTrue( set.contains( "G2" ) );
      Assert.assertTrue( set.contains( "H2" ) );
      Assert.assertTrue( set.contains( "I2" ) );
      Assert.assertTrue( set.contains( "J2" ) );
      Assert.assertTrue( set.contains( "K2" ) );
      Assert.assertTrue( set.contains( "L2" ) );
      Assert.assertTrue( set.contains( "M2" ) );
      Assert.assertTrue( set.contains( "N2" ) );
      Assert.assertTrue( set.contains( "O2" ) );
      Assert.assertTrue( set.contains( "P2" ) );
      Assert.assertTrue( set.contains( "Q2" ) );
      Assert.assertTrue( set.contains( "R2" ) );
      Assert.assertTrue( set.contains( "S2" ) );
      Assert.assertTrue( set.contains( "T2" ) );
      Assert.assertTrue( set.contains( "U2" ) );
      Assert.assertTrue( set.contains( "V2" ) );
      Assert.assertTrue( set.contains( "W2" ) );
      Assert.assertTrue( set.contains( "X2" ) );
      Assert.assertTrue( set.contains( "Y2" ) );
      Assert.assertTrue( set.contains( "Z2" ) );
      Assert.assertTrue( set.contains( "a2" ) );

      set = store.streamKeysAtAndBelow( new Object[] { "A0" } ).collect( Collectors.toSet() );

      Assert.assertEquals( 6, set.size() );

      Assert.assertTrue( set.contains( "A1" ) );
      Assert.assertTrue( set.contains( "B1" ) );
      Assert.assertTrue( set.contains( "C1" ) );

      Assert.assertTrue( set.contains( "A2" ) );
      Assert.assertTrue( set.contains( "B2" ) );
      Assert.assertTrue( set.contains( "C2" ) );

      set = store.streamKeysAtAndBelow( new Object[] { "A0", "A1" } ).collect( Collectors.toSet() );

      Assert.assertEquals( 1, set.size() );

      Assert.assertTrue( set.contains( "A2" ) );

   }

   @Test
   public void teststreamKeysAtR1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      store.add( new GroveThingTester( new Object[] { "A0" }, new Object[] { "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "B0" }, new Object[] { "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "C0" }, new Object[] { "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "D0" }, new Object[] { "Z" }, "gt10" ) );
      store.add( new GroveThingTester( new Object[] { "E0" }, new Object[] { "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "F0" }, new Object[] { "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "G0" }, new Object[] { "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "H0" }, new Object[] { "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "I0" }, new Object[] { "Z" }, "gt25" ) );

      var set = store.streamKeysAt().collect( Collectors.toSet() );

      Assert.assertEquals( 9, set.size() );

      Assert.assertTrue( set.contains( "A0" ) );
      Assert.assertTrue( set.contains( "B0" ) );
      Assert.assertTrue( set.contains( "C0" ) );
      Assert.assertTrue( set.contains( "D0" ) );
      Assert.assertTrue( set.contains( "E0" ) );
      Assert.assertTrue( set.contains( "F0" ) );
      Assert.assertTrue( set.contains( "G0" ) );
      Assert.assertTrue( set.contains( "H0" ) );
      Assert.assertTrue( set.contains( "I0" ) );

   }

   @Test
   public void teststreamKeysAtR2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      store.add( new GroveThingTester( new Object[] { "A0", "A1" }, new Object[] { "Z", "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "B1" }, new Object[] { "Z", "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "C1" }, new Object[] { "Z", "Z" }, "gt03" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "D1" }, new Object[] { "Z", "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "E1" }, new Object[] { "Z", "Z" }, "gt05" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "F1" }, new Object[] { "Z", "Z" }, "gt06" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "G1" }, new Object[] { "Z", "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "H1" }, new Object[] { "Z", "Z" }, "gt08" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "I1" }, new Object[] { "Z", "Z" }, "gt09" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "J1" }, new Object[] { "Z", "Z" }, "gt10" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "K1" }, new Object[] { "Z", "Z" }, "gt11" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "L1" }, new Object[] { "Z", "Z" }, "gt12" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "M1" }, new Object[] { "Z", "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "N1" }, new Object[] { "Z", "Z" }, "gt14" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "O1" }, new Object[] { "Z", "Z" }, "gt15" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "P1" }, new Object[] { "Z", "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "Q1" }, new Object[] { "Z", "Z" }, "gt17" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "R1" }, new Object[] { "Z", "Z" }, "gt18" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "S1" }, new Object[] { "Z", "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "T1" }, new Object[] { "Z", "Z" }, "gt20" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "U1" }, new Object[] { "Z", "Z" }, "gt21" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "V1" }, new Object[] { "Z", "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "W1" }, new Object[] { "Z", "Z" }, "gt23" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "X1" }, new Object[] { "Z", "Z" }, "gt24" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Y1" }, new Object[] { "Z", "Z" }, "gt25" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Z1" }, new Object[] { "Z", "Z" }, "gt26" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "a1" }, new Object[] { "Z", "Z" }, "gt27" ) );

      var set = store.streamKeysAt().collect( Collectors.toSet() );

      Assert.assertEquals( 9, set.size() );

      Assert.assertTrue( set.contains( "A0" ) );
      Assert.assertTrue( set.contains( "B0" ) );
      Assert.assertTrue( set.contains( "C0" ) );
      Assert.assertTrue( set.contains( "D0" ) );
      Assert.assertTrue( set.contains( "E0" ) );
      Assert.assertTrue( set.contains( "F0" ) );
      Assert.assertTrue( set.contains( "G0" ) );
      Assert.assertTrue( set.contains( "H0" ) );
      Assert.assertTrue( set.contains( "I0" ) );

      set = store.streamKeysAt( new Object[] { "A0" } ).collect( Collectors.toSet() );

      Assert.assertEquals( 3, set.size() );

      Assert.assertTrue( set.contains( "A1" ) );
      Assert.assertTrue( set.contains( "B1" ) );
      Assert.assertTrue( set.contains( "C1" ) );

   }

   @Test
   public void teststreamKeysAtR3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      store.add( new GroveThingTester( new Object[] { "A0", "A1", "A2" }, new Object[] { "Z", "Z", "Z" }, "gt01" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "B1", "B2" }, new Object[] { "Z", "Z", "Z" }, "gt02" ) );
      store.add( new GroveThingTester( new Object[] { "A0", "C1", "C2" }, new Object[] { "Z", "Z", "Z" }, "gt03" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "D1", "D2" }, new Object[] { "Z", "Z", "Z" }, "gt04" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "E1", "E2" }, new Object[] { "Z", "Z", "Z" }, "gt05" ) );
      store.add( new GroveThingTester( new Object[] { "B0", "F1", "F2" }, new Object[] { "Z", "Z", "Z" }, "gt06" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "G1", "G2" }, new Object[] { "Z", "Z", "Z" }, "gt07" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "H1", "H2" }, new Object[] { "Z", "Z", "Z" }, "gt08" ) );
      store.add( new GroveThingTester( new Object[] { "C0", "I1", "I2" }, new Object[] { "Z", "Z", "Z" }, "gt09" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "J1", "J2" }, new Object[] { "Z", "Z", "Z" }, "gt10" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "K1", "K2" }, new Object[] { "Z", "Z", "Z" }, "gt11" ) );
      store.add( new GroveThingTester( new Object[] { "D0", "L1", "L2" }, new Object[] { "Z", "Z", "Z" }, "gt12" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "M1", "M2" }, new Object[] { "Z", "Z", "Z" }, "gt13" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "N1", "N2" }, new Object[] { "Z", "Z", "Z" }, "gt14" ) );
      store.add( new GroveThingTester( new Object[] { "E0", "O1", "O2" }, new Object[] { "Z", "Z", "Z" }, "gt15" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "P1", "P2" }, new Object[] { "Z", "Z", "Z" }, "gt16" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "Q1", "Q2" }, new Object[] { "Z", "Z", "Z" }, "gt17" ) );
      store.add( new GroveThingTester( new Object[] { "F0", "R1", "R2" }, new Object[] { "Z", "Z", "Z" }, "gt18" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "S1", "S2" }, new Object[] { "Z", "Z", "Z" }, "gt19" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "T1", "T2" }, new Object[] { "Z", "Z", "Z" }, "gt20" ) );
      store.add( new GroveThingTester( new Object[] { "G0", "U1", "U2" }, new Object[] { "Z", "Z", "Z" }, "gt21" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "V1", "V2" }, new Object[] { "Z", "Z", "Z" }, "gt22" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "W1", "W2" }, new Object[] { "Z", "Z", "Z" }, "gt23" ) );
      store.add( new GroveThingTester( new Object[] { "H0", "X1", "X2" }, new Object[] { "Z", "Z", "Z" }, "gt24" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Y1", "Y2" }, new Object[] { "Z", "Z", "Z" }, "gt25" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "Z1", "Z2" }, new Object[] { "Z", "Z", "Z" }, "gt26" ) );
      store.add( new GroveThingTester( new Object[] { "I0", "a1", "a2" }, new Object[] { "Z", "Z", "Z" }, "gt27" ) );

      var set = store.streamKeysAt().collect( Collectors.toSet() );

      Assert.assertEquals( 9, set.size() );

      Assert.assertTrue( set.contains( "A0" ) );
      Assert.assertTrue( set.contains( "B0" ) );
      Assert.assertTrue( set.contains( "C0" ) );
      Assert.assertTrue( set.contains( "D0" ) );
      Assert.assertTrue( set.contains( "E0" ) );
      Assert.assertTrue( set.contains( "F0" ) );
      Assert.assertTrue( set.contains( "G0" ) );
      Assert.assertTrue( set.contains( "H0" ) );
      Assert.assertTrue( set.contains( "I0" ) );

      set = store.streamKeysAt( new Object[] { "A0" } ).collect( Collectors.toSet() );

      Assert.assertEquals( 3, set.size() );

      Assert.assertTrue( set.contains( "A1" ) );
      Assert.assertTrue( set.contains( "B1" ) );
      Assert.assertTrue( set.contains( "C1" ) );

      set = store.streamKeysAt( new Object[] { "A0", "A1" } ).collect( Collectors.toSet() );

      Assert.assertEquals( 1, set.size() );

      Assert.assertTrue( set.contains( "A2" ) );

   }


   @Test
   public void testStreamKeySetsT1R1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      var gt1 = new GroveThingTester( new Object[] { "A" }, new Object[] { "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B" }, new Object[] { "Z" }, "gt2" );

      store.add( gt1 );

      var set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );

      store.add( gt2 );

      set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );

      //@formatter:on
   }

   @Test
   public void testStreamKeySetsT1R2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A" }, new Object[] { "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A" }, new Object[] { "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B" }, new Object[] { "Z", "Z" }, "gt3" );

      store.add( gt1 );

      var set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );

      store.add( gt2 );

      set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );

      store.add( gt3 );

      set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );

      //@formatter:on
   }

   @Test
   public void testStreamKeySetsT1R3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      var gt1 = new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt1" );
      var gt2 = new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt2" );
      var gt3 = new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt3" );
      var gt4 = new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt4" );

      store.add( gt1 );

      var set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt4.getPrimaryKeys().get() ) ) );

      store.add( gt2 );

      set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt4.getPrimaryKeys().get() ) ) );

      store.add( gt3 );

      set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt4.getPrimaryKeys().get() ) ) );

      store.add( gt4 );

      set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt1.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt2.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt3.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt4.getPrimaryKeys().get() ) ) );

      //@formatter:on
   }

   @Test
   public void testStreamKeySetsT2R1() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 1, StoreTest.keyValidators1 );

      GroveThingTester gt01, gt02, gt03;

      store.add( gt01 = new GroveThingTester( new Object[] { "A" }, new Object[] { "Z" }, "gt01" ) );
      store.add( gt02 = new GroveThingTester( new Object[] { "B" }, new Object[] { "Z" }, "gt02" ) );
      store.add( gt03 = new GroveThingTester( new Object[] { "C" }, new Object[] { "Z" }, "gt03" ) );

      var set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "A" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "B" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "C" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );

      //@formatter:on
   }

   @Test
   public void testStreamKeySetsT2R2() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 2, StoreTest.keyValidators2 );

      GroveThingTester gt01, gt02, gt03, gt04, gt05, gt06, gt07, gt08, gt09;

      store.add( gt01 = new GroveThingTester( new Object[] { "A", "A" }, new Object[] { "Z", "Z" }, "gt01" ) );
      store.add( gt02 = new GroveThingTester( new Object[] { "A", "B" }, new Object[] { "Z", "Z" }, "gt02" ) );
      store.add( gt03 = new GroveThingTester( new Object[] { "A", "C" }, new Object[] { "Z", "Z" }, "gt03" ) );
      store.add( gt04 = new GroveThingTester( new Object[] { "B", "A" }, new Object[] { "Z", "Z" }, "gt04" ) );
      store.add( gt05 = new GroveThingTester( new Object[] { "B", "B" }, new Object[] { "Z", "Z" }, "gt05" ) );
      store.add( gt06 = new GroveThingTester( new Object[] { "B", "C" }, new Object[] { "Z", "Z" }, "gt06" ) );
      store.add( gt07 = new GroveThingTester( new Object[] { "C", "A" }, new Object[] { "Z", "Z" }, "gt07" ) );
      store.add( gt08 = new GroveThingTester( new Object[] { "C", "B" }, new Object[] { "Z", "Z" }, "gt08" ) );
      store.add( gt09 = new GroveThingTester( new Object[] { "C", "C" }, new Object[] { "Z", "Z" }, "gt09" ) );

      var set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "A" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "B" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "C" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "A", "A" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "B", "B" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "C", "C" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt01.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt02.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt03.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt04.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt05.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt06.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt07.getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt08.getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt09.getPrimaryKeys().get() ) ) );


      //@formatter:on
   }

   @Test
   public void testStreamKeySetsT2R3() {
      //@formatter:off
      var store = new StoreRankN( StoreType.PRIMARY, 3, StoreTest.keyValidators3 );

      GroveThingTester[] gt = new GroveThingTester[30];

      store.add( gt[ 1] = new GroveThingTester( new Object[] { "A", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt01" ) );
      store.add( gt[ 2] = new GroveThingTester( new Object[] { "A", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt02" ) );
      store.add( gt[ 3] = new GroveThingTester( new Object[] { "A", "A", "C" }, new Object[] { "Z", "Z", "Z" }, "gt03" ) );
      store.add( gt[ 4] = new GroveThingTester( new Object[] { "B", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt04" ) );
      store.add( gt[ 5] = new GroveThingTester( new Object[] { "B", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt05" ) );
      store.add( gt[ 6] = new GroveThingTester( new Object[] { "B", "A", "C" }, new Object[] { "Z", "Z", "Z" }, "gt06" ) );
      store.add( gt[ 7] = new GroveThingTester( new Object[] { "C", "A", "A" }, new Object[] { "Z", "Z", "Z" }, "gt07" ) );
      store.add( gt[ 8] = new GroveThingTester( new Object[] { "C", "A", "B" }, new Object[] { "Z", "Z", "Z" }, "gt08" ) );
      store.add( gt[ 9] = new GroveThingTester( new Object[] { "C", "A", "C" }, new Object[] { "Z", "Z", "Z" }, "gt09" ) );
      store.add( gt[11] = new GroveThingTester( new Object[] { "A", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt11" ) );
      store.add( gt[12] = new GroveThingTester( new Object[] { "A", "B", "B" }, new Object[] { "Z", "Z", "Z" }, "gt12" ) );
      store.add( gt[13] = new GroveThingTester( new Object[] { "A", "B", "C" }, new Object[] { "Z", "Z", "Z" }, "gt13" ) );
      store.add( gt[14] = new GroveThingTester( new Object[] { "B", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt14" ) );
      store.add( gt[15] = new GroveThingTester( new Object[] { "B", "B", "B" }, new Object[] { "Z", "Z", "Z" }, "gt15" ) );
      store.add( gt[16] = new GroveThingTester( new Object[] { "B", "B", "C" }, new Object[] { "Z", "Z", "Z" }, "gt16" ) );
      store.add( gt[17] = new GroveThingTester( new Object[] { "C", "B", "A" }, new Object[] { "Z", "Z", "Z" }, "gt17" ) );
      store.add( gt[18] = new GroveThingTester( new Object[] { "C", "B", "B" }, new Object[] { "Z", "Z", "Z" }, "gt18" ) );
      store.add( gt[19] = new GroveThingTester( new Object[] { "C", "B", "C" }, new Object[] { "Z", "Z", "Z" }, "gt19" ) );
      store.add( gt[21] = new GroveThingTester( new Object[] { "A", "C", "A" }, new Object[] { "Z", "Z", "Z" }, "gt21" ) );
      store.add( gt[22] = new GroveThingTester( new Object[] { "A", "C", "B" }, new Object[] { "Z", "Z", "Z" }, "gt22" ) );
      store.add( gt[23] = new GroveThingTester( new Object[] { "A", "C", "C" }, new Object[] { "Z", "Z", "Z" }, "gt23" ) );
      store.add( gt[24] = new GroveThingTester( new Object[] { "B", "C", "A" }, new Object[] { "Z", "Z", "Z" }, "gt24" ) );
      store.add( gt[25] = new GroveThingTester( new Object[] { "B", "C", "B" }, new Object[] { "Z", "Z", "Z" }, "gt25" ) );
      store.add( gt[26] = new GroveThingTester( new Object[] { "B", "C", "C" }, new Object[] { "Z", "Z", "Z" }, "gt26" ) );
      store.add( gt[27] = new GroveThingTester( new Object[] { "C", "C", "A" }, new Object[] { "Z", "Z", "Z" }, "gt27" ) );
      store.add( gt[28] = new GroveThingTester( new Object[] { "C", "C", "B" }, new Object[] { "Z", "Z", "Z" }, "gt28" ) );
      store.add( gt[29] = new GroveThingTester( new Object[] { "C", "C", "C" }, new Object[] { "Z", "Z", "Z" }, "gt29" ) );

      var set = store.streamKeySets().map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "A" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "B" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "C" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "A", "A" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "B", "B" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "C", "C" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "A", "A", "A" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertTrue  ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "B", "B", "B" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      set = store.streamKeySets( new Object[] { "C", "C", "C" } ).map( KeySetTester::new ).collect( Collectors.toSet() );

      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 1].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 2].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 3].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 4].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 5].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 6].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 7].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 8].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[ 9].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[11].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[12].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[13].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[14].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[15].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[16].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[17].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[18].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[19].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[21].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[22].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[23].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[24].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[25].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[26].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[27].getPrimaryKeys().get() ) ) );
      Assert.assertFalse ( set.contains( new KeySetTester( gt[28].getPrimaryKeys().get() ) ) );
      Assert.assertTrue  ( set.contains( new KeySetTester( gt[29].getPrimaryKeys().get() ) ) );

      //@formatter:on
   }

}
/* EOF */
