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

package org.eclipse.osee.define.operations.synchronization.forest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.osee.define.operations.synchronization.Direction;
import org.eclipse.osee.define.operations.synchronization.ForeignThingFamily;
import org.eclipse.osee.define.operations.synchronization.LinkType;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeDataTypeKey;
import org.eclipse.osee.define.operations.synchronization.forest.denizens.NativeHeader;
import org.eclipse.osee.define.operations.synchronization.forest.morphology.AbstractGroveThing;
import org.eclipse.osee.define.operations.synchronization.forest.morphology.AbstractMapGrove;
import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierFactory;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierType;
import org.eclipse.osee.define.operations.synchronization.identifier.IdentifierTypeGroup;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ParameterArray;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Class encapsulates the data structures for the Synchronization Artifact DocumentImpl Object Model. There is a
 * {@link Grove} and {@link GroveThing} implementation for each member of the {@link IdentifierType} enumeration. The
 * table below shows the {@link Grove} and {@link GroveThing} properties for each {@link IdentifierType}.
 * <p>
 * <table border="1" style="border-collapse:collapse;">
 * <caption>{@link Grove} And {@link GroveThing} Properties</caption>
 * <tr>
 * <th>{@link IdentifierType}</th>
 * <th>Primary<br>
 * Rank</th>
 * <th colspan="2">Primary Key Types</th>
 * <th>Provides<br>
 * Native<br>
 * Keys</th>
 * <th>Native<br>
 * Rank</th>
 * <th>Native Thing Types</th>
 * <th>Native Key Types</th>
 * </tr>
 * <tr>
 * <td rowspan="3">{@link IdentifierType#ATTRIBUTE_DEFINITION}</td>
 * <td rowspan="3">2</td>
 * <td>{@link IdentifierType#SPECIFICATION_TYPE}<br>
 * {@link IdentifierType#SPEC_OBJECT_TYPE}<br>
 * {@link IdentifierType#SPEC_RELATION_TYPE}</td>
 * <td>High Rank Index(0)</td>
 * <td rowspan="3">false</td>
 * <td rowspan="3">3</td>
 * <td>{@link IdentifierType}</td>
 * <td rowspan="3">(none)</td>
 * </tr>
 * <tr>
 * <td rowspan="2">{@link IdentifierType#ATTRIBUTE_DEFINITION}</td>
 * <td rowspan="2">Low Rank Index(1)</td>
 * <td>{@link ArtifactTypeToken}</td>
 * </tr>
 * <tr>
 * <td>{@link AttributeTypeToken}</td>
 * </tr>
 * <tr>
 * <td rowspan="2">{@link IdentifierType#ATTRIBUTE_VALUE}</td>
 * <td rowspan="2">2</td>
 * <td>{@link IdentifierType#SPECIFICATION}<br>
 * {@link IdentifierType#SPECTER_SPEC_OBJECT}<br>
 * {@link IdentifierType#SPEC_OBJECT}<br>
 * {@link IdentifierType#SPEC_RELATION}</td>
 * <td>High Rank Index(0)</td>
 * <td rowspan="2">false</td>
 * <td rowspan="2">1</td>
 * <td rowspan="2">(any class)</td>
 * <td rowspan="2">(none)</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#ATTRIBUTE_VALUE}</td>
 * <td>Low Rank Index(1)</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#DATA_TYPE_DEFINITION}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#DATA_TYPE_DEFINITION}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link NativeDataTypeKey}</td>
 * <td>{@link NativeDataTypeKey}</td>
 * </tr>
 * <tr>
 * <td rowspan="2">{@link IdentifierType#ENUM_VALUE}</td>
 * <td rowspan="2">1</td>
 * <td rowspan="2" colspan="2">{@link IdentifierType#ENUM_VALUE}</td>
 * <td rowspan="2">true</td>
 * <td rowspan="2">2</td>
 * <td>{@link AttributeTypeToken}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link EnumToken}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#HEADER}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#HEADER}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link NativeHeader}</td>
 * <td>(none)</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPECIFICATION}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#SPECIFICATION}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link ArtifactReadable}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPECIFICATION_TYPE}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#SPECIFICATION_TYPE}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link ArtifactTypeToken}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPECTER_SPEC_OBJECT}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#SPECTER_SPEC_OBJECT}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link ArtifactReadable}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td rowspan="3">{@link IdentifierType#SPEC_OBJECT}</td>
 * <td rowspan="3">3</td>
 * <td>{@link IdentifierType#SPECIFICATION}</td>
 * <td>High Rank Index(0)</td>
 * <td rowspan="3">true</td>
 * <td rowspan="3">1</td>
 * <td rowspan="3">{@link ArtifactReadable}</td>
 * <td rowspan="3">{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPECIFICATION}<br>
 * {@link IdentifierType#SPEC_OBJECT}</td>
 * <td>Middle Rank Index(1)</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPECIFICATION}<br>
 * {@link IdentifierType#SPEC_OBJECT}</td>
 * <td>Low Rank Index(2)</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPEC_OBJECT_TYPE}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#SPEC_OBJECT_TYPE}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link ArtifactTypeToken}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td rowspan="3">{@link IdentifierType#SPEC_RELATION}</td>
 * <td rowspan="3">1</td>
 * <td rowspan="3" colspan="2">{@link IdentifierType#SPEC_RELATION}</td>
 * <td rowspan="3">true</td>
 * <td rowspan="3">3</td>
 * <td>{@link ArtifactReadable}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link ArtifactReadable}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link ArtifactReadable}</td>
 * <td>{@link Long}</td>
 * </tr>
 * <tr>
 * <td>{@link IdentifierType#SPEC_RELATION_TYPE}</td>
 * <td>1</td>
 * <td colspan="2">{@link IdentifierType#SPEC_RELATION_TYPE}</td>
 * <td>true</td>
 * <td>1</td>
 * <td>{@link ArtifactTypeToken}</td>
 * <td>{@link Long}</td>
 * </tr>
 * </table>
 * <p>
 *
 * @author Loren K. Ashley
 */

public class Forest implements ToMessage {

   /**
    * Assertion guard rail for the minimum rank of a grove's primary store.
    */

   private static int minPrimaryRank = 1;

   /**
    * Assertion guard rail for the maximum rank of a grove's primary store.
    */

   private static int maxPrimaryRank = 3;

   /**
    * Assertion guard rail for the minimum rank of a grove's native store.
    */

   private static int minNativeRank = 1;

   /**
    * Assertion guard rail for the maximum rank of a grove's native store.
    */

   private static int maxNativeRank = 3;

   /**
    * Class used to hold static initialization records for {@link Grove} and {@link GroveThing} implementations.
    */

   private static class GroveThingInitializationRecord {

      /**
       * The {@link IdentifierType} of the Synchronization Artifact thing represented by the {@link GroveThing}
       * implementation and stored in the associated {@link Grove} implementation.
       *
       * @implNote The <code>identifierType</code> is used in assertion error messages.
       */

      @SuppressWarnings("unused")
      IdentifierType identifierType;

      /**
       * {@link Map} of {@link AbstractGroveThing.LinkRank} indicators by {@link LinkType}. {@link GroveThing} instances
       * maybe linked to other {@link GroveThing} instances. The map saves the type of link that maybe established to
       * {@link GroveThing} instances of the specified type. Members of the enumerations {@link IdentifierType} and
       * {@link IdentifierTypeGroup} both implement the {@link LinkType} interface and may be used as map keys.
       */

      Map<LinkType, AbstractGroveThing.LinkRank> linkRank;

      /**
       * {@link Predicate} used to validate the {@link LinkType} specified in a method's link type parameter is an
       * allowed type for the {@link GroveThing} implementation.
       */

      Predicate<LinkType> linkTypeValidator;

      /**
       * For {@link Grove} implementations that provide an index of the stored {@link GroveThing} instances by their
       * native keys, an array of {@link Function} implementations that extract the native keys from the native things
       * for each native rank of the {@link Grove}.
       */

      Function<Object, Object>[] nativeKeyFunctions;

      /**
       * A {@link Predicate} used by {@link GroveThing} implementations to validate the class of native things when they
       * are set.
       */

      Predicate<Object[]> nativeThingsValidator;

      /**
       * For {@link Grove} implementations that provide an index of the stored {@link GroveThing} instances by their
       * native keys, the number of map levels used to index the {@link GroveThing} instances.
       */

      int nativeRank;

      /**
       * A {@link Predicate} used by {@link GroveThing} implementations to validate the {@link IdentifierType} of the
       * parent {@link GroveThing} instances provided to the constructor. For {@link GroveThing} implementations without
       * parents (primary rank == 1) the predicate will validate that no parents were passed to the {@link GroveThing}
       * constructor.
       */

      Predicate<GroveThing[]> parentsValidator;

      /**
       * The number of map levels used to index the {@link GroveThing} instances by their primary keys.
       */

      int primaryRank;

      /**
       * When <code>true</code>, the <code>nativeKeyFunctions<code> will be applied to the native things passed to a
       * {@link GroveThing#setNativeThings} method to extract the native keys and {@link Grove} implementations will be
       * created with a native index (store).
       */

      boolean providesNativeKeys;

      /**
       * An array of the allowed classes for native keys. The index of the array corresponds with the rank of the key.
       */

      Class<?>[] validNativeKeyTypes;

      /**
       * An array of the allowed classes for the native things. The index of the array corresponds with the rank of the
       * native thing.
       */

      Class<?>[] validNativeThings;

      /**
       * Two dimensional array that specifies the allowed {@link IdentifierType}s of the primary keys. The primary rank
       * of the array corresponds with the rank of the primary key. The secondary rank of the array contains the allowed
       * {@link IdentifierType}s of the key for that rank. The {@link validPrimaryKeyTypes} array may not be regular in
       * size.
       */

      IdentifierType[][] validPrimaryKeyTypes;

      /**
       * Creates a new container for {@link Grove} and {@link GroveThing} initialization parameters.
       *
       * @param identifierType the {@link IdentifierType} of the Synchronization Artifact thing initialization
       * parameters are for.
       * @param primaryRank the number of map levels used to index the {@link GroveThing} instances by primary keys.
       * @param nativeRank the number of map levels used to index {@link GroveThing} instances by native keys.
       * @param providesNativeKeys when <code>true</code>, the <code>nativeKeyFunctions<code> will be applied to the
       * native things passed to a {@link GroveThing#setNativeThings} method to extract the native keys and
       * {@link Grove} implementations will be created with a native index (store).
       * @param linkTypeValidator {@link Predicate} used to validate the {@link LinkType} specified in a method's link
       * type parameter is an allowed type for the {@link GroveThing} implementation.
       * @param linkRank {@link Map} of {@link AbstractGroveThing.LinkRank} indicators by {@link LinkType}.
       * {@link GroveThing} instances maybe linked to other {@link GroveThing} instances. The map saves the type of link
       * that maybe established to {@link GroveThing} instances of the specified type.
       * @param validPrimaryKeyTypes Two dimensional array that specifies the allowed {@link IdentifierType}s of the
       * primary keys. The primary rank of the array corresponds with the rank of the primary key. The secondary rank of
       * the array contains the allowed {@link IdentifierType}s of the key for that rank. The
       * {@link validPrimaryKeyTypes} array may not be regular in size.
       * @param validNativeThings An array of the allowed classes for the native things. The index of the array
       * corresponds with the rank of the native thing.
       * @param validNativeKeyTypes An array of the allowed classes for native keys. The index of the array corresponds
       * with the rank of the key.
       * @param nativeKeyFunctions For {@link Grove} implementations that provide an index of the stored
       * {@link GroveThing} instances by their native keys, an array of {@link Function} implementations that extract
       * the native keys from the native things for each native rank of the {@link Grove}.
       */

      //@formatter:off
      GroveThingInitializationRecord
         (
            IdentifierType                             identifierType,
            int                                        primaryRank,
            int                                        nativeRank,
            boolean                                    providesNativeKeys,
            Predicate<LinkType>                        linkTypeValidator,
            Map<LinkType, AbstractGroveThing.LinkRank> linkRank,
            IdentifierType[][]                         validPrimaryKeyTypes,
            Class<?>[]                                 validNativeThings,
            Class<?>[]                                 validNativeKeyTypes,
            Function<Object,Object>[]                  nativeKeyFunctions
         )
      {
         assert
              Objects.nonNull( identifierType )
            : "Bad GroveThingInitializationRecord, identifier type is null.";

         assert
               ( primaryRank >= Forest.minPrimaryRank )
            && ( primaryRank <= Forest.maxPrimaryRank )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " primaryRank is out of range.";

         assert
               ( nativeRank >= Forest.minNativeRank )
            && ( nativeRank <= Forest.maxNativeRank )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " nativeRank is out of range.";

         assert
                 Objects.nonNull( linkRank )
              && (
                      (    ( linkRank.size() >= 1 )
                        && Objects.nonNull( linkTypeValidator ) )
                   || (    ( linkRank.size() == 0 )
                        && Objects.isNull( linkTypeValidator) ) )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " linkTypeValidator must be specified when the linkRank map has entries.";

         assert
              Objects.nonNull( validPrimaryKeyTypes )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " validPrimayKeyTypes is null.";

         assert
              ( validPrimaryKeyTypes.length == primaryRank )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " validPrimayKeyTypes length does not equal primaryRank.";

         assert
              (    Objects.isNull( validNativeThings )
                || ( validNativeThings.length == nativeRank ) )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " validNativeThings are provided and length does not match nativeRank.";

         assert
               (    !providesNativeKeys
                 && Objects.isNull( validNativeKeyTypes) )
            || (    providesNativeKeys
                 && Objects.nonNull( validNativeKeyTypes ) )
            : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " when providesNativeKeys is true validNativeKeyTypes must be non-null and when providesNativeKeys is flase validNatieKeyTypes must be null.";

        assert
                Objects.isNull( validNativeKeyTypes )
             || ( validNativeKeyTypes.length == nativeRank )
           : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " validNativeKeyTypes are provided and length does not match nativeRank.";

        assert
              (    !providesNativeKeys
                && Objects.isNull( nativeKeyFunctions) )
           || (    providesNativeKeys
                && Objects.nonNull( nativeKeyFunctions ) )
           : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " when providesNativeKeys is true nativeKeyFunctions must be non-null and when providesNativeKeys is flase natieKeyFunctions must be null.";

        assert
                Objects.isNull( nativeKeyFunctions )
             || ( nativeKeyFunctions.length == nativeRank )
           : "Bad GroveThingInitializationRecord for " + identifierType.toString() + " nativeKeyFunctions are provided and length does not match nativeRank.";

         this.identifierType       = identifierType;
         this.primaryRank          = primaryRank;
         this.nativeRank           = nativeRank;
         this.providesNativeKeys   = providesNativeKeys;
         this.linkTypeValidator    = linkTypeValidator;
         this.linkRank             = linkRank;
         this.validPrimaryKeyTypes = validPrimaryKeyTypes;
         this.validNativeThings    = validNativeThings;
         this.validNativeKeyTypes  = validNativeKeyTypes;
         this.nativeKeyFunctions   = nativeKeyFunctions;

         this.parentsValidator      = this.getParentsValidator();
         this.nativeThingsValidator = this.getNativeThingsValidator();
      }
      //@formatter:on

      Predicate<GroveThing[]> getParentsValidator() {
         //@formatter:off
         return
            ( this.primaryRank > 1 )
               ? new Predicate<GroveThing[]>()
                 {
                    @Override
                    public boolean test(GroveThing[] parents)
                    {
                       if( !ParameterArray.validateNonNullAndSize
                              (
                                parents,
                                GroveThingInitializationRecord.this.primaryRank - 1,
                                GroveThingInitializationRecord.this.primaryRank - 1
                              )
                          )
                       {
                          return false;
                       }

                       for( int i = 0; i < GroveThingInitializationRecord.this.primaryRank - 1; i++ )
                       {
                          int j;
                          for( j = 0; j < GroveThingInitializationRecord.this.validPrimaryKeyTypes[i].length; j++ )
                          {
                             if( parents[i].isType( GroveThingInitializationRecord.this.validPrimaryKeyTypes[i][j] ) )
                             {
                                 break;
                             }
                          }
                          if( j >= GroveThingInitializationRecord.this.validPrimaryKeyTypes[i].length )
                          {
                             return false;
                          }
                       }

                       return true;
                    }
                 }
               : new Predicate<GroveThing[]>()
                 {

                    @Override
                    public boolean test(GroveThing[] parents)
                    {
                       return ParameterArray.validateNullOrEmpty( parents );
                    }
                 };
         //@formatter:on
      }

      Predicate<Object>[] getPrimaryKeyValidators() {
         //@formatter:off

         @SuppressWarnings("unchecked")
         Predicate<Object>[] primaryKeyValidators = new Predicate[this.primaryRank];

         for (int i = 0; i < this.primaryRank; i++)
         {

            var identifierTypesForRank = this.validPrimaryKeyTypes[i];

            if (identifierTypesForRank.length == 1)
            {

               primaryKeyValidators[i] = new Predicate<Object>()
               {

                  IdentifierType identifierType = identifierTypesForRank[0];

                  @Override
                  public boolean test(Object key)
                  {
                     return
                        Objects.nonNull( key )
                        && ( key instanceof Identifier )
                        && ((Identifier)key).getType().equals( this.identifierType );
                  }
               };

            }
            else
            {
               primaryKeyValidators[i] = new Predicate<Object>()
               {

                  IdentifierType[] identifierTypes = identifierTypesForRank;

                  @Override
                  public boolean test(Object key)
                  {
                     if( Objects.isNull(key) || !(key instanceof Identifier) )
                     {
                        return false;
                     }

                     var keyType = ((Identifier) key).getType();

                     for (int i = 0; i < this.identifierTypes.length; i++)
                     {
                        if (keyType.equals(this.identifierTypes[i]))
                        {
                           return true;
                        }
                     }

                     return false;
                  }
               };
            }
         }

         return primaryKeyValidators;
         //@formatter:on
      }

      Predicate<Object[]> getNativeThingsValidator() {
      //@formatter:off
         return
            Objects.nonNull( this.validNativeThings )
               ? new Predicate<Object[]>()
                 {
                    @Override
                    public boolean test(Object[] nativeThings)
                    {
                       if( !ParameterArray.validateNonNullAndSize
                              (
                                nativeThings,
                                GroveThingInitializationRecord.this.nativeRank,
                                GroveThingInitializationRecord.this.nativeRank
                              )
                          )
                       {
                          return false;
                       }

                       for( int i = 0; i < GroveThingInitializationRecord.this.nativeRank; i++ )
                       {
                          if( !GroveThingInitializationRecord.this.validNativeThings[i].isInstance( nativeThings[i] ) )
                          {
                             return false;
                          }
                       }

                       return true;
                    }
                 }
               : new Predicate<Object[]>()
                 {
                    @Override
                    public boolean test(Object[] nativeThings)
                    {
                       return
                          ParameterArray.validateNonNullAndSize
                             (
                                nativeThings,
                                GroveThingInitializationRecord.this.nativeRank,
                                GroveThingInitializationRecord.this.nativeRank
                             );
                    }
                 };
         //@formatter:on
      }

      Predicate<Object>[] getNativeKeyValidators() {
      //@formatter:off
         if (Objects.isNull( this.validNativeKeyTypes )) {
            return null;
         }

         @SuppressWarnings("unchecked")
         Predicate<Object>[] nativeKeyValidators = new Predicate[ this.nativeRank ];

         for (int i = 0; i < this.nativeRank; i++)
         {
            var keyClassForRank = this.validNativeKeyTypes[i];

            nativeKeyValidators[i] = new Predicate<Object>()
            {
               Class<?> keyClass = keyClassForRank;

               @Override
               public boolean test(Object key) {

                  //@formatter:off
                     return
                        Objects.nonNull( key )
                        && this.keyClass.isInstance( key );
                     //@formatter:on
               }
            };
         }

         return nativeKeyValidators;
         //@formatter:on
      }
   }

   //@formatter:off
   @SuppressWarnings({"unchecked", "unlikely-arg-type"})
   private static Map<IdentifierType,GroveThingInitializationRecord> groveThingInitializers =
      Map.ofEntries
         (
            Map.entry
               (
                  IdentifierType.ATTRIBUTE_DEFINITION,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.ATTRIBUTE_DEFINITION,
                            2,                                                     /* Primary Rank         */
                            1,                                                     /* Native Rank          */
                            false,                                                 /* Does not provide native keys */
                            IdentifierType.DATA_TYPE_DEFINITION::equals,           /* Link Type Validator  */
                            Map.of                                                 /* Link Rank Map        */
                               (
                                  IdentifierType.DATA_TYPE_DEFINITION, AbstractGroveThing.LinkRank.SCALAR
                               ),
                            new IdentifierType[][]                                 /* Allowed Primary Key Types  */
                            {
                               {                                                   /* High Order Key             */
                                 IdentifierType.SPECIFICATION_TYPE,
                                 IdentifierType.SPEC_OBJECT_TYPE,
                                 IdentifierType.SPEC_RELATION_TYPE
                               },
                               {                                                   /* Low Order Key              */
                                 IdentifierType.ATTRIBUTE_DEFINITION
                               }
                            },
                            new Class<?>[]                                         /* Valid Native Types         */
                            {
                               //IdentifierType.class,
                               //ArtifactTypeToken.class,
                               AttributeTypeToken.class
                            },
                            null,                                                  /* Attribute Definitions are not indexed by native keys */
                            null                                                   /* Native Key Functions */
                         )
               ),

            Map.entry
               (
                  IdentifierType.ATTRIBUTE_VALUE,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.ATTRIBUTE_VALUE,
                            2,                                                     /* Primary Rank         */
                            1,                                                     /* Native Rank          */
                            false,                                                 /* Does not provides native keys */
                            IdentifierType.ATTRIBUTE_DEFINITION::equals,           /* Link Type Validator  */
                            Map.of                                                 /* Link Rank Map        */
                               (
                                  IdentifierType.ATTRIBUTE_DEFINITION, AbstractGroveThing.LinkRank.SCALAR
                               ),
                            new IdentifierType[][]                                 /* Allowed Primary Key Types */
                            {
                               {                                                   /* High Order Key             */
                                  IdentifierType.SPECIFICATION,
                                  IdentifierType.SPECTER_SPEC_OBJECT,
                                  IdentifierType.SPEC_OBJECT,
                                  IdentifierType.SPEC_RELATION
                               },
                               {                                                   /* Low Order Key             */
                                  IdentifierType.ATTRIBUTE_VALUE
                               }
                            },
                            null,                                                  /* Any class of object can be saved as the Native Thing */
                            null,                                                  /* Attribute Values are not indexed by native keys */
                            null                                                   /* Native Key Functions */
                         )
               ),

            Map.entry
               (
                  IdentifierType.DATA_TYPE_DEFINITION,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.DATA_TYPE_DEFINITION,
                            1,                                                     /* Primary Rank         */
                            1,                                                     /* Native Rank          */
                            true,                                                  /* Provides Native Keys */
                            IdentifierType.ENUM_VALUE::equals,                     /* Link Type Validator  */
                            Map.of                                                 /* Link Rank Map        */
                            (
                               IdentifierType.ENUM_VALUE, AbstractGroveThing.LinkRank.VECTOR
                            ),
                            new IdentifierType[][]                                 /* Allowed Primary Key Types */
                            {
                               { IdentifierType.DATA_TYPE_DEFINITION }
                            },
                            new Class<?>[]                                         /* Valid Native Types   */
                            {
                               NativeDataTypeKey.class
                            },
                            new Class<?>[]                                         /* Allowed Native Key Classes */
                            {
                               NativeDataTypeKey.class
                            },
                            new Function[]                                         /* Native Key Functions */
                            {
                               ( nativeThing ) -> nativeThing
                            }
                         )
               ),

            Map.entry
               (
                  IdentifierType.ENUM_VALUE,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.ENUM_VALUE,
                            1,                                                     /* Primary Rank                        */
                            2,                                                     /* Native Rank                         */
                            true,                                                  /* Provides Native Keys                */
                            null,                                                  /* Link Type Validator                 */
                            Map.of(),                                              /* Link Rank Map                       */
                            new IdentifierType[][]                                 /* Allowed Primary Key Types           */
                            {
                               { IdentifierType.ENUM_VALUE }
                            },
                            new Class<?>[]                                         /* Valid Native Types                  */
                            {
                               AttributeTypeToken.class,
                               EnumToken.class
                            },
                            new Class<?>[]                                         /* Allowed Native Key Classes          */
                            {
                               Long.class,                                         /* Key class for AttributeTypeToken    */
                               Long.class                                          /* Key class for EnumToken             */
                            },
                            new Function[]                                         /* Native Key Functions                */
                            {
                               ( nativeThing ) -> ((Id) nativeThing).getId(),      /* Extract Key from AttributeTypeToken */
                               ( nativeThing ) -> ((Id) nativeThing).getId()       /* Extract Key from EnumToken          */
                            }
                         )
               ),

            Map.entry
               (
                  IdentifierType.HEADER,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.HEADER,
                            1,                                                     /* Primary Rank         */
                            1,                                                     /* Native Rank          */
                            false,                                                 /* Does not provides native keys, there is only one Header */
                            null,                                                  /* Link Type Validator  */
                            Map.of(),                                              /* Link Rank Map        */
                            new IdentifierType[][]                                 /* Allowed Primary Key Types */
                            {
                               { IdentifierType.HEADER }
                            },
                            new Class<?>[]                                         /* Valid Native Types   */
                            {
                               NativeHeader.class
                            },
                            null,                                                  /* Headers are not indexed by native keys */
                            null                                                   /* Native Key Functions */
                         )
               ),

            Map.entry
               (
                  IdentifierType.SPECIFICATION,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.SPECIFICATION,
                            1,                                                     /* Primary Rank         */
                            1,                                                     /* Native Rank          */
                            true,                                                  /* Provides Native Keys */
                            IdentifierType.SPECIFICATION_TYPE::equals,             /* Link Type Validator  */
                            Map.of                                                 /* Link Rank Map        */
                            (
                               IdentifierType.SPECIFICATION_TYPE, AbstractGroveThing.LinkRank.SCALAR
                            ),
                            new IdentifierType[][]                                 /* Allowed Primary Key Types */
                            {
                               { IdentifierType.SPECIFICATION }
                            },
                            new Class<?>[]                                         /* Valid Native Types   */
                            {
                               ArtifactReadable.class
                            },
                            new Class<?>[]                                         /* Allowed Native Key Classes */
                            {
                               Long.class
                            },
                            new Function[]                                         /* Native Key Functions */
                            {
                               ( nativeThing ) -> ((Id) nativeThing).getId()
                            }
                         )
                ),

               Map.entry
               (
                  IdentifierType.SPECTER_SPEC_OBJECT,
                  new GroveThingInitializationRecord
                         (
                            IdentifierType.SPECTER_SPEC_OBJECT,
                            1,                                                     /* Primary Rank         */
                            1,                                                     /* Native Rank          */
                            true,                                                  /* Provides Native Keys */
                            IdentifierType.SPEC_OBJECT_TYPE::equals,               /* Link Type Validator  */
                            Map.of                                                 /* Link Rank Map        */
                            (
                               IdentifierType.SPEC_OBJECT_TYPE, AbstractGroveThing.LinkRank.SCALAR
                            ),
                            new IdentifierType[][]                                 /* Allowed Primary Key Types */
                            {
                               {
                                  IdentifierType.SPECTER_SPEC_OBJECT
                               }
                            },
                            new Class<?>[]                                         /* Valid Native Types   */
                            {
                                  ArtifactReadable.class
                            },
                            new Class<?>[]                                         /* Allowed Native Key Classes */
                            {
                               Long.class
                            },
                            new Function[]                                         /* Native Key Functions */
                            {
                               ( nativeThing ) -> ((Id) nativeThing).getId()
                            }
                         )
                ),

             Map.entry
                (
                   IdentifierType.SPEC_OBJECT,
                   new GroveThingInitializationRecord
                          (
                             IdentifierType.SPEC_OBJECT,
                             3,                                                     /* Primary Rank         */
                             1,                                                     /* Native Rank          */
                             true,                                                  /* Provides Native Keys */
                             IdentifierType.SPEC_OBJECT_TYPE::equals,               /* Link Type Validator  */
                             Map.of                                                 /* Link Rank Map        */
                             (
                                IdentifierType.SPEC_OBJECT_TYPE, AbstractGroveThing.LinkRank.SCALAR
                             ),
                             new IdentifierType[][]                                 /* Allowed Primary Key Types */
                             {
                                {
                                   IdentifierType.SPECIFICATION
                                },
                                {
                                   IdentifierType.SPECIFICATION,
                                   IdentifierType.SPEC_OBJECT
                                },
                                {
                                   IdentifierType.SPECIFICATION,
                                   IdentifierType.SPEC_OBJECT
                                }
                             },
                             new Class<?>[]                                         /* Valid Native Types   */
                             {
                                   ArtifactReadable.class
                             },
                             new Class<?>[]                                         /* Allowed Native Key Classes */
                             {
                                Long.class
                             },
                             new Function[]                                         /* Native Key Functions */
                             {
                                ( nativeThing ) -> ((Id) nativeThing).getId()
                             }
                          )
                 ),

              Map.entry
                 (
                    IdentifierType.SPEC_RELATION,
                    new GroveThingInitializationRecord
                           (
                              IdentifierType.SPEC_RELATION,
                              1,                                                     /* Primary Rank         */
                              3,                                                     /* Native Rank          */
                              true,                                                  /* Provides Native Keys */
                              ( linkType ) ->                                        /* Link Type Validator  */
                                    IdentifierType.SPEC_RELATION_TYPE.equals( linkType )
                                 || IdentifierTypeGroup.RELATABLE_OBJECT.equals( linkType ),
                              Map.of                                                 /* Link Rank Map        */
                              (
                                    IdentifierType.SPEC_RELATION_TYPE,    AbstractGroveThing.LinkRank.SCALAR,
                                    IdentifierTypeGroup.RELATABLE_OBJECT, AbstractGroveThing.LinkRank.VECTOR
                              ),
                              new IdentifierType[][]                                 /* Allowed Primary Key Types  */
                              {
                                 { IdentifierType.SPEC_RELATION }
                              },
                              new Class<?>[]                                         /* Valid Native Types   */
                              {
                                 ArtifactReadable.class,                             /* Side A related ArtifactReadable */
                                 ArtifactReadable.class,                             /* Side B related ArtifactReadable */
                                 ArtifactReadable.class                              /* Fake ArtifactReadable for the relationship */
                              },
                              new Class<?>[]                                         /* Allowed Native Key Classes */
                              {
                                 Long.class,
                                 Long.class,
                                 Long.class
                              },
                              new Function[]                                         /* Native Key Functions */
                              {
                                 ( nativeThing ) -> ((Id) nativeThing).getId(),
                                 ( nativeThing ) -> ((Id) nativeThing).getId(),
                                 ( nativeThing ) -> ((Id) nativeThing).getId()
                              }
                           )
                 ),

              Map.entry
                 (
                    IdentifierType.SPECIFICATION_TYPE,
                    new GroveThingInitializationRecord
                           (
                              IdentifierType.SPECIFICATION_TYPE,
                              1,                                                     /* Primary Rank         */
                              1,                                                     /* Native Rank          */
                              true,                                                  /* Provides Native Keys */
                              IdentifierType.ATTRIBUTE_DEFINITION::equals,           /* Link Type Validator  */
                              Map.of                                                 /* Link Rank Map        */
                              (
                                 IdentifierType.ATTRIBUTE_DEFINITION, AbstractGroveThing.LinkRank.MAP
                              ),
                              new IdentifierType[][]                                 /* Allowed Primary Key Types  */
                              {
                                 { IdentifierType.SPECIFICATION_TYPE }
                              },
                              new Class<?>[]                                         /* Valid Native Types   */
                              {
                                 ArtifactTypeToken.class
                              },
                              new Class<?>[]                                         /* Allowed Native Key Classes */
                              {
                                 Long.class
                              },
                              new Function[]                                         /* Native Key Functions */
                              {
                                 ( nativeThing ) -> ((Id) nativeThing).getId()
                              }
                           )
                  ),

              Map.entry
                 (
                    IdentifierType.SPEC_OBJECT_TYPE,
                    new GroveThingInitializationRecord
                           (
                              IdentifierType.SPEC_OBJECT_TYPE,
                              1,                                                     /* Primary Rank         */
                              1,                                                     /* Native Rank          */
                              true,                                                  /* Provides Native Keys */
                              IdentifierType.ATTRIBUTE_DEFINITION::equals,           /* Link Type Validator  */
                              Map.of                                                 /* Link Rank Map        */
                              (
                                 IdentifierType.ATTRIBUTE_DEFINITION, AbstractGroveThing.LinkRank.MAP
                              ),
                              new IdentifierType[][]                                 /* Allowed Primary Key Types  */
                              {
                                 { IdentifierType.SPEC_OBJECT_TYPE }
                              },
                              new Class<?>[]                                         /* Valid Native Types   */
                              {
                                    ArtifactTypeToken.class
                              },
                              new Class<?>[]                                         /* Allowed Native Key Classes */
                              {
                                 Long.class
                              },
                              new Function[]                                         /* Native Key Functions */
                              {
                                 ( nativeThing ) -> ((Id) nativeThing).getId()
                              }
                           )
                 ),

              Map.entry
                 (
                    IdentifierType.SPEC_RELATION_TYPE,
                    new GroveThingInitializationRecord
                           (
                              IdentifierType.SPEC_RELATION_TYPE,
                              1,                                                     /* Primary Rank         */
                              1,                                                     /* Native Rank          */
                              true,                                                  /* Provides Native Keys */
                              IdentifierType.ATTRIBUTE_DEFINITION::equals,           /* Link Type Validator  */
                              Map.of                                                 /* Link Rank Map        */
                              (
                                    IdentifierType.ATTRIBUTE_DEFINITION, AbstractGroveThing.LinkRank.MAP
                              ),
                              new IdentifierType[][]                                 /* Allowed Primary Key Types  */
                              {
                                 { IdentifierType.SPEC_RELATION_TYPE }
                              },
                              new Class<?>[]                                         /* Valid Native Types   */
                              {
                                 ArtifactTypeToken.class
                              },
                              new Class<?>[]                                         /* Allowed Native Key Classes */
                              {
                                 Long.class
                              },
                              new Function[]                                         /* Native Key Functions */
                              {
                                 ( nativeThing ) -> ((Id) nativeThing).getId()
                              }
                           )
                 )
         );
   //@formatter:on

   /**
    * A {@link Map} of the Synchronization Artifact {@link Grove} objects by {@link IdentifierType}.
    */

   private final Map<IdentifierType, Grove> groveMap;

   /**
    * The factory for creating {@link GroveThing} identifiers.
    */

   private final IdentifierFactory identifierFactory;

   /**
    * The direction (export or import) of the operation.
    */

   private final Direction direction;

   /**
    * Saves the {@link Forest} {@link Identifier}.
    */

   private final Identifier identifier;

   /**
    * Constructor creates the data structures for a new Synchronization Artifact. Each Synchronization Artifact export
    * or import should use it's own {@link Forest}.
    *
    * @param direction the Synchronization Artifact operation is either export or import.
    * @throws NullPointerException when the parameter <code>direction</code> is <code>null</code>.
    */

   public Forest(Direction direction) {

      Objects.requireNonNull(direction, "Forest::new, parameter \"direction\" is null.");

      var groveMap = new LinkedHashMap<IdentifierType, Grove>() {

         /**
          * Serialization identifier
          */

         private static final long serialVersionUID = 1L;

         /**
          * Extracts the grove type from the grove and uses that as the map key to store the grove by.
          *
          * @param grove the {@link Grove} to be saved in the map.
          */

         public void put(Grove grove) {
            this.put(grove.getType(), grove);
         }
      };

      this.groveMap = groveMap;

      /*
       * This order is important, the groves will be processed in the order below for for the conversion of grove
       * things.
       */

      groveMap.put(Forest.createGrove(IdentifierType.HEADER));
      groveMap.put(Forest.createGrove(IdentifierType.ENUM_VALUE));
      groveMap.put(Forest.createGrove(IdentifierType.DATA_TYPE_DEFINITION));
      groveMap.put(Forest.createGrove(IdentifierType.SPECIFICATION_TYPE));
      groveMap.put(Forest.createGrove(IdentifierType.SPEC_OBJECT_TYPE));
      groveMap.put(Forest.createGrove(IdentifierType.SPEC_RELATION_TYPE));
      groveMap.put(Forest.createGrove(IdentifierType.ATTRIBUTE_DEFINITION));
      groveMap.put(Forest.createGrove(IdentifierType.SPECIFICATION));
      groveMap.put(Forest.createGrove(IdentifierType.SPECTER_SPEC_OBJECT));
      groveMap.put(Forest.createGrove(IdentifierType.SPEC_OBJECT));
      groveMap.put(Forest.createGrove(IdentifierType.SPEC_RELATION));
      groveMap.put(Forest.createGrove(IdentifierType.ATTRIBUTE_VALUE));

      this.direction = direction;
      this.identifierFactory = new IdentifierFactory(direction.getIdentifierFactoryType());
      //@formatter:off
      this.identifier = Direction.EXPORT.equals( direction )
                           ? this.identifierFactory.createIdentifier(IdentifierType.FOREST)
                           : this.identifierFactory.createIdentifier(IdentifierType.FOREST, "F-0" );
      //@formatter:on
   }

   /**
    * Creates a new {@link Grove} implementation for the specified {@link IdentifierType}.
    *
    * @param identifierType the type of {@link Grove} implementation to create.
    * @return the created {@link Grove} implementation.
    */

   private static Grove createGrove(IdentifierType identifierType) {

      var groveThingInitializer = Forest.groveThingInitializers.get(identifierType);

      //@formatter:off
      return
         new AbstractMapGrove
                (
                   identifierType,
                   groveThingInitializer.providesNativeKeys,
                   groveThingInitializer.getPrimaryKeyValidators(),
                   groveThingInitializer.getNativeKeyValidators()
                );
      //@formatter"on
   }

   /**
    * For Synchronization Artifact exports, creates a new {@link GroveThing} for the specified {@link IdentifierType} with a unique {@link Identifier}.
    *
    * @param identifierType the type of {@link GroveThing} to create.
    * @param parents the hierarchical parent of the {@link GroveThing} to be created.
    * @return a new {@link GroveThing}.
    * @throws IllegalStateException when the {@link Forest} was created with a {@link Direction} other than {@link Direction#EXPORT}.
    */

   public GroveThing createGroveThing(IdentifierType identifierType, GroveThing... parents) {

      if (!Direction.EXPORT.equals(this.direction)) {
         throw new IllegalStateException();
      }

      var groveThingInitializer = Forest.groveThingInitializers.get(identifierType);

      //@formatter:off
      return
         new AbstractGroveThing
               (
                 this.identifierFactory.createIdentifier(identifierType),
                 groveThingInitializer.primaryRank,
                 groveThingInitializer.nativeRank,
                 groveThingInitializer.providesNativeKeys,
                 groveThingInitializer.linkTypeValidator,
                 groveThingInitializer.linkRank,
                 groveThingInitializer.parentsValidator,
                 groveThingInitializer.nativeThingsValidator,
                 groveThingInitializer.nativeKeyFunctions,
                 parents
               );
      //@formatter:on
   }

   /**
    * For Synchronization Artifact imports, creates a new {@link GroveThing} for the specified {@link IdentifierType}
    * with a unique {@link Identifier}.
    *
    * @param foreignThingFamily a {@link ForeignThingFamily} containing the foreign thing, its identifier and the
    * identifiers of it's hierarchical parents.
    * @return a new {@link GroveThing}.
    */

   public GroveThing createGroveThingFromForeignThing(IdentifierType groveIdentifierType, ForeignThingFamily foreignThingFamily) {

      if (!Direction.IMPORT.equals(this.direction)) {
         throw new IllegalStateException();
      }

      var stringForeignKeys = foreignThingFamily.getForeignIdentifiersAsStrings();
      var identifierTypes = foreignThingFamily.getIdentifierTypes();
      var foreignThing = foreignThingFamily.getChild();

      //@formatter:off
      if(    Objects.isNull( stringForeignKeys )
          || ( stringForeignKeys.length <= 0 )
          || Objects.isNull( identifierTypes )
          || ( identifierTypes.length != stringForeignKeys.length )
          || Objects.isNull( foreignThing ) ) {
         throw new CreateGroveThingFromForeignThingException( "bad foreign thing record", foreignThingFamily );
      }
      //@formatter:on

      var count = stringForeignKeys.length;
      var parents = new GroveThing[count - 1];
      var groveThingIdentifierType = identifierTypes[count - 1];

      if (groveThingIdentifierType != groveIdentifierType) {
         var grove = this.getGrove(groveThingIdentifierType);
         var groveThingForeignIdentifierString = stringForeignKeys[count - 1];
         var primaryIdentifier = this.getPrimaryIdentifierByForeignIdentifierString(groveThingIdentifierType,
            groveThingForeignIdentifierString).get();
         var groveThing = grove.getByUniquePrimaryKey(primaryIdentifier).get();

         return groveThing;
      }

      for (var i = 0; i < count - 1; i++) {
         var identifierType = identifierTypes[i];
         var stringForeignKey = stringForeignKeys[i];
         //@formatter:off
         var primaryIdentifier =
            this.getPrimaryIdentifierByForeignIdentifierString(identifierType, stringForeignKey)
               .orElseThrow
                  (
                     () -> new CreateGroveThingFromForeignThingException
                                  (
                                     "failed to get primary identifier from foreign thing identifier",
                                     stringForeignKey,
                                     foreignThingFamily
                                  )
                  );

         parents[ i ] =
            this.getGrove(identifierType).getByUniquePrimaryKey(primaryIdentifier)
               .orElseThrow
                  (
                     () -> new CreateGroveThingFromForeignThingException
                                  (
                                     "grove thing not found for foreign thing identifier",
                                     stringForeignKey,
                                     foreignThingFamily
                                  )
                  );
         //@formatter:on
      }

      var groveThingInitializer = Forest.groveThingInitializers.get(groveThingIdentifierType);

      //@formatter:off
      var groveThing =
         new AbstractGroveThing
               (
                 this.identifierFactory.createIdentifier(groveThingIdentifierType,stringForeignKeys[count-1]),
                 groveThingInitializer.primaryRank,
                 groveThingInitializer.nativeRank,
                 groveThingInitializer.providesNativeKeys,
                 groveThingInitializer.linkTypeValidator,
                 groveThingInitializer.linkRank,
                 groveThingInitializer.parentsValidator,
                 groveThingInitializer.nativeThingsValidator,
                 groveThingInitializer.nativeKeyFunctions,
                 parents
               );
      //@formatter:on

      groveThing.setForeignThing(foreignThing);

      return groveThing;
   }

   /**
    * Gets the {@link Grove} for the Synchronization Artifact {@link GroveThing}s specified by
    * <code>identifierType</code>.
    *
    * @param identifierType specifies the {@link Grove} to get.
    * @return the {@link Grove} for the specified {@link GroveThings}.
    */

   public Grove getGrove(IdentifierType identifierType) {
      return this.groveMap.get(identifierType);
   }

   /**
    * Gets the {@link Forest} {@link Identifier}.
    *
    * @return the forest {@link Identifier}.
    */

   public Identifier getIdentifier() {
      return this.identifier;
   }

   /**
    * Looks up the primary {@link Identifier} associated with the provided string representation of a foreign thing
    * identifier. A {@link GroveThing} is created to contain each foreign thing from an imported Synchronization
    * Artifact. A primary {@link Identifier} is created for each {@link GroveThing} from the string representation of
    * the contained foreign thing's identifier.
    *
    * @param identifierType the type of primary {@link Identifier} to look up.
    * @param foreignIdentifierString the string representation of the foreign thing's identifier.
    * @return when an primary {@link Identifier} of the specified {@link IdentifierType} is associated with the provided
    * foreign thing identifier, an {@link Optional} containing the associated primary {@link Identifier}; otherwise, an
    * empty {@link Optional}.
    */

   public Optional<Identifier> getPrimaryIdentifierByForeignIdentifierString(IdentifierType identifierType, String foreignIdentifierString) {
      return this.identifierFactory.getPrimaryIdentifierByForeignIdentifierString(identifierType,
         foreignIdentifierString);
   }

   /**
    * Returns an ordered stream of the {@link Grove} implementations in the {@link Forest}.
    *
    * @return a {@link Stream} of the {@link Grove}s in the {@link Forest}.
    */

   public Stream<Grove> stream() {
      return this.groveMap.values().stream();
   }

   /**
    * Returns an unordered {@link Stream} of the {@link GroveThing}s in the {@link Grove}s specified by the provided
    * {@link IdentifierType}s.
    *
    * @param groveIdentifierTypes an array of the {@link IdentifierType}s for the {@link Grove}s to be streamed.
    * @return a {@link Stream} of the {@link GroveThing}s in the specified {@link Grove}s.
    */

   public Stream<GroveThing> streamGroves(IdentifierType... groveIdentifierTypes) {

      //@formatter:off
      assert
              Objects.nonNull( groveIdentifierTypes )
           && ( groveIdentifierTypes.length > 0 )
         : "Forest::streamGroves, no groves specified.";
      //@formatter:on

      return Arrays.stream(groveIdentifierTypes).map(this::getGrove).flatMap(Grove::stream);
   }

   /**
    * Returns an unordered {@link Stream} of the {@link GroveThing}s in the {@link Grove}s specified by the provided
    * {@link StreamEntry}s. If a {@link StreamEntry} contains a filter {@link Predicate}, it will be used to filter the
    * {@link GroveThing}s from the {@link Grove} specified by the {@link StreamEntry}'s {@link IdentifierType}.
    *
    * @param streamEntries an array of the {@link StreamEntry}s for the {@link Grove}s to be streamed.
    * @return a {@link Stream} of the {@link GroveThing}s, possibly filtered, in the specified {@link Grove}s.
    */

   public Stream<GroveThing> streamGroves(StreamEntry... streamEntries) {
      //@formatter:off
      assert
              Objects.nonNull( streamEntries )
           && ( streamEntries.length > 0 )
         : "Forest::streamGroves, no groves specified.";
      //@formatter:on

      //@formatter:off
      return
         Arrays.stream( streamEntries )
            .flatMap
               (
                  (streamEntry) ->
                  {
                     var grove = this.groveMap.get(streamEntry.getIdentifierType());

                     return
                        streamEntry.hasFilter()
                           ? grove.stream().filter( streamEntry.getFilter() )
                           : grove.stream();
                  }
               );
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( name )
         .indentInc()
         .segment( "Direction", this.direction )
         .title( "Grove Map" )
         .indentInc()
         ;

      this.groveMap.values().stream()
         .forEach
            (
               ( grove ) -> outMessage
                               .blank()
                               .segment( "Type",        grove.getType()    )
                               .segment( "Grove Size",  grove.size()       )
                               .segment( "Native Rank", grove.nativeRank() )
                               .segment( "Rank",        grove.rank()       )
            );

      return outMessage;
   }

    /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

}

/* EOF */
